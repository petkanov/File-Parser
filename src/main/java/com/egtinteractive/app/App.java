package com.egtinteractive.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;

import com.egtinteractive.config.Config;
import com.egtinteractive.config.FileLoader;
import com.egtinteractive.config.Service;
import com.egtinteractive.config.ServiceConfig;
import com.thoughtworks.xstream.XStream;

public class App {
    public static void main(String[] args) throws Exception {

	generateConfig();

	final int timeDelay = 1200;
	final Config config = FileLoader.getConfiguration();
	ServiceChain serviceChain = createServiceChain(config);
	
	int i =1;
	while (i++ <2) {

	    System.out.println("-------------------------------new read----------------");
	    
	    File workingDir = new File(config.getWorkingDirectory());
	    
	    for (File file : workingDir.listFiles()) {
		if (file.isFile()) {
		    serviceChain.acceptFile(file.getAbsolutePath());
		}
	    }
	    Thread.sleep(timeDelay);
	}

//	 readConfig();
    }

    private static void readConfig() {
	final Config config = FileLoader.getConfiguration();

	
	for (ServiceConfig<?> serviceConfig : config.getServices()) {
	    new Service<>(serviceConfig).startProcessing();
	}
    }
    
    private static ServiceChain createServiceChain(final Config config) {
	ServiceChain first = null;
	ServiceChain previous = null;
	for (ServiceConfig<?> serviceConfig : config.getServices()) {
	    if(first == null) {
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

	final Parser<ResponseData> parser = new ResponseTimeDomaneParser<>("some pattern");
	final Writer<ResponseData> writer = new InfluxWriter<>("http://localhost:8086", "someDb3", 1500);
	services.add(new ServiceConfig<ResponseData>("thanos", parser, writer));

	final Parser<ResponseData> parserString = new ResponseTimeDomaneParser<>("some pattern");
	final Writer<ResponseData> writerString = new InfluxWriter<>("http://localhost:8086", "otherDb3", 1500);
	services.add(new ServiceConfig<ResponseData>("gamora", parserString, writerString));

//	final Parser<Integer> parserInteger = new ResponseTimeDomaneParser<>("some pattern");
//	final Writer<Integer> writerInteger = new InfluxWriter<>("http://localhost:8086", "bahDb3", 1500);
//	services.add(new ServiceConfig<Integer>("micro.log", parserInteger, writerInteger));

	final XStream x = new XStream();
	final Config config = new Config(services, logFileName, workingDirectory);
	try {
	    x.toXML(config, new FileOutputStream(new File("configuration.xml")));
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
    }

}
