package com.egtinteractive.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.apache.log4j.Logger;
import com.egtinteractive.app.Parser;
import com.egtinteractive.app.RecoveryManager;
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
	final String parserName = parser.getClass().getSimpleName();
	int lineCounter = 0;
	String fileName = null;
	try {
	    fileName = filesQueue.take();
	} catch (InterruptedException e) {
	    Logger.getLogger(this.getClass()).error(e.getMessage());
	    return;
	}
	if (RecoveryManager.isFileProcessed(fileName)) {
	    Logger.getLogger(this.getClass()).warn("File: " + fileName + " is already processed!");
	    return;
	}
	final int lineToStartParsingFrom = RecoveryManager.getLineOfLastParsedObject(parserName, fileName);

	try (BufferedReader br = new BufferedReader(new FileReader(new File(fileName)))) {
	    String line = null;
	    do {
		line = br.readLine();
		lineCounter++;
		if (lineCounter < lineToStartParsingFrom) {
		    continue;
		}
		final T result = parser.parseLine(line);
		if (result != null) {
		    final boolean isWriteOperationDispatched = writer.consume(result);
		    if (isWriteOperationDispatched) {
			RecoveryManager.updateFileProcessingProgress(parserName, fileName, lineCounter + 1);
		    }
		}
	    } while (line != null);
	    RecoveryManager.saveFile(fileName);
	    RecoveryManager.updateFileProcessingProgress(parserName, fileName, -1);

	    Logger.getLogger(this.getClass()).info("Successfully parsed file " + fileName);
	} catch (Exception e) {
	    Logger.getLogger(this.getClass()).error(e.getMessage());
	}
    }
}
