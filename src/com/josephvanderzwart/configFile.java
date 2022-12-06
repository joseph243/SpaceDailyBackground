package com.josephvanderzwart;

public class configFile {
    private static String text = "# config file needs to be in same directory as executable JAR.\n" +
            "# config file needs parameters in this style, or these defaults will be used:\n" +
            "# imagePath: c:/SpaceDailyImages\n" +
            "# apiKey: DEMO_KEY\n" +
            "# saveDescription: true\n" +
            "\n" +
            "imagePath: c:/SpaceDailyBackground\n" +
            "apiKey: DEMO_KEY\n" +
            "saveDescription: true";

    public static String getText() {
        return text;
    }
}
