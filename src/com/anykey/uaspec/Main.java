package com.anykey.uaspec;

import com.anykey.uaspec.WebServer.HttpServer;
import com.anykey.uaspec.communicator.Communicator;
import com.anykey.uaspec.communicator.CommunicatorFactory;
import com.anykey.uaspec.utils.Globals;
import jssc.SerialPortException;

import java.io.File;

public class Main {
    static Communicator communicator;

    //DEFAULT
    static String portName = "demo";
    static String httpPort = "80";

    public static void main(String[] args) {

        if (args.length > 0) {
            portName = args[0];

            if (args.length == 2) {
                httpPort = args[1];
            }
            if (args.length == 3) {
                Globals.setDebug(true);
            }
        } else {
            System.out.println("Usage: \n" +
                    "UaSpec \'COM Port name\' \'HTTP port\'\n" +
                    "Using default values: '" + portName + "' '" + httpPort + "'");
        }

        Globals.setPortName(portName);

        startCommunicator();

        startServer(httpPort);


    }

    private static void startServer(String port) {
        final String ROOTDIR = System.getProperty("user.dir") + File.separator + "www";
        Globals.setWebRoot(ROOTDIR);
        final int PORT = Integer.parseInt(port);

        try {
            HttpServer server = new HttpServer(ROOTDIR, PORT);
        } catch (Exception e) {
            System.err.println("ERROR STARTING SERVER");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void startCommunicator() {
        Globals.setCommunicator(CommunicatorFactory.getCommunicator());
        communicator = Globals.getCommunicator();
        try {
            System.out.println("Spectrometer info: " + communicator.getSpecInfo());
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

}
