package com.egtinteractive.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.egtinteractive.config.AsyncService;
import com.egtinteractive.config.Config;
import com.egtinteractive.config.FileLoader;
import com.egtinteractive.config.Service;
import com.egtinteractive.config.ServiceConfig;

public class FileParser { 
    
    public static void startApp() {
	final int timeDelay = 1200;
	final Config config = FileLoader.getConfiguration();
	final RecoveryManager recoveryManager = config.getRecoveryManager();
	final FPLogger logger = config.getLogger();
	logger.initializeLogger();
	recoveryManager.setLogger(logger);

	final List<Service> services = getServices(config);

	while (true) {
	    try {
		final File workingDir = new File(config.getWorkingDirectory());

		for (File file : workingDir.listFiles()) {
		    final String fileName = file.getAbsolutePath();
		    if (file.isFile() && !recoveryManager.isFileAlreadySeen(fileName)) {  
			for (Service service : services) {
			    service.acceptFile(fileName);
			}
			recoveryManager.addToAlreadySeenFiles(fileName);
		    }
		}
		Thread.sleep(timeDelay);
	    } catch (Exception e) {
		logger.logErrorMessage(App.class, e.getMessage());
	    }
	}
    }
    
    private static List<Service> getServices(final Config config) {
	final List<Service> services = new ArrayList<>();
	for (ServiceConfig<?> serviceConfig : config.getServices()) {
	    final Service service = new AsyncService<>(serviceConfig, config.getRecoveryManager(), config.getLogger());
	    services.add(service);
	}
	return services;
    }
}
