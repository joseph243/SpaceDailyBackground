package com.josephvanderzwart;

public class APIValues {

    private static final String key = "DEMO_KEY";
    private static final String url = "https://api.nasa.gov/planetary/apod?api_key=";

    public static String getKey() {
        return key;
    }

    public static String getUrl() {
        return url;
    }

}
