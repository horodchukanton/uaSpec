package com.anykey.uaspec.WebServer;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Anton Horodchuk on 014 14.05.15.
 */
//public class OutputWriter {
//    private static final String ERROR_MESSAGE = "OutputWriter Error";
//
//    private HttpServletRequest request = null;
//    private String result = ERROR_MESSAGE;
//    private ArrayList<String> knownCommands = new ArrayList<>(Arrays.asList(new String[]{
//            "header",
//            "footer"
//    }));
//
//    public String getResponse(HttpServletRequest request) {
//        result = ERROR_MESSAGE;
//        this.request = request;
//        processCommands(request);
//
//        return result;
//    }
//
//    private void processCommands(HttpServletRequest request) {
//        java.util.Enumeration<String> params = request.getParameterNames();
//
//        while (params.hasMoreElements()) {
//            processElement(params.nextElement());
//        }
//    }
//
//    private void processElement(String param) {
//        //System.out.println("processing:" + param);
//        if (knownCommands.contains(param)) {
//            if (param.equals("header")) result = new StaticFile("header.html").getContent();
//            if (param.equals("footer")) result = new StaticFile("footer.html").getContent();
//        } else {
//            System.err.println("OutputWriter: Unknown parameter: \'" + param + "\' value: " + request.getParameter(param));
//        }
//    }
//}
