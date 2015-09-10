package com.anykey.uaspec.utils;

import com.anykey.uaspec.data.Data;
import com.anykey.uaspec.data.DataValuesHolder;
import com.anykey.uaspec.LevelAnalyzer;
import com.anykey.uaspec.communicator.Communicator;
import com.anykey.uaspec.communicator.CommunicatorFactory;

/**
 * Created by Anton Horodchuk on 014 14.05.15.
 */
public class Globals {
    private static String portName = "";
    private static String webRoot = "";
    private static boolean debug = false;

    private static Communicator mainCommunicator;
    private static Data mainData;
    private static LevelAnalyzer levelAnalyzer;
    private static DataValuesHolder dataValuesHolder;

    public static LevelAnalyzer getLevelAnalyzer() {
        if (levelAnalyzer == null) levelAnalyzer = new LevelAnalyzer();
        return levelAnalyzer;
    }

    public static String getPortName() {
        return portName;
    }

    public static void setPortName(String portName) {
        Globals.portName = portName;
        mainCommunicator = CommunicatorFactory.getCommunicator();
        mainData = new Data();
    }

    public static String getWebRoot() {
        return webRoot;
    }

    public static void setWebRoot(String webRoot) {
        Globals.webRoot = webRoot;
    }

    public static Communicator getCommunicator() {
        return mainCommunicator;
    }

    public static void setCommunicator(Communicator communicator) {
        mainCommunicator = communicator;
    }

    public static Data getData() {
        if (mainData == null) mainData = new Data();
        return mainData;
    }

    public static void setDebug(boolean DEBUG) {
        Globals.debug = DEBUG;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static DataValuesHolder getDataValuesHolder() {
        if (dataValuesHolder == null) {
            dataValuesHolder = new DataValuesHolder();
        }
        return dataValuesHolder;
    }

    public static void setDataValuesHolder(DataValuesHolder dataValuesHolder) {
        Globals.dataValuesHolder = dataValuesHolder;
    }
}
