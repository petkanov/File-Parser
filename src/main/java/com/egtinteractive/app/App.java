package com.egtinteractive.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import com.egtinteractive.config.AsyncService;
import com.egtinteractive.config.Config;
import com.egtinteractive.config.FileLoader;
import com.egtinteractive.config.Service;
import com.egtinteractive.config.ServiceConfig;
import com.thoughtworks.xstream.XStream;

public class App {
    public static void main(String[] args) throws Exception {
	generateConfig();
	readConfig();
    }

    private static void readConfig() {
	final int timeDelay = 1200;
	final Config config = FileLoader.getConfiguration();
	final RecoveryManager recoveryManager = config.getRecoveryManager();
	final FPLogger logger = config.getLogger();
	logger.initializeLogger();
	recoveryManager.setLogger(logger);

	final List<Service> services = getServices(config);

	while (true) {
	    try {
		final File workingDir = new File(config.getWorkingDirectory());

		for (File file : workingDir.listFiles()) {
		    final String fileName = file.getAbsolutePath();
		    if (file.isFile() && !recoveryManager.isFileAlreadySeen(file.getName())) {
			for (Service service : services) {
			    service.acceptFile(fileName);
			}
		    }
		    recoveryManager.addToAlreadySeenFiles(fileName);
		}
		Thread.sleep(timeDelay);
	    } catch (Exception e) {
		logger.logErrorMessage(App.class, e.getMessage());
	    }
	}
    }

    
    @SuppressWarnings("unused")
    private static void generateConfig() {

	final List<ServiceConfig<?>> services = new LinkedList<>();
	final String logFileName = "file-parser-logs.log";
	final String workingDirectory = "/big_device/veto";

	final Parser<ResponseData> parser = new ResponseTimeDomaneParser<>("HTTP\\sNative\\sexecute\\s(http://.*?)=>\\s([0-9]{1,})",
		"\\[([0-9]{1,2}\\s[a-zA-Z]{3,12}\\s[0-9]{4}\\s[0-9]{2}:[0-9]{2}:[0-9]{2},[0-9]{3})\\]", "dd MMMM yyyy HH:mm:ss,SSS");
	final Writer<ResponseData> writer = new InfluxWriter<>("http://localhost:8086/write?db=someDb3", 1500,
		"http://localhost:8086/query", "q=CREATE DATABASE someDb3");
	final ProcessingRunner<ResponseData> processingRunner1 = new ProcessingRunnerImpl<>();
	services.add(new ServiceConfig<ResponseData>("thanos", parser, writer, processingRunner1));

	final Parser<ResponseData> parserString = new ResponseTimeDomaneParser<>("HTTP\\sNative\\sexecute\\s(http://.*?)=>\\s([0-9]{1,})",
		"\\[([0-9]{1,2}\\s[a-zA-Z]{3,12}\\s[0-9]{4}\\s[0-9]{2}:[0-9]{2}:[0-9]{2},[0-9]{3})\\]", "dd MMMM yyyy HH:mm:ss,SSS");
	final Writer<ResponseData> writerString = new InfluxWriter<>("http://localhost:8086/write?db=otherDb3", 1500,
		"http://localhost:8086/query", "q=CREATE DATABASE otherDb3");
	final ProcessingRunner<ResponseData> processingRunner2 = new ProcessingRunnerImpl<>();
	services.add(new ServiceConfig<ResponseData>("gamora", parserString, writerString, processingRunner2));

	final ConnectionPool connectionPool = new ConnectionPool("jdbc:mysql://localhost:3306/file_reader?useSSL=false", "root", "3569", 7);
	final FPLogger logger = new Log4jLogger("log4j.properties", logFileName);
	final RecoveryManager recoveryManager = new RecoveryManager(connectionPool);

	final boolean clearRecoveryDatabaseOnStartup = false;

	final Config config = new Config(services, logFileName, workingDirectory, recoveryManager, logger, clearRecoveryDatabaseOnStartup);

	final XStream x = new XStream();
	try {
	    x.toXML(config, new FileOutputStream(new File("configuration.xml")));
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
    }

    private static List<Service> getServices(final Config config) {
	final List<Service> services = new ArrayList<>();
	for (ServiceConfig<?> serviceConfig : config.getServices()) {
	    final Service service = new AsyncService<>(serviceConfig, config.getRecoveryManager(), config.getLogger());
	    services.add(service);
	}
	return services;
    } 
}
