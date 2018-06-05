package com.egtinteractive.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.PropertyConfigurator;
import com.egtinteractive.app.App;
import com.thoughtworks.xstream.XStream;


public class FileLoader {
    private static final Config CONFIG;
    
    static {
	final XStream x = new XStream();
	CONFIG = (Config) x.fromXML(new File("configuration.xml"));
	initializeLogger(CONFIG.getLogFileName());
    }
    
    public static Config getConfiguration() {
	return CONFIG;
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
