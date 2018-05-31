package com.egtinteractive.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.egtinteractive.config.Config;
import com.egtinteractive.config.FileLoader;
import com.egtinteractive.config.Service;
import com.egtinteractive.config.ServiceConfig;
import com.thoughtworks.xstream.XStream;

 
public class App 
{
    public static void main(String[] args) throws Exception {
	generateConfig();
	readConfig();
    }

    private static void readConfig() throws IOException {
	final Config config = FileLoader.getConfiguration();
	for (ServiceConfig<?> serviceConfig : config.getServices()) {
	    new Service<>(serviceConfig).start();
	}
    }

    private static void generateConfig() throws FileNotFoundException {
	final List<ServiceConfig<?>> services = new LinkedList<>();

	final Parser<String> parser = new SingleLineParserConfig<>("some pattern");
	final Writer<String> writer = new WriterImpl<>("someField");

	services.add(new ServiceConfig<String>("configuration.xml", parser, writer));

	final XStream x = new XStream();
	final Config config = new Config(services);
	x.toXML(config, new FileOutputStream(new File("configuration.xml")));
    }
}
