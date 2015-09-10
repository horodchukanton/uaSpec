package com.anykey.uaspec.WebServer;

/**
 * Created by Anton Horodchuk on 013 13.05.15.
 */

import com.anykey.uaspec.utils.Globals;

import java.io.*;

public class StaticFile {
    String fileName;

    public StaticFile(String fileName) {
        this.fileName = Globals.getWebRoot() + File.separator + fileName;
        //System.out.println(this.fileName);
    }

    public String getContent() {
        StringBuilder result = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            return "File not found \'"+ fileName + "\'";
        }
        try {
            String line = null;
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            return "IOException in StaticFile";
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return result.toString();
    }
}
