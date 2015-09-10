package com.anykey.uaspec.data;


import com.anykey.uaspec.utils.Globals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by User on 014 14.06.15.
 */
public class DataValuesHolder {
    private ArrayList<DataValues> dataValues = new ArrayList<>();

    public void getNew(){
        if (dataValues.size()>=65)
            dataValues.remove(0);
        dataValues.trimToSize();
        System.out.println(dataValues.size());

        Globals.getData().renewData();
        dataValues.add(new DataValues(Globals.getData()));
    }

    /* returns last data CSVString */
    public String getCSV(){
        return getCSV(dataValues.size()-1);
    }

    /* returns specified data CSVString*/
    public String getCSV(int index){
        if (dataValues.isEmpty()) {
            getNew();
            return dataValues.get(0).CSVString;
        }
        return dataValues.get(index).CSVString;
    }

    public LinkedHashMap<Integer, Double> getIntensityMap(){
        return getIntensityMap(dataValues.size() - 1);
    }

    public LinkedHashMap<Integer, Double> getIntensityMap(int index){
        if (dataValues.isEmpty()) {
            getNew();
            return dataValues.get(0).intensityMap;
        }
        return  dataValues.get(index).intensityMap;
    }
}
