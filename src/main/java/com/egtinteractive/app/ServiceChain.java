package com.egtinteractive.app;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;

public abstract class ServiceChain {
    
    protected ServiceChain nextLink;
    private final String fileNamePrefix;
    
    protected final BlockingQueue<String> filesQueue = new ArrayBlockingQueue<>(1024);
    protected final ExecutorService engine = Executors.newSingleThreadExecutor();
    
    public ServiceChain(final String fileNamePrefix) {
	this.fileNamePrefix = fileNamePrefix;
    }
    
    public void acceptFile(String fileName) {
	if (fileName.contains(fileNamePrefix) && !RecoveryManager.isFileProcessed(fileName)) {
	    try {
		filesQueue.put(fileName);
	    } catch (InterruptedException e) {
		Logger.getLogger(this.getClass()).error(e.getMessage());
	    }
	    startProcessing();
	}
	if (nextLink != null) {
	    nextLink.acceptFile(fileName);
	}
    }
    
    protected abstract void startProcessing();

    public void setNextLink(ServiceChain nextLink) {
	this.nextLink = nextLink;
    }

    public ServiceChain getNextLink() {
	return this.nextLink;
    }
}
