package com.egtinteractive.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

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
	final ServiceChain serviceChain = createServiceChain(config);
	final Set<String> oldFiles = new HashSet<>();

	while (true) {

	    System.out.println("-------------------------------new read----------------");

	    final File workingDir = new File(config.getWorkingDirectory());

	    for (File file : workingDir.listFiles()) {
		if (file.isFile() && !oldFiles.contains(file.getName())) {
		    serviceChain.acceptFile(file.getAbsolutePath());
		    oldFiles.add(file.getName());
		}
	    }
	    try {
		Thread.sleep(timeDelay);
	    } catch (InterruptedException e) {
		Logger.getLogger(App.class).error(e.getMessage());
	    }
	}
    }

    private static ServiceChain createServiceChain(final Config config) {
	ServiceChain first = null;
	ServiceChain previous = null;
	for (ServiceConfig<?> serviceConfig : config.getServices()) {
	    if (first == null) {
		first = new Service<>(serviceConfig);
		previous = first;
		continue;
	    }
	    ServiceChain current = new Service<>(serviceConfig);
	    previous.setNextLink(current);
	    previous = current;
	}
	return first;
    }

    private static void generateConfig() {
	final List<ServiceConfig<?>> services = new LinkedList<>();
	final String logFileName = "file-parser-logs.log";
	final String workingDirectory = "/big_device/veto";
	

	final Parser<ResponseData> parser = new ResponseTimeDomaneParser<>("HTTP\\sNative\\sexecute\\s(http://.*?)=>\\s([0-9]{1,})",
		"\\[([0-9]{1,2}\\s[a-zA-Z]{3,12}\\s[0-9]{4}\\s[0-9]{2}:[0-9]{2}:[0-9]{2},[0-9]{3})\\]",
		"dd MMMM yyyy HH:mm:ss,SSS");
	final Writer<ResponseData> writer = new InfluxWriter<>("http://localhost:8086/write?db=someDb3", 1500, "http://localhost:8086/query", "q=CREATE DATABASE someDb3");
	services.add(new ServiceConfig<ResponseData>("thanos", parser, writer));

	final Parser<ResponseData> parserString = new ResponseTimeDomaneParser<>("HTTP\\sNative\\sexecute\\s(http://.*?)=>\\s([0-9]{1,})",
		"\\[([0-9]{1,2}\\s[a-zA-Z]{3,12}\\s[0-9]{4}\\s[0-9]{2}:[0-9]{2}:[0-9]{2},[0-9]{3})\\]",
		"dd MMMM yyyy HH:mm:ss,SSS");
	final Writer<ResponseData> writerString = new InfluxWriter<>("http://localhost:8086/write?db=otherDb3", 1500, "http://localhost:8086/query", "q=CREATE DATABASE otherDb3");
	services.add(new ServiceConfig<ResponseData>("gamora", parserString, writerString));

	final XStream x = new XStream();
	final Config config = new Config(services, logFileName, workingDirectory);
	try {
	    x.toXML(config, new FileOutputStream(new File("configuration.xml")));
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
    }

}
