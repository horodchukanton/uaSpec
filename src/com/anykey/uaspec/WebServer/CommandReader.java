package com.anykey.uaspec.WebServer;

import com.anykey.uaspec.data.Data;
import com.anykey.uaspec.communicator.Communicator;
import com.anykey.uaspec.utils.Globals;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Anton Horodchuk on 014 14.05.15.
 *
 */
public class CommandReader {
    private static CommandReader self = null;
    private Communicator communicator = Globals.getCommunicator();
    private Data data = Globals.getData();

    public static CommandReader getInstance() {
        if (self == null) {
            synchronized (CommandReader.class) {
                if (self == null) {
                    self = new CommandReader();
                }
            }
        }
        return self;
    }

    public String getResponse(HttpServletRequest request) {
//        result = "ComWriter Error";
//        this.request = request;
//        processCommands(request);
//
//        return result;
        return null;
    }
}
