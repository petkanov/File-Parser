package com.egtinteractive.config;

import java.util.List;

import com.egtinteractive.app.ConnectionPool;

public class Config {

    private final List<ServiceConfig<?>> serviceConfigs;
    private final String logFileName;
    private final String workingDirectory;
    private final ConnectionPool connectionPool;
    
    public Config(final List<ServiceConfig<?>> services, final String logFileName, final String workingDirectory, final ConnectionPool connectionPool) {
	this.serviceConfigs = services;
	this.logFileName = logFileName;
	this.workingDirectory = workingDirectory;
	this.connectionPool = connectionPool;
    }

    public List<ServiceConfig<?>> getServices() {
	return serviceConfigs;
    }
    
    public String getLogFileName() {
	return this.logFileName;
    }

    public String getWorkingDirectory() {
	return this.workingDirectory;
    }

    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }

}