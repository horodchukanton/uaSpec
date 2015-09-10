package com.anykey.uaspec.data;

import java.util.LinkedHashMap;

/**
 * Created by User on 014 14.06.15.
 */
public class DataValues {
    String CSVString;

    private double[] arrayX;
    private double[] arrayY;

    LinkedHashMap<Integer, Double> intensityMap;

    double average = 0.0;

    public DataValues(Data data){
        arrayX = data.getDataArrayX();
        arrayY = data.getDataArrayY();
        CSVString = data.getCSV();

        intensityMap = data.getIntensityMap();
        calcAverage();
    }

    private double calcAverage(){
        double result = 0.0;
        int counter = 0;
        for (double x : arrayY){
            result+=x;
            counter++;
        }
        return result/counter;
    }
}
