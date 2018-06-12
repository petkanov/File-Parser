package com.egtinteractive.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;

import com.egtinteractive.app.ConnectionPool;
import com.egtinteractive.app.FPLogger;
import com.egtinteractive.app.InfluxWriter;
import com.egtinteractive.app.Log4jLogger;
import com.egtinteractive.app.Parser;
import com.egtinteractive.app.ProcessingRunner;
import com.egtinteractive.app.ProcessingRunnerImpl;
import com.egtinteractive.app.RecoveryManager;
import com.egtinteractive.app.ResponseData;
import com.egtinteractive.app.ResponseTimeDomaneParser;
import com.egtinteractive.app.Writer;
import com.thoughtworks.xstream.XStream;

public class Bootstrap {
    public static void generateConfig() {

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
}
