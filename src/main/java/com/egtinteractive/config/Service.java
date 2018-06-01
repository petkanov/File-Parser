package com.egtinteractive.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.egtinteractive.app.Parser;
import com.egtinteractive.app.Writer;

public class Service<T> {

    private final ServiceConfig<T> serviceConfig;

    public Service(ServiceConfig<T> serviceConfig) {
	this.serviceConfig = serviceConfig;
    }

    public void start() {

	try (BufferedReader br = new BufferedReader(new FileReader(new File(serviceConfig.getFileName())))) {
	    String line = null;
	    do {
		line = br.readLine();

		final Parser<T> parser = serviceConfig.getParser();
		final Writer<T> writer = serviceConfig.getWriter();

		final T result = parser.parseLine(line);
		if (result != null) {
		    writer.consume(result);
		}
	    } while (line != null);
	    Logger.getLogger(this.getClass()).info("Successfully parsed file "+serviceConfig.getFileName());
	} catch (IOException e) {
	    Logger.getLogger(this.getClass()).error(e.getMessage());
	}
    }
}
