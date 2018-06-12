package com.egtinteractive.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.egtinteractive.app.FPLogger;
import com.egtinteractive.app.RecoveryManager;

public class AsyncService<T> implements Service {

    private final Runnable processingRunner;
    private final String fileNamePrefix;
    private final RecoveryManager recoveryManager;
    private final BlockingQueue<String> filesQueue;
    private final ExecutorService engine;
    private final FPLogger logger;

    public AsyncService(final ServiceConfig<T> serviceConfig, final RecoveryManager recoveryManager, final FPLogger logger) {
	this.filesQueue = new ArrayBlockingQueue<>(1024);
	this.engine = Executors.newSingleThreadExecutor();
	this.fileNamePrefix = serviceConfig.getFileNamePrefix();
	this.recoveryManager = recoveryManager;
	this.logger = logger;
	processingRunner = serviceConfig.getProcessingRunner(filesQueue);
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
