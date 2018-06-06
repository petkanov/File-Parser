package com.egtinteractive.config;

import java.util.List;

import com.egtinteractive.app.ConnectionPool;

public class Config {

    private final List<ServiceConfig<?>> serviceConfigs;
    private final String logFileName;
    private final String workingDirectory;
    private final ConnectionPool connectionPool;
    private final boolean clearRecoveryDatabaseOnStartup;
    
    public Config( List<ServiceConfig<?>> services, String logFileName, String workingDirectory, ConnectionPool connectionPool, boolean clearRecoveryDatabaseOnStartup) {
	this.serviceConfigs = services;
	this.logFileName = logFileName;
	this.workingDirectory = workingDirectory;
	this.connectionPool = connectionPool;
	this.clearRecoveryDatabaseOnStartup = clearRecoveryDatabaseOnStartup;
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

    public boolean isClearRecoveryDatabaseOnStartup() {
        return clearRecoveryDatabaseOnStartup;
    }

}