package com.egtinteractive.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log4jLogger implements FPLogger {

    private final String propertiesFile;
    private final String logFileName;
    
    public Log4jLogger(final String propertiesFile, final String logFileName) {
	this.propertiesFile = propertiesFile;
	this.logFileName = logFileName;
    }
    
    public void logInfoMessage(Class<?> cls, String msg) {
	Logger.getLogger(cls).info(msg);
    }

    public void logWarnMessage(Class<?> cls, String msg) {
	Logger.getLogger(cls).warn(msg);
    }

    public void logErrorMessage(Class<?> cls, String msg) {
	Logger.getLogger(cls).error(msg);
    }
    
    public void initializeLogger() {
	final Properties props = new Properties();

	try (InputStream configStream = App.class.getClassLoader().getResourceAsStream(propertiesFile)) {
	    props.load(configStream);
	} catch (IOException e) {
	    throw new RuntimeException("Logger Error: Cannot load configuration file ");
	}
	props.setProperty("log4j.appender.file.File", logFileName);
	PropertyConfigurator.configure(props);
    }
}
