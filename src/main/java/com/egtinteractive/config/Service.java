package com.egtinteractive.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.egtinteractive.app.Parser;
import com.egtinteractive.app.ServiceChain;
import com.egtinteractive.app.Writer;

public class Service<T> implements ServiceChain{

    private final ServiceConfig<T> serviceConfig;
    protected ServiceChain nextLink;

    public Service(ServiceConfig<T> serviceConfig) {
	this.serviceConfig = serviceConfig;
    }

    public void start(String fileName) {

	try (BufferedReader br = new BufferedReader(new FileReader(new File(fileName)))) {
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
	    Logger.getLogger(this.getClass()).info("Successfully parsed file "+fileName);
	} catch (IOException e) {
	    Logger.getLogger(this.getClass()).error(e.getMessage());
	}
    }

    @Override
    public void acceptFile(String fileName) {
	if(fileName.contains(serviceConfig.getFileNamePrefix())) {
//	    System.out.println(serviceConfig+"::"+fileName);
	    start(fileName);
	}
	if(nextLink!=null) {
	    nextLink.acceptFile(fileName);
	}
    }

    @Override
    public void setNextLink(ServiceChain nextLink) {
	this.nextLink = nextLink;
    }

    @Override
    public ServiceChain getNextLink() {
	return this.nextLink;
    }
 
}
