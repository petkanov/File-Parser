package com.egtinteractive.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.BlockingQueue;

public class ProcessingRunnerImpl<T> implements ProcessingRunner<T> {

    private final Parser<T> parser;
    private final Writer<T> writer;
    private final RecoveryManager recoveryManager;
    private final FPLogger logger;
    private BlockingQueue<String> filesQueue;

    public ProcessingRunnerImpl(final Parser<T> parser, final Writer<T> writer, final RecoveryManager recoveryManager, final FPLogger logger) {
	this.parser = parser;
	this.writer = writer;
	this.recoveryManager = recoveryManager;
	this.logger = logger;
    }
    
    @Override
    public void run() {
	if (filesQueue == null) {
	    logger.logErrorMessage(this.getClass(), "ProcessingRunner has no FilesQueue set!");
	    return;
	}
	final String parserName = parser.getClass().getName();
	int lineCounter = 0;
	String fileName = null;
	try {
	    fileName = filesQueue.take();
	} catch (InterruptedException e) {
	    recoveryManager.removeFromAlreadySeenFiles(fileName);
	    logger.logErrorMessage(this.getClass(), e.getMessage());
	    return;
	} 
	final int lineToStartParsingFrom = recoveryManager.getLineOfLastParsedObject(parserName, fileName);

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
			recoveryManager.updateFileProcessingProgress(parserName, fileName, lineCounter + 1);
		    }
		}
	    } while (line != null);
	    recoveryManager.updateFileProcessingProgress(parserName, fileName, -1);
	    logger.logInfoMessage(this.getClass(), "Successfully parsed file " + fileName);
	} catch (Exception e) {
	    recoveryManager.removeFromAlreadySeenFiles(fileName);
	    logger.logErrorMessage(this.getClass(), e.getMessage());
	}
    }

    @Override
    public void setFilesQueue(final BlockingQueue<String> filesQueue) { 
	this.filesQueue = filesQueue;
    }
}
