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
    public static void main(String[] args) {
	
	
	
	
	generateConfig();
	readConfig();
    }

    private static void readConfig() {
	final Config config = FileLoader.getConfiguration();
	
	for (ServiceConfig<?> serviceConfig : config.getServices()) {
	    new Service<>(serviceConfig).start();
	}
    }

    private static void generateConfig() {
	final List<ServiceConfig<?>> services = new LinkedList<>();
	final String logFileName = "file-parser-logs.log";

	final Parser<ResponseData> parser = new ResponseTimeDomaneParser<>("some pattern");
	final Writer<ResponseData> writer = new InfluxWriter<>("http://localhost:8086", "someDb3", 1500);

	services.add(new ServiceConfig<ResponseData>("/big_device/veto/micro.log.thanos", parser, writer));

	final XStream x = new XStream();
	final Config config = new Config(services, logFileName);
	try {
	    x.toXML(config, new FileOutputStream(new File("configuration.xml")));
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
    }
    
    
}
