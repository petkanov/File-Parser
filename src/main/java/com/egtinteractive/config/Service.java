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
    private final BlockingQueue<String> filesQueue = new ArrayBlockingQueue<>(1024);
    private final ExecutorService engine = Executors.newSingleThreadExecutor();
    private ServiceChain nextLink;

    public Service(final ServiceConfig<T> serviceConfig) {
	this.fileNamePrefix = serviceConfig.getFileNamePrefix();
	processingRunner = serviceConfig.getProcessingRunner(filesQueue);
    }

    public void acceptFile(String fileName) {
	if (fileName.contains(fileNamePrefix) && !RecoveryManager.isFileProcessed(fileName)) {
	    try {
		filesQueue.put(fileName);
	    } catch (InterruptedException e) {
		Logger.getLogger(this.getClass()).error(e.getMessage());
	    }
	    engine.execute(this.processingRunner);
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
