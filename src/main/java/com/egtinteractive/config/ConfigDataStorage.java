package com.egtinteractive.config;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ConfigDataStorage {

    private static final Pattern patternDomane = Pattern.compile("HTTP\\sNative\\sexecute\\s(http://.*?)=>\\s([0-9]{1,})");
    private static final Pattern patternTime = Pattern.compile("\\[([0-9]{1,2}\\s[a-zA-Z]{3,12}\\s[0-9]{4}\\s[0-9]{2}:[0-9]{2}:[0-9]{2},[0-9]{3})\\]");
    private static final SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss,SSS");
    
    private static final Map<String, String> streamObjectStart = new HashMap<>();
    private static final Map<String, String> streamObjectEnd = new HashMap<>();
    
    static {
	streamObjectStart.put("XmlWriter", "<object-stream>");
	streamObjectEnd.put("XmlWriter", "</object-stream>");

	streamObjectStart.put("JsonWriter", "[");
	streamObjectEnd.put("JsonWriter", "]");
    }

    
    public static String getStreamObjectStart(final String className) {
        return streamObjectStart.get(className);
    }

    public static String getStreamObjectEnd(final String className) {
        return streamObjectEnd.get(className);
    }

    public static Pattern getPatternDomane() {
        return patternDomane;
    }

    public static Pattern getPatternTime() {
        return patternTime;
    }

    public static SimpleDateFormat getFormat() {
        return format;
    }
}
