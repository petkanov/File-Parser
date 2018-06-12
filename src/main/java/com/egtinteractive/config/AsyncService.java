package com.egtinteractive.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.egtinteractive.app.FPLogger;
import com.egtinteractive.app.Parser;
import com.egtinteractive.app.RecoveryManager;
import com.egtinteractive.app.Writer;

public class AsyncService<T> implements Service {

    private final Runnable processingRunner;
    private final String fileNamePrefix;
    private final RecoveryManager recoveryManager;
    private final BlockingQueue<String> filesQueue;
    private final ExecutorService engine;
    private final FPLogger logger;

    private class ProcessingRunner implements Runnable {
	private final Parser<T> parser;
	private final Writer<T> writer;

	public ProcessingRunner(final ServiceConfig<T> serviceConfig) {
	    this.parser = serviceConfig.getParser();
	    this.writer = serviceConfig.getWriter();
	}

	@Override
	public void run() { 
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
    }

    public AsyncService(final ServiceConfig<T> serviceConfig, final RecoveryManager recoveryManager, final FPLogger logger) {
	this.filesQueue = new ArrayBlockingQueue<>(1024);
	this.engine = Executors.newSingleThreadExecutor();
	this.fileNamePrefix = serviceConfig.getFileNamePrefix();
	this.recoveryManager = recoveryManager;
	this.logger = logger;
	this.processingRunner = new ProcessingRunner(serviceConfig);
    }

    public void acceptFile(String fileName) {
	if (fileName.contains(fileNamePrefix) && !recoveryManager.isFileProcessed(fileName)) {
	    try {
		filesQueue.put(fileName);
	    } catch (InterruptedException e) {
		logger.logErrorMessage(this.getClass(), e.getMessage());
		throw new RuntimeException(e);
	    }
	    engine.execute(processingRunner);
	}
    }
}
