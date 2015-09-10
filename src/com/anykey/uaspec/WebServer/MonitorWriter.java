package com.anykey.uaspec.WebServer;

import com.anykey.uaspec.LevelAnalyzer;
import com.anykey.uaspec.utils.Globals;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by Anton Horodchuk
 * on 024 24.05.15.
 */
public class MonitorWriter {
    private static final String ERROR_MESSAGE = "MonitorWriter Error";

    private String result = ERROR_MESSAGE;
    private ArrayList<String> knownParameters = new ArrayList<>(Arrays.asList(new String[]{
            "monitor"
    }));

    public String getResponse(HttpServletRequest request, HttpServletResponse response) {
        result = ERROR_MESSAGE;

        processCommands(request);

        return result;
    }

    private void processCommands(HttpServletRequest request) {
        java.util.Enumeration<String> params = request.getParameterNames();

        while (params.hasMoreElements()) {
            String param = params.nextElement();
            processElement(param,request.getParameter(param));
        }
    }

    private void processElement(String param, String value) {
        if (knownParameters.contains(param)) {
            if (value.equals("get")) {
                result = getMonitoredPanel();
            }
        } else {
            System.err.println("MonitorWriter: Unknown parameter: \'" + param + "\' value: " + value);
        }
    }


    public void setMonitorSettings(HttpServletRequest request) {
        int rapper = Integer.parseInt(request.getParameter("Rapper"));
        int wavelength = Integer.parseInt(request.getParameter("Wave"));
        double level = Double.parseDouble(request.getParameter("Level"));
        double dispersion = Double.parseDouble(request.getParameter("Dispersion"));

        Globals.getLevelAnalyzer().addMonitoredPoint(wavelength, rapper, level, dispersion);
    }
//    private void createNewPoint(String value) {
//        System.out.println("create");
//        System.out.println("value = " + value);
//    }
//
//    private void appendToPoint(String param, String value) {
//        System.out.println("append");
//        System.out.println("param = " + param);
//        System.out.println("value = " + value);
//    }
//
//    private void createNewRapper(String value) {
//        System.out.println("createRapper");
//        System.out.println("value = " + value);
//    }

    private String getMonitoredPanel() {
        final String COL = "col-lg-2 col-md-2 col-sm-2 col-xs-2";
        StringBuilder sb = new StringBuilder();
        LevelAnalyzer levelAnalyzer = Globals.getLevelAnalyzer();
        ArrayList<ArrayList<String>> strings = levelAnalyzer.getMonitoredStrArray();

        for (ArrayList<String> row : strings) {
            sb.append("<div class='row'>");
            for (String value : row) {
                sb.append("<label class='" + COL + "'>" + value + "</label>");
            }
            sb.append("<hr>");
            sb.append("</div>");
        }
        return sb.toString();
    }

}
