package com.egtinteractive.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;
import com.egtinteractive.app.RecoveryManager;

public class Service<T> implements ServiceChain {

    private final Runnable processingRunner;
    private final String fileNamePrefix;
    private final RecoveryManager recoveryManager;
    private final BlockingQueue<String> filesQueue;
    private final ExecutorService engine;
    private ServiceChain nextLink;

    public Service(final ServiceConfig<T> serviceConfig, final RecoveryManager recoveryManager) {
	this.filesQueue = new ArrayBlockingQueue<>(1024);
	this.engine = Executors.newSingleThreadExecutor();
	this.fileNamePrefix = serviceConfig.getFileNamePrefix();
	this.recoveryManager = recoveryManager;
	processingRunner = serviceConfig.getProcessingRunner(filesQueue, recoveryManager);
    }

    public void acceptFile(String fileName) { 
	if (fileName.contains(fileNamePrefix) && !recoveryManager.isFileProcessed(fileName)) {
	    try {
		filesQueue.put(fileName);
	    } catch (InterruptedException e) {
		recoveryManager.removeFromAlreadySeenFiles(fileName);
		Logger.getLogger(this.getClass()).error(e.getMessage());
	    }
	    engine.execute(processingRunner);
	}
	if (nextLink != null) {
	    nextLink.acceptFile(fileName);
	}
    }

    public void setNextLink(ServiceChain nextLink) {
	this.nextLink = nextLink;
    }

    public ServiceChain getNextLink() {
	return this.nextLink;
    }
}
