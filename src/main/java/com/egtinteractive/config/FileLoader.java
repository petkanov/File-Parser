package com.egtinteractive.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.log4j.PropertyConfigurator;

import com.egtinteractive.app.App;
import com.thoughtworks.xstream.XStream;


public class FileLoader {
    private static final Config CONFIG;
    
    private static final Pattern patternDomane = Pattern.compile("HTTP\\sNative\\sexecute\\s(http://.*?)=>\\s([0-9]{1,})");
    private static final Pattern patternTime = Pattern.compile("\\[([0-9]{1,2}\\s[a-zA-Z]{3,12}\\s[0-9]{4}\\s[0-9]{2}:[0-9]{2}:[0-9]{2},[0-9]{3})\\]");
    private static final SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss,SSS");

    static {
	final XStream x = new XStream();
	CONFIG = (Config) x.fromXML(new File("configuration.xml"));
	initializeLogger(CONFIG.getLogFileName());
    }
    
     

    public static Config getConfiguration() {
	return CONFIG;
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

    private static void initializeLogger(final String logFileName) {
	final Properties props = new Properties();

	try (InputStream configStream = App.class.getClassLoader().getResourceAsStream("log4j.properties")) {
	    props.load(configStream);
	} catch (IOException e) {
	    System.out.println("Logger Error: Cannot load configuration file ");
	}
	props.setProperty("log4j.appender.file.File", logFileName);
	PropertyConfigurator.configure(props);
    }
}
