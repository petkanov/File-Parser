package com.egtinteractive.config;

import java.util.concurrent.BlockingQueue;

import com.egtinteractive.app.Parser;
import com.egtinteractive.app.ProcessingRunner;
import com.egtinteractive.app.Writer;

public class ServiceConfig<T> {

    private final String fileNamePrefix;
    private final Parser<T> parser;
    private final Writer<T> writer;
    private final ProcessingRunner<T> processingRunner;

    public ServiceConfig(final String fileNamePrefix, final Parser<T> parser, final Writer<T> writer, final ProcessingRunner<T> processingRunner) {
	this.fileNamePrefix = fileNamePrefix;
	this.parser = parser;
	this.writer = writer;
	this.processingRunner = processingRunner;
    }

    public String getFileNamePrefix() {
	return fileNamePrefix;
    }

    public Parser<T> getParser() {
	return parser;
    }

    public Writer<T> getWriter() {
	return writer;
    }
    
    public Runnable getProcessingRunner(final BlockingQueue<String> filesQueue) {
	processingRunner.setFilesQueue(filesQueue);
	return processingRunner;
    } 
}
