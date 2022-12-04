package com.josephvanderzwart;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Main {

    public static final String path = "C:\\Users\\Joseph\\Pictures\\NASA Picture of the Day\\";
    private static PrintWriter writer;

    public static void main(String[] args) throws Exception {
        System.out.println("Getting Daily Space Background:");
        PictureOfTheDay potd = new PictureOfTheDay();
        String query = APIValues.getUrl() + APIValues.getKey();
        HttpResponse<JsonNode> response = kong.unirest.Unirest.get(query).asJson();
        if (response.isSuccess()) {
            System.out.println("Server Response = " + response.getStatus());
            potd.setDate(response.getBody().getObject().get("date").toString());
            potd.setInfoText(response.getBody().getObject().get("explanation").toString());
            potd.setUrl(response.getBody().getObject().get("url").toString());
        } else {
            throw new Exception("Server Response Error " + response.getStatus());
        }
        writeText(potd.getDate() + ".txt", potd.getInfoText());
        downloadFile(potd.getUrl(), potd.getDate() + ".jpg");
    }

    public static void writeText(String inFileName, String inText) {
        try {
            writer = new PrintWriter(path + inFileName);
            writer.println(inText);
        } catch (Exception e) {
            System.out.println("cannot find file: " + path + inFileName);
        }
        writer.close();
    }

    public static void downloadFile(String inURL, String infileName) {
        InputStream inputStream = null;
        Long bytes = Long.valueOf("0");
        try {
            inputStream = new URL(inURL).openStream();
            bytes = Files.copy(inputStream, Paths.get(path + infileName), StandardCopyOption.REPLACE_EXISTING);
            inputStream.close();
        } catch (Exception e) {
            System.out.println("ERROR WORKING DOWNLOAD STREAM:" + e.toString());
        }
        System.out.println("file copy: " + bytes + " bytes read from:");
        System.out.println(inURL);
        System.out.println("Saved to: " + path + infileName);
    }
}