package com.anykey.uaspec;

import com.anykey.uaspec.utils.Globals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Anton Horodchuk on 021 21.05.15.
 */
public class LevelAnalyzer {

    private static ConcurrentHashMap<Integer, Integer> monitoredWavelengthes = new ConcurrentHashMap<>(new LinkedHashMap<Integer,Integer>());
    private static LinkedHashMap<Integer, Double> monitoredLevels = new LinkedHashMap<>();

    private static LinkedHashMap<Integer, Double> data;

    private static ConcurrentHashMap<Integer, Double> levels = new ConcurrentHashMap<>();
    private static HashMap<Integer, Double> dispersions = new HashMap<>(50, 0.7f);

    private static AnalyzerThread thread = new AnalyzerThread();
    private static int numOfPoints = 0;
    private boolean threadActive;

    private void setData(LinkedHashMap<Integer, Double> data) {
        this.data = data;
    }

    public LevelAnalyzer() {
        startThread();
    }

    public void addMonitoredPoint(int wavelength, int rapper, double level, double dispersion) {
        if (
                (wavelength > Globals.getCommunicator().MIN_WAVELENGTH &&
                        wavelength < Globals.getCommunicator().MAX_WAVELENGTH) &&
                        (rapper > Globals.getCommunicator().MIN_WAVELENGTH &&
                                rapper < Globals.getCommunicator().MAX_WAVELENGTH)
                ) {
            monitoredWavelengthes.put(wavelength, rapper);
            monitoredLevels.put(wavelength, level);
            dispersions.put(wavelength, dispersion);
        }
    }

    public boolean isActive() {
        return threadActive;
    }

    public void stopThread() {

        thread.stop();
    }

    public void startThread() {
        if (!thread.running) {
            new Thread(thread).start();
            threadActive = true;
        }
    }

    private Double getLevel(int wavelength) {
        if (monitoredWavelengthes.get(wavelength) != null) {
            return levels.get(wavelength);
        } else
            return 0.0;
    }

    private Double getDispersion(int wavelength) {
        Double disp = dispersions.get(wavelength);
        if (disp != null) {
            return disp;
        } else
            return 0.0;
    }

    private boolean checkPoint(int waveLength) {
        Double value = getLevel(waveLength);
        Double level = monitoredLevels.get(waveLength);

        return ((Math.round(value - level) * 10) / 10) < dispersions.get(waveLength);
    }

    public ArrayList<ArrayList<String>> getMonitoredStrArray() {
        ArrayList<ArrayList<String>> result = new ArrayList<>(0);

        for (int key : monitoredWavelengthes.keySet()) {
            ArrayList<String> row = new ArrayList<>(4);
            try {
                boolean norm = Math.round(
                        (Math.abs(levels.get(key) - monitoredLevels.get(key))) * 100.0) / 100.0
                        < dispersions.get(key);
                row.add(Integer.toString(++numOfPoints));
                row.add(Integer.toString(key));                      //wavelength
                row.add(monitoredWavelengthes.get(key).toString());  //rapper
                row.add(Double.toString(Math.round((levels.get(key)*100.0))/100.0));           //level
                row.add(Double.toString(Math.round((levels.get(key) - monitoredLevels.get(key)) * 100.0) / 100.0)); //dispersion
                row.add(((norm) ? "<span style='color: greenyellow;'>OK</span>" : "<span style='color: red;'>FAIL</span>"));
            } catch (NullPointerException ex) {
                row.add(Integer.toString(key));
                row.add("Error");
                row.add("adding");
                row.add("a");
                row.add("point");
                row.add("Fail");
            }
//            row.add()
            result.add(row);
        }

        return result;
    }


    private static class AnalyzerThread implements Runnable {
        private boolean running = false;
        private boolean active = true;

        private void toggle() {
            active = !active;
        }

        @Override
        public void run() {
            running = true;
            while (!Thread.interrupted()) {
                Globals.getDataValuesHolder().getNew();
                data = Globals.getDataValuesHolder().getIntensityMap();
                for (int key : monitoredWavelengthes.keySet()) {
                    levels.put(key, Math.round((data.get(key) / data.get(monitoredWavelengthes.get(key))) * 1000.0) / 1000.0);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }

        public void stop() {
            running = false;
            active = false;
        }
    }


    public static void main(String[] args) {
//        final int WAVELENGTH1 = 420;
//        final int WAVELENGTH2 = 514;
//
//        final Double LEVEL = 3.0;
//        final Double ALLOWED_DISPERSION = 2.0;
//
//        Globals.setPortName("COM5");
//        LevelAnalyzer levelAnalyzer = new LevelAnalyzer();
//        levelAnalyzer.addMonitoredPoint(WAVELENGTH2, WAVELENGTH1, LEVEL, ALLOWED_DISPERSION);
//        levelAnalyzer.startThread();
//        while (true) {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {/**/}
//            System.out.println(levelAnalyzer.checkPoint(WAVELENGTH2, ALLOWED_DISPERSION));
//        }
    }
}
