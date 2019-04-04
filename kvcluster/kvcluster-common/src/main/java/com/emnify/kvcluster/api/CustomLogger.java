package com.emnify.kvcluster.api;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Danilo Oliveira
 */
public class CustomLogger {

    public static void println(String filename, Object contents) {
        try (FileWriter fileWriter = new FileWriter(filename, true)) {
            fileWriter.write(contents.toString() + "\n");
            fileWriter.flush();
        } catch (IOException ex) {
        }
    }

    public static void println(String contents) {
        String user = System.getProperty("user.name");
        println("/home/" + user + "/logfile.txt", contents);
    }

    public static void main(String[] args) {
        println("Ronaldo");
    }
}
