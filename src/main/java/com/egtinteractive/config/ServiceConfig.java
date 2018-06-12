package com.egtinteractive.config;

import com.egtinteractive.app.parsers.Parser;
import com.egtinteractive.app.writers.Writer;

public class ServiceConfig<T> {

    private final String fileNamePrefix;
    private final Parser<T> parser;
    private final Writer<T> writer;

    public ServiceConfig(final String fileNamePrefix, final Parser<T> parser, final Writer<T> writer) {
	this.fileNamePrefix = fileNamePrefix;
	this.parser = parser;
	this.writer = writer;
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
}
