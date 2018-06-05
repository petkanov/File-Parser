package com.egtinteractive.app;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;

public abstract class ServiceChain {
    
    protected ServiceChain nextLink;
    private final String fileNamePrefix;
    
    protected final BlockingQueue<String> filesQueue = new ArrayBlockingQueue<>(1024);
    protected final Set<String> processedFiles = new HashSet<>();
    protected final ExecutorService engine = Executors.newSingleThreadExecutor();
    
    public ServiceChain(final String fileNamePrefix) {
	this.fileNamePrefix = fileNamePrefix;
    }

    
    protected abstract void startProcessing();
    
    public void acceptFile(String fileName) {
	if (fileName.contains(fileNamePrefix) && !processedFiles.contains(fileName)) {
	    System.out.println(fileNamePrefix + "::" + fileName);
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

    public void setNextLink(ServiceChain nextLink) {
	this.nextLink = nextLink;
    }

    public ServiceChain getNextLink() {
	return this.nextLink;
    }
}
