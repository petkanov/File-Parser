package com.egtinteractive.config;

import java.util.List;

import com.egtinteractive.app.moduls.logger.FPLogger;
import com.egtinteractive.app.moduls.mysql.RecoveryManager;

public class Config {

    private final List<ServiceConfig<?>> serviceConfigs;
    private final String logFileName;
    private final String workingDirectory;
    private final RecoveryManager recoveryManager;
    private final FPLogger logger;
    
    public Config( List<ServiceConfig<?>> services, String logFileName, String workingDirectory, RecoveryManager recoveryManager, FPLogger logger) {
	this.serviceConfigs = services;
	this.logFileName = logFileName;
	this.workingDirectory = workingDirectory;
	this.recoveryManager = recoveryManager;
	this.logger = logger;
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

    public RecoveryManager getRecoveryManager() {
        return recoveryManager;
    }

    public FPLogger getLogger() {
        return logger;
    } 
}