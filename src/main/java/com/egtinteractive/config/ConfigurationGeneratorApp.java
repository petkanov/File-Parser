package com.egtinteractive.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;

import com.egtinteractive.app.moduls.ResponseData;
import com.egtinteractive.app.moduls.logger.FPLogger;
import com.egtinteractive.app.moduls.logger.Log4jLogger;
import com.egtinteractive.app.moduls.recovery.DBConnection;
import com.egtinteractive.app.moduls.recovery.RecoveryManager;
import com.egtinteractive.app.parsers.Parser;
import com.egtinteractive.app.parsers.ResponseTimeDomainParser;
import com.egtinteractive.app.writers.InfluxWriter;
import com.egtinteractive.app.writers.Writer;
import com.thoughtworks.xstream.XStream;

public class ConfigurationGeneratorApp {
    public static void main(String[] args) {
	generateConfig();
    }

    private static void generateConfig() {

	final List<ServiceConfig<?>> services = new LinkedList<>();
	final String logFileName = "file-parser-logs.log";
	final String workingDirectory = "/big_device/veto";
	final int msTimeDirectoryScanDelay = 1200;
	final DBConnection connectionPool = new DBConnection("jdbc:mysql://localhost:3306/file_reader?useSSL=false", "root", "3569", "mysql");
	final FPLogger logger = new Log4jLogger("log4j.properties", logFileName);
	final RecoveryManager recoveryManager = new RecoveryManager(connectionPool);

	final int lineProtocolPointsPerBatchTransfer = 1500;

	final Parser<ResponseData> parser = new ResponseTimeDomainParser<>("HTTP\\sNative\\sexecute\\s(http://.*?)=>\\s([0-9]{1,})",
		"\\[([0-9]{1,2}\\s[a-zA-Z]{3,12}\\s[0-9]{4}\\s[0-9]{2}:[0-9]{2}:[0-9]{2},[0-9]{3})\\]", "dd MMMM yyyy HH:mm:ss,SSS");
	final Writer<ResponseData> writer = new InfluxWriter<>("http://localhost:8086/write?db=someDb3", lineProtocolPointsPerBatchTransfer);
	services.add(new ServiceConfig<ResponseData>("thanos", parser, writer));

	final Parser<ResponseData> parserString = new ResponseTimeDomainParser<>("HTTP\\sNative\\sexecute\\s(http://.*?)=>\\s([0-9]{1,})",
		"\\[([0-9]{1,2}\\s[a-zA-Z]{3,12}\\s[0-9]{4}\\s[0-9]{2}:[0-9]{2}:[0-9]{2},[0-9]{3})\\]", "dd MMMM yyyy HH:mm:ss,SSS");
	final Writer<ResponseData> writerString = new InfluxWriter<>("http://localhost:8086/write?db=otherDb3", lineProtocolPointsPerBatchTransfer);
	services.add(new ServiceConfig<ResponseData>("gamora", parserString, writerString));

	final Config config = new Config(services, logFileName, workingDirectory, msTimeDirectoryScanDelay, recoveryManager, logger);

	final XStream x = new XStream();
	try {
	    x.toXML(config, new FileOutputStream(new File("configuration.xml")));
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
    }
}
