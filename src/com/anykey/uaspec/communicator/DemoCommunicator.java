package com.anykey.uaspec.communicator;

import com.anykey.uaspec.utils.Globals;
import jssc.SerialPortException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Anton Horodchuk on 016 16.05.15.
 *
 */
public class DemoCommunicator extends Communicator {
    private String comPacket = "";

    public DemoCommunicator() throws SerialPortException {
        byte [] fileData = null;
        try {
            Path p = FileSystems.getDefault().getPath("testPacket.txt");
            fileData = Files.readAllBytes(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Charset charset = Charset.forName("ISO-8859-1");
        if (fileData != null) {
            comPacket = new String(fileData, charset);
        }
    }

    private synchronized double[] getData() throws SerialPortException {
        double[] result = new double[NDATA];

        String data = comPacket;
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

        if (Globals.isDebug())
            System.out.println("aver: " + aver);

        //************EXPO**********************
        int expoTemp1 = ((data.charAt(3) << 8) & 0xff00);
        int expoTemp2 = data.charAt(4) & 0x00ff;
        double expo = MIN_EXPO * (Integer.rotateLeft(expoTemp1, 8) + expoTemp2);

        if (Globals.isDebug())
            System.out.println("expo: " + expo);

        //***********Data**********************
        for (int i = 0; i < NDATA; i++) {
            if (!(2 * i + 2 + 4 >= data.length())) {

                int resultTemp1 = (data.charAt(2 * i + 1 + 4));
                int resultTemp2 = (data.charAt(2 * i + 2 + 4));

                result[i]= (((resultTemp1 << 8) & 0xFF00) | (resultTemp2 & 0x00FF));

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

        return result;
    }

    @Override
    public void openPort() {
        //no-op
    }

    @Override
    public void closePort() {
        //no-op
    }

    public double[] getOnce() {
        try {
            Thread.sleep(300);
            return getData();
        } catch (SerialPortException e) {
            e.printStackTrace();
        } catch (InterruptedException ex){
            //
        }
        return new double[]{0.0}; //test
    }

    @Override
    public String getSpecInfo() {
        return "#Demo mode";
    }
}
