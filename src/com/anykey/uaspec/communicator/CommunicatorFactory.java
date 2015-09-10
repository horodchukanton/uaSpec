package com.anykey.uaspec.communicator;


import jssc.SerialPortException;

/**
 * Created by Anton Horodchuk on 016 16.05.15.
 *
 */
public class CommunicatorFactory {
    private static Communicator communicator;

    public static Communicator getCommunicator() {
        if (communicator == null) {
            try {
                communicator = new Communicator();
            } catch (SerialPortException e) {
                try {
                    //e.printStackTrace();
                    System.err.println("Can't connect to spectrometer. Program will be run in demo mode");
                    System.out.println(e.getExceptionType());
                    System.err.println(e.getMessage());
                    communicator = new DemoCommunicator();
                } catch (SerialPortException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return communicator;
    }

    public static Communicator renew() {
        System.out.println("renew");
        try {
            communicator.closePort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
        communicator = null;
        return getCommunicator();
    }
}
