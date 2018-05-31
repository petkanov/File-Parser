package com.egtinteractive.config;

import com.egtinteractive.app.Parser;
import com.egtinteractive.app.Writer;

public class ServiceConfig<T> {

    private final String fileName;
    private final Parser<T> parser;
    private final Writer<T> writer;

    public ServiceConfig(String fileName, Parser<T> parser, Writer<T> writer) {
	super();
	this.fileName = fileName;
	this.parser = parser;
	this.writer = writer;
    }

    public String getFileName() {
	return fileName;
    }

    public Parser<T> getParser() {
	return parser;
    }

    public Writer<T> getWriter() {
	return writer;
    }
}
