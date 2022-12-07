package com.josephvanderzwart;

import java.util.HashMap;
import java.util.Map;

public class PictureOfTheDay {

    private Map<String, String> attributes = new HashMap<String, String>();

    public String[] getAllAttributes() {
        return (String[]) attributes.keySet().toArray();
    }

    public String getValue(String inAttribute)
    {
        return attributes.get(inAttribute);
    }

    public PictureOfTheDay() {
    }

    public void parseHTTPresponse (String responseBody) {
        responseBody = responseBody.substring(1, responseBody.length()-2);
        String aKey = "";
        String aVal = "";
        String aObj = "";
        boolean inObj = false;
        for (Character c : responseBody.toCharArray()) {
            if (c == '"')
            {
                //are we currently inside quotes?
                inObj = !inObj;
            }
            if (inObj && c != '"')
            {
                aObj = aObj + c;
            }
            else
            {
                if (aKey == "")
                {
                    aKey = aObj;
                    aObj = "";
                }
                else
                {
                    aVal = aObj;
                    aObj = "";
                }
            }
            if (!inObj && aKey != "" && aVal != "")
            {
                attributes.put(aKey, aVal);
                aKey = "";
                aVal = "";
            }
        }
    }
}
