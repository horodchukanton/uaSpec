package com.anykey.uaspec.WebServer;

import com.anykey.uaspec.communicator.CommunicatorFactory;
import com.anykey.uaspec.utils.Globals;
import jssc.SerialPortException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Anton Horodchuk on 014 14.05.15.
 *
 */
public class ComWriter {
    static final String ERROR_MESSAGE = "ComWriterError";

    private HttpServletRequest request = null;
    private String result;
    private ArrayList<String> knownCommands = new ArrayList<>(Arrays.asList(new String[]{
            "openPort",
            "renew",
            "specInfo",
            "closePort",
            "setAsZero",
            "flushZero",
            "exposition",
            "framesAveraged",
            "csv"
    }));

    public String getResponse(HttpServletRequest request) {
        result = ERROR_MESSAGE;
        this.request = request;
        processCommands(request);

        return result;
    }

    private void processCommands(HttpServletRequest request) {
        java.util.Enumeration<String> params = request.getParameterNames();

        while (params.hasMoreElements()) {
            processElement(params.nextElement());
        }
    }

    private void processElement(String param) {
        //System.out.println("processing:" + param);
        String value = request.getParameter(param);
        if (knownCommands.contains(param)) {
            if (value.equals("action")) {
                result = "";
                switch (param) {
                    case "openPort":
                        openPort();
                        break;
                    case "setAsZero":
                        Globals.getData().setZeroLevel();
                        break;
                    case "flushZero":
                        Globals.getData().flushZeroLevel();
                        break;
                    case "closePort":
                        closePort();
                        break;
                    case "renew":
                        Globals.setCommunicator(CommunicatorFactory.renew());
                        break;
                }
            } else if (value.equals("get")) {
                switch (param) {
                    case "specInfo":
                        getSpecInfo();
                        break;
                    case "exposition":
                        result = Byte.toString(Globals.getCommunicator().getExposition());
                        break;
                    case "framesAveraged":
                        result = Byte.toString(Globals.getCommunicator().getFramesAveraged());
                        break;
                    case "csv":
                        long millis = System.currentTimeMillis();
                        result = Globals.getDataValuesHolder().getCSV();
                        if (Globals.isDebug())
                            System.out.println("time to return CSV:" + (System.currentTimeMillis() - millis));
                        break;
                }
            } else
                switch (param) {
                    case "exposition":
                        byte expo = (value.equals("")
                                ? Globals.getCommunicator().getExposition()
                                : (byte) Integer.parseInt(value));

                        Globals.getCommunicator().setExposition(expo);

                        if (result.equals(ERROR_MESSAGE)) result = "Exposition has been set to: " + value;
                        else
                            result = result.concat(" Exposition has been set to:" + value + ". \n");
                        break;
                    case "framesAveraged":
                        byte aver = (value.equals("")
                                ? Globals.getCommunicator().getFramesAveraged()
                                : (byte) Integer.parseInt(value));
                        Globals.getCommunicator().setFramesAveraged(aver);

                        if (result.equals(ERROR_MESSAGE)) result = "Averaged has been set to: " + value;
                        else
                            result = result.concat(" Averaged has been set to:" + value + ". \n");

                        break;
                    default:
                        System.err.println("This should not be here");
                }
        } else {
            System.err.println("COMWriter: Unknown parameter: \'" + param + "\' ; value: " + value);
        }
    }

    private void openPort() {
        try {
            Globals.getCommunicator().openPort();
        } catch (SerialPortException e) {
            Globals.setCommunicator(CommunicatorFactory.renew());
            try {
                Globals.getCommunicator().openPort();
            } catch (SerialPortException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void closePort() {
        try {
            Globals.getCommunicator().closePort();
        } catch (SerialPortException e) {
            System.err.println("Error closing port: " + e.getExceptionType());
            Globals.setCommunicator(CommunicatorFactory.renew());
            try {
                Globals.getCommunicator().closePort();
            } catch (SerialPortException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void getSpecInfo() {
        try {
            //System.out.println("processing specInfo");
            result = Globals.getCommunicator().getSpecInfo();

        } catch (SerialPortException e) {
            Globals.setCommunicator(CommunicatorFactory.renew());
            try {
                result = Globals.getCommunicator().getSpecInfo();
            } catch (SerialPortException e1) {
                e1.printStackTrace();
            }
        }
    }
}

