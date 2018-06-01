package com.egtinteractive.config;

import java.util.List;

public class Config {

    private final List<ServiceConfig<?>> serviceConfigs;
    private final String logFileName;

    public Config(List<ServiceConfig<?>> services, String logFileName) {
	super();
	this.serviceConfigs = services;
	this.logFileName = logFileName;
    }

    public List<ServiceConfig<?>> getServices() {
	return serviceConfigs;
    }
    
    public String getLogFileName() {
	return this.logFileName;
    }

}