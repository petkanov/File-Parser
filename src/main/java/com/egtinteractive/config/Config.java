package com.egtinteractive.config;

import java.util.List;

public class Config {

    private final List<ServiceConfig<?>> serviceConfigs;

    public Config(List<ServiceConfig<?>> services) {
	super();
	this.serviceConfigs = services;
    }

    public List<ServiceConfig<?>> getServices() {
	return serviceConfigs;
    }

}