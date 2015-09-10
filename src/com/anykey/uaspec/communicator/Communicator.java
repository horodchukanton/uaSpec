package com.anykey.uaspec.communicator;

import com.anykey.uaspec.utils.Globals;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Created by Anton Horodchuk on 007 07.05.15.
 *
 */

public class Communicator {

    public static final byte[] COMMAND_GETSN = {(byte) 0xFE, (byte) 0x64};
    public static final byte[] COMMAND_END = {(byte) 0xFE, (byte) 0x00};

    @Deprecated
    public static final byte[] COMMAND_GETONCE = { //FE 01 00 10 00 04
            (byte) 0xFE, (byte) 0x01,  //mode
            (byte) 0, (byte) 16,  //aver = 16
            (byte) 0, (byte) 4   //expo = 10 ms (4*2,5)
    };

    public static final int MIN_WAVELENGTH = 329;
    public static final int MAX_WAVELENGTH = 898;

    private static final byte COMMAND_START = (byte) 0xFE;
    private static final byte MODE_NORMAL = (byte) 1;

    private static final byte ZERO = (byte) 0;

    private byte framesAveraged = (byte) 10;
    private byte exposition = (byte) 4;

    private static final short MAX_VALUE = 3600;

    final static double MIN_EXPO = 2.5;
    final static int NUM_OF_ACTIVE_PIXELS = 2048;
    final static int DATA_COUNT = 2100;
    final static int NDATA = DATA_COUNT - 1;
    final static int LDATA = 34;
    final static int RDATA = LDATA - 1 + NUM_OF_ACTIVE_PIXELS;
    final static int LBLACK = LDATA - 3 - 17;
    final static int RBLACK = LDATA - 3;

    private static Communicator self;
    private SerialPort port;
    private String comPacket = "";
    private boolean hasData;
    final static int MASK = SerialPort.MASK_RXCHAR + SerialPort.MASK_TXEMPTY + SerialPort.MASK_ERR;

    int times = 0;

    public Communicator() throws SerialPortException {
        openPort();
        self = this;
    }

    public byte getExposition() {
        return exposition;
    }

    public void setExposition(byte exposition) {
        this.exposition = exposition;
        System.out.println("Exposition set to: " + exposition);
    }

    public byte getFramesAveraged() {
        return framesAveraged;
    }

    public void setFramesAveraged(byte framesAveraged) {
        this.framesAveraged = framesAveraged;
        System.out.println("Averaged set to: " + framesAveraged);
    }

    public static double toDouble(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }

    public static double toInteger(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    public static double toChar(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getChar();
    }

    private synchronized double[] getData() throws SerialPortException {
        double[] result = new double[NDATA];

        String data = getDataString();
        data = data.substring(3, data.length() - 2);
        //System.out.println(data);
        //System.out.println(comPacket);
        if (Globals.isDebug())
            System.out.println("Packet length: " + data.length());


        //***********AVER***********************
        int averTemp1 = data.charAt(1);
        int averTemp2 = data.charAt(2);
        double aver = Integer.rotateLeft(averTemp1, 8) + averTemp2;
        if (aver == 0) aver = 1;
        assert (framesAveraged == (byte) aver);
        if (Globals.isDebug())
            System.out.println("aver: " + aver);

        //************EXPO**********************
        int expoTemp1 = ((data.charAt(3) << 8) & 0xff00);
        int expoTemp2 = data.charAt(4) & 0x00ff;
        double expo = MIN_EXPO * (Integer.rotateLeft(expoTemp1, 8) + expoTemp2);
        assert (exposition == (byte) expo);
        if (Globals.isDebug())
            System.out.println("expo: " + expo);

        //***********Data**********************
        for (int i = 0; i < NDATA; i++) {
            if (!(2 * i + 2 + 4 >= data.length())) {

                int resultTemp1 = (data.charAt(2 * i + 1 + 4));
                int resultTemp2 = (data.charAt(2 * i + 2 + 4));
                result[i] = (((resultTemp1 << 8) & 0xFF00) | (resultTemp2 & 0x00FF)) / aver;
            }
        }
        //***********BLACK*********************
        double blackData = 0;
        for (int i = LBLACK; i < RBLACK; i++) {
            blackData += result[i];
            blackData = blackData / (RBLACK - LBLACK + 1);
        }
        if (Globals.isDebug())
            System.out.println("black data: " + blackData);

        clearData();

        times = 0;

        return result;
    }

    private synchronized String getDataString() {
        return comPacket;
    }

    private void clearData() {
        comPacket = "";
        hasData = false;
    }

    private synchronized void sendCommand(byte[] command) throws SerialPortException {
        port.writeBytes(command);
    }

    private boolean hasData() {
        return hasData;
    }

    private void waitForData() {
        long millis = System.currentTimeMillis();

        long time = Math.round(framesAveraged * Math.round(exposition * 2.5) * 1.1);
        time = (time < 25 ? 25 : time);
        do {  //Поки немає даних
            try {
                Thread.sleep(time);  //Чекати  мінімальний час, потрібний спектрометру для формування кадру
//                System.out.println("time:" + counter++*time + " length:" +comPacket.length() + "; isEnd:" + comPacket.endsWith(END_OF_PACKET));
                if (comPacket.length() == 4214) hasData = true;
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        } while (!hasData);
        if (Globals.isDebug())
            System.out.println("Time waited for data: " + (System.currentTimeMillis() - millis));
    }

    public void openPort() throws SerialPortException {
        port = new SerialPort(Globals.getPortName());
        port.openPort();
        port.setParams(SerialPort.BAUDRATE_115200,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);
        port.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
                SerialPort.FLOWCONTROL_RTSCTS_OUT);
        port.addEventListener(new PortReader(), MASK);
    }

    public synchronized double[] getOnce() throws SerialPortException {
        return getOnce(framesAveraged, exposition); //USING SAVED AVERAGE & EXPO
    }

    public synchronized double[] getOnce(byte aver, byte expo) throws SerialPortException {
        //long millis = System.currentTimeMillis();
        byte[] command = {
                COMMAND_START, MODE_NORMAL, //FE 01
                ZERO, aver,
                ZERO, expo
        };
        sendCommand(command);
        waitForData();
        double[] result = getData();
        //System.out.println("profiling getOnce: " + (System.currentTimeMillis()-millis));
        return result;
    }

    public synchronized String getSpecInfo() throws SerialPortException {
        sendCommand(COMMAND_GETSN);
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String result = "SN";
        try {
            result = getDataString().substring(10);
        } catch (StringIndexOutOfBoundsException ex) {
            //
        }
        clearData();
        return result;
    }

    public void closePort() throws SerialPortException {
        port.closePort();
    }

    private class PortReader implements SerialPortEventListener {

        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    byte[] bytes = port.readBytes(event.getEventValue());
                    Charset charset = Charset.forName("ISO-8859-1");
                    String data = new String(bytes, charset);
                    if (data.length() == 22) { //SpecInfo
                        synchronized (comPacket) {
                            comPacket = data;
                        }
                        setReady();
                        return;
                    }
                    if (data.startsWith("aaaa")) { //StartOfPacket
                        comPacket = data;

                    } else {
                        comPacket = comPacket.concat(data);
                    }

                    if (comPacket.length() >= 4214) { //Sometimes package is smaller. TODO: check is it a charset problem;
                        setReady();
                    }
                } catch (SerialPortException ex) {
                    System.out.println(ex);
                }
            }
        }

        public void setReady() {
            try {
                port.purgePort(SerialPort.PURGE_RXCLEAR);
                port.purgePort(SerialPort.PURGE_TXCLEAR);
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
            hasData = true;
        }
    }


}


