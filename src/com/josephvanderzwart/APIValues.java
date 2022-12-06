package com.josephvanderzwart;

public class APIValues {

    private static final String demoKey = "DEMO_KEY";
    private static final String url = "https://api.nasa.gov/planetary/apod?api_key=";

    public static String getDemoKey() {
        return demoKey;
    }

    public static String getUrl() {
        return url;
    }

}
