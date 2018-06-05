package com.egtinteractive.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.egtinteractive.app.Parser;
import com.egtinteractive.app.ServiceChain;
import com.egtinteractive.app.Writer;

public class Service<T> implements ServiceChain {

    protected ServiceChain nextLink;
    
    private final ServiceConfig<T> serviceConfig;
    final BlockingQueue<String> filesQueue = new ArrayBlockingQueue<>(1024);
    final Parser<T> parser; //= serviceConfig.getParser();
    final Writer<T> writer; // = serviceConfig.getWriter();
    
    final Set<String> processedFiles = new HashSet<>();

    final ExecutorService engine = Executors.newSingleThreadExecutor();

    public Service(final ServiceConfig<T> serviceConfig) {
	this.serviceConfig = serviceConfig;
	 parser = serviceConfig.getParser();
	 writer = serviceConfig.getWriter();
    }

    private void startProcessing() {

	String tmp = null;
	try {
	    tmp = filesQueue.take();
	} catch (Exception e) {
	    Logger.getLogger(this.getClass()).error(e.getMessage());
	}
	final String fileName = tmp;

	engine.execute(new Runnable() {
	    @Override
	    public void run() {
		try (BufferedReader br = new BufferedReader(new FileReader(new File(fileName)))) { 
		    String line = null;
		    do {
			line = br.readLine();

			final T result = parser.parseLine(line); 
			if (result != null) {
			    writer.consume(result);
			}
		    } while (line != null);
		    processedFiles.add(fileName);
		    
		    Logger.getLogger(this.getClass()).info("Successfully parsed file "+fileName);
		} catch (Exception e) {
		    Logger.getLogger(this.getClass()).error(e.getMessage());
		}
	    }
	});
    }

    @Override
    public void acceptFile(String fileName) {
	if (fileName.contains(serviceConfig.getFileNamePrefix()) && !processedFiles.contains(fileName)) {
	    System.out.println(serviceConfig + "::" + fileName);
	    try {
		filesQueue.put(fileName);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	    startProcessing();
	}
	if (nextLink != null) {
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
