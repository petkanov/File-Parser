package com.egtinteractive.config;

import java.util.List;
import com.egtinteractive.app.RecoveryManager;

public class Config {

    private final List<ServiceConfig<?>> serviceConfigs;
    private final String logFileName;
    private final String workingDirectory;
    private final RecoveryManager recoveryManager;
    private final boolean clearRecoveryDatabaseOnStartup;
    
    public Config( List<ServiceConfig<?>> services, String logFileName, String workingDirectory,  final RecoveryManager recoveryManager, boolean clearRecoveryDatabaseOnStartup) {
	this.serviceConfigs = services;
	this.logFileName = logFileName;
	this.workingDirectory = workingDirectory;
	this.recoveryManager = recoveryManager;
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

    public RecoveryManager getRecoveryManager() {
        return recoveryManager;
    }

    public boolean isClearRecoveryDatabaseOnStartup() {
        return clearRecoveryDatabaseOnStartup;
    }

}