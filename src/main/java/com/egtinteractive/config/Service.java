package com.egtinteractive.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.egtinteractive.app.Parser;
import com.egtinteractive.app.Writer;

public class Service<T> {

    private final ServiceConfig<T> serviceConfig;

    public Service(ServiceConfig<T> serviceConfig) {
	this.serviceConfig = serviceConfig;
    }

    public void start() throws IOException {
	try (BufferedReader br = new BufferedReader(new FileReader(new File(serviceConfig.getFileName())))) {
	    String line = null;
	    while ((line = br.readLine()) != null) {
		final Parser<T> parser = serviceConfig.getParser();
		final Writer<T> writer = serviceConfig.getWriter();

		final T result = parser.parseLine(line);
		writer.consume(result);
	    }
	}
    }
}
