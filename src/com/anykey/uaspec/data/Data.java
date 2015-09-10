package com.anykey.uaspec.data;

import com.anykey.uaspec.communicator.Communicator;
import com.anykey.uaspec.utils.Globals;
import jssc.SerialPortException;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Anton Horodchuk on 013 13.05.15.
 * Analyzer holds and serves data from spectrometer
 */
public class Data {
    public static double[] BKGArray = new double[2100];
    private double[] dataArrayY = null;
    private double[] dataArrayX = new double[2099];

    public static final int NOISE_FLOOR = 305;

    private LinkedHashMap<Integer, Double> data;

    public Data() {
        calibrateX();
        flushZeroLevel();
    }


    private LinkedHashMap<Integer, Double> formHashMap() {
        return formHashMap(dataArrayX, dataArrayY);
    }

    private LinkedHashMap<Integer, Double> formHashMap(double[] dataX, double[] dataY) {
        long millis = System.currentTimeMillis();

        Double minDouble = dataX[0];
        Double maxDouble = dataX[dataY.length - 1];

        int min = minDouble.intValue();
        int max = maxDouble.intValue();

        int numOfWavelengths = max - min -1;

        LinkedHashMap<Integer, Double> result = new LinkedHashMap<>(numOfWavelengths - 1);

        int base = min - 1;
        int iterator = 0;
        int prevIterator;

        Double currX;
        Double valueY;

        double sum;
        while (iterator < dataY.length - 2) {
            sum = 0;
            base++;

            prevIterator = iterator;
            do {
                currX = dataX[iterator];
                valueY = dataY[iterator++];
                sum += Math.abs(valueY);
            } while (Math.abs(base - currX.intValue()) == 0);
            Integer key = currX.intValue();
            Double value = sum / (iterator - prevIterator);
            value = (double) Math.round(value);//(value * 1000)/1000;
            result.put(key, value);

        }
        if (Globals.isDebug())
            System.out.println("  time to make a map: " + (System.currentTimeMillis() - millis));
        return result;
    }

    public void calibrateX() {
        final double cc0 = 328.7556985;
        final double cc1 = 0.30987359;
        final double cc2 = -1.745882e-5;

        for (int i = 0; i < dataArrayX.length; i++) {
            dataArrayX[i] = cc0 + cc1 * i + cc2 * i * i;
        }
    }

    public void setZeroLevel() {
        assertHasData();
        for (int i = 0; i < dataArrayY.length; i++) {
            BKGArray[i] = dataArrayY[i] + NOISE_FLOOR;
        }
    }

    public void flushZeroLevel() {
        Arrays.fill(BKGArray, NOISE_FLOOR);
    }

    public void calibrateZeroLevel() {
        for (int i = 0; i < dataArrayY.length; i++) {
            dataArrayY[i] = dataArrayY[i] - BKGArray[i];
        }
    }

    public double[] getDataArrayY() {
        return dataArrayY;
    }

    public void setDataArrayY(double[] data) {
        dataArrayY = data;
        calibrateZeroLevel();
    }

    public double[] getDataArrayX() {
        return dataArrayX;
    }

    public double[] getPoint(int index) {
        assertHasData();
        if (index < dataArrayY.length) {
            return new double[]{dataArrayY[index], dataArrayX[index]};
        } else {
            return new double[]{0, 0};
        }
    }

    public double getIntensity(int wavelength) {
        assertHasData();
        data = formHashMap(dataArrayX, dataArrayY);
        //System.out.println(data);
        if (wavelength >= Communicator.MIN_WAVELENGTH && wavelength <= Communicator.MAX_WAVELENGTH)
            return data.get(wavelength);
        else return 0.0;
    }


    public synchronized double[][] getPoints() {
        assertHasData();
        double[][] result = new double[dataArrayY.length][2];
        for (int i = 0; i < dataArrayY.length; i++) {
            result[i] = getPoint(i);
        }
        return result;
    }

    public void assertHasData() {
        if (dataArrayY == null) renewData();
    }

    public int getDataLength() {
        assertHasData();
        return dataArrayY.length;
    }

    public LinkedHashMap<Integer, Double> getIntensityMap() {
        assertHasData();
        return formHashMap();
    }

    public void renewData() {
        long millis = System.currentTimeMillis();
        try {
            setDataArrayY(Globals.getCommunicator().getOnce());
        } catch (SerialPortException e) {
            System.err.println("error getting data");
        }
        if (Globals.isDebug())
            System.out.println("  time to renew data: " + (System.currentTimeMillis() - millis));
    }

    public String getCSV() {
        renewData();
        StringBuilder sb = new StringBuilder();

        data = formHashMap(dataArrayX, dataArrayY);
        sb.append("x, y,\n");
        for (Map.Entry e : data.entrySet()) {
            //System.out.println(e);
            sb.append(e.getKey() + "," + Integer.parseInt(e.getValue().toString()) + "\n");
        }
        sb.deleteCharAt(sb.toString().length() - 1);

        return sb.toString();
    }

    public static void main(String[] args) {
        Globals.setPortName("COM5");
        Data stanalyzer = new Data();
        try {
            stanalyzer.setDataArrayY(Globals.getCommunicator().getOnce());
        } catch (SerialPortException e) {
            e.printStackTrace();
        }

        System.out.println("value of 392: " + stanalyzer.getIntensity(392));

        System.exit(1);
    }
}
