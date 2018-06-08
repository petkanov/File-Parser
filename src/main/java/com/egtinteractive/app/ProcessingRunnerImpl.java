package com.egtinteractive.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

public class ProcessingRunnerImpl<T> implements ProcessingRunner<T> {

    private Parser<T> parser;
    private Writer<T> writer;
    private BlockingQueue<String> filesQueue;

    @Override
    public void run() {
	if (filesQueue == null || parser == null || writer == null) {
	    Logger.getLogger(this.getClass()).error("ProcessingRunner has not been setUp correctly!");
	    return;
	}
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

    @Override
    public void setUp(final Parser<T> parser, final Writer<T> writer, final BlockingQueue<String> filesQueue) {
	this.filesQueue = filesQueue;
	this.parser = parser;
	this.writer = writer;
    }
}
