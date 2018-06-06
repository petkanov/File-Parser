package com.egtinteractive.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.apache.log4j.Logger;

import com.egtinteractive.app.Parser;
import com.egtinteractive.app.RecoveryManager;
import com.egtinteractive.app.ServiceChain;
import com.egtinteractive.app.Writer;

public class Service<T> extends ServiceChain {
    
    private final Parser<T> parser;
    private final Writer<T> writer;
    
    public Service(final ServiceConfig<T> serviceConfig) {
	super(serviceConfig.getFileNamePrefix());
	parser = serviceConfig.getParser();
	writer = serviceConfig.getWriter();
    }

    protected void startProcessing() {
	engine.execute(new Runnable() {
	    @Override
	    public void run() {

		String fileName = null;
		try {
		    fileName = filesQueue.take();
		} catch (Exception e) {
		    Logger.getLogger(this.getClass()).error(e.getMessage());
		    return;
		}
		if(RecoveryManager.isFileProcessed(fileName)) {
		    System.out.println("File: "+fileName+" is already processed!");
		    Logger.getLogger(this.getClass()).warn("File: "+fileName+" is already processed!");
		    return;
		}
		try (BufferedReader br = new BufferedReader(new FileReader(new File(fileName)))) {
		    String line = null;
		    do {
			line = br.readLine();

			final T result = parser.parseLine(line);
			if (result != null) {
			    writer.consume(result);
			}
		    } while (line != null);
		    RecoveryManager.saveFile(fileName);

		    Logger.getLogger(this.getClass()).info("Successfully parsed file " + fileName);
		} catch (Exception e) {
		    Logger.getLogger(this.getClass()).error(e.getMessage());
		}
	    }
	});
    }
}
