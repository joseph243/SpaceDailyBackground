package com.josephvanderzwart;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String config = "config.txt";
    private static final String defaultPath = "C:\\SpaceDailyBackground";
    private static String myApiKey = APIValues.getDemoKey();
    private static String myPath = defaultPath;
    private static boolean myWantDescriptions = true;
    private static PrintWriter writer;

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Daily Space Background:");
        buildConfig();
        parseConfig();
        PictureOfTheDay pictureOfTheDay = new PictureOfTheDay();
        String query = APIValues.getUrl() + myApiKey;
        boolean responseSuccess = false;
        HttpResponse<JsonNode> response = null;

        if (buildPath()) {
            response = Unirest.get(query).asJson();
            responseSuccess = response.isSuccess();
        }
        else {
            System.out.println("ERROR BUILDING PATH:  DID NOT DOWNLOAD PICTURE OF THE DAY.");
            throw new Exception("ERROR BUILDING PATH:  DID NOT DOWNLOAD PICTURE OF THE DAY. INVALID PATH? " + myPath);
        }

        if (responseSuccess) {
            System.out.println("Server Response = " + response.getStatus());
            pictureOfTheDay.setDate(response.getBody().getObject().get("date").toString());
            pictureOfTheDay.setUrl(response.getBody().getObject().get("url").toString());
            if (myWantDescriptions) {
                pictureOfTheDay.setInfoText(response.getBody().getObject().get("explanation").toString());
            }
        } else {
            throw new Exception("Server Response Error " + response.getStatus() + " " + response.getStatusText());
        }

        //output:
        if (myWantDescriptions) {
            writeText(pictureOfTheDay.getDate() + ".txt", pictureOfTheDay.getInfoText());
        }
        downloadFile(pictureOfTheDay.getDate() + ".jpg", pictureOfTheDay.getUrl());
    }

    private static void writeText(String inFileName, String inText) {
        try {
            writer = new PrintWriter(myPath + "\\" + inFileName);
            writer.println(inText);
        } catch (Exception e) {
            System.out.println("cannot find file: " + myPath + "\\" + inFileName);
        }
        writer.close();
    }

    private static void downloadFile(String infileName, String inURL) {
        System.out.print("Downloading...   ");
        InputStream inputStream = null;
        Long bytes = Long.valueOf("0");
        try {
            inputStream = new URL(inURL).openStream();
            bytes = Files.copy(inputStream, Paths.get(myPath + "\\" + infileName), StandardCopyOption.REPLACE_EXISTING);
            inputStream.close();
            System.out.println("Finished. " + bytes/1024 + " KB downloaded from URL:");
            System.out.println(inURL);
            System.out.println("Saved to: " + myPath + "\\" + infileName);
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("ERROR WORKING DOWNLOAD STREAM:" + e.toString());
        }
    }

    private static boolean buildPath() {
        try {
            Files.createDirectory(Paths.get(myPath));
            System.out.println("Created Directory " + myPath);
        }
        catch (FileAlreadyExistsException e) {
            System.out.println("Found Existing Directory: " + myPath);
        }
        catch (IOException e) {
            System.out.println("SYSTEM IO ERROR CREATING DIRECTORY" + e);
        }
        catch (Exception e) {
            System.out.println("UNKNOWN ERROR CREATING DIRECTORY" + e);
        }
        return Files.exists(Paths.get(myPath));
    }

    private static void buildConfig() {
            if (Files.exists(Paths.get(config))) {
                System.out.println("Found Configuration File: " + config);
            }
            else {
                System.out.println("Did not find Configuration File, attempting to create: ");
                try {
                    String path = Files.write(Paths.get(config), com.josephvanderzwart.configFile.getText().getBytes())
                            .toAbsolutePath().toString();
                    System.out.println("Created file: " + path);
                }
                catch (SecurityException e) {
                    System.out.println("FILE SECURITY EXCEPTION BUILDING" + config + ", LOGGED IN USER MAY NOT " +
                            "HAVE WRITE ACCESS TO THIS FOLDER: " + e);
                }
                catch (Exception e)
                {
                    System.out.println("UNKNOWN ERROR CREATING" + config + " FILE: " + e);
                }
            }
    }

    private static void parseConfig() {
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(Paths.get(config));
        }
        catch (Exception e) {
            System.out.println("ERROR READING CONFIG FILE, USING DEFAULTS." + e);
        }
        if (lines != null)
        {
            for (String line : lines) {
                if (line.toCharArray().length == 0 || line.toCharArray()[0] == '#') {
                    //purposely blank. ignore #comment and blank lines.
                }
                else {
                    //read uncommented line:
                    String[] text = line.split(": ");
                    switch (text[0]) {
                        case "imagePath":
                            myPath = text[1];
                            System.out.println("Set parameter from config: " + text[0] + " TO " + text[1]);
                            break;
                        case "apiKey":
                            myApiKey = text[1];
                            System.out.println("Set parameter from config: " + text[0] + " TO " + text[1]);
                            break;
                        case "saveDescription":
                            myWantDescriptions = text[1].equalsIgnoreCase("true");
                            System.out.println("Set parameter from config: " + text[0] + " TO " + text[1]);
                            break;
                        default:
                            System.out.println("UNRECOGNIZED CONFIG PARAMETER: " + text[0]);
                    }
                }
            }
        }
        else if (lines.size() > 100){
            System.out.println("ERROR: CONFIG NOT APPLIED. FILE TOO LARGE, REDUCE TO LESS THAN 100 LINES.");
        }
        else {
            System.out.println("ERROR: DID NOT CORRECTLY PARSE CONFIG FILE.");
        }
    }
}