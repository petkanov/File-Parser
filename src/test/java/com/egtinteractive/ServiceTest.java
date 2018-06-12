package com.egtinteractive;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import org.mockito.Mockito;
import org.testng.annotations.Test;

import com.egtinteractive.app.moduls.logger.FPLogger;
import com.egtinteractive.app.moduls.mysql.RecoveryManager;
import com.egtinteractive.app.services.AsyncService;
import com.egtinteractive.app.services.Service;
import com.egtinteractive.config.ServiceConfig;

public class ServiceTest {

    @SuppressWarnings("unchecked")
    @Test
    public void newFileExecutionTest() throws Exception {
	final ServiceConfig<?> serviceConfig = Mockito.mock(ServiceConfig.class);
	final String fileNamePrefix = "bla";
	final RecoveryManager recoveryManager = Mockito.mock(RecoveryManager.class);
	final FPLogger logger = Mockito.mock(FPLogger.class);
	final BlockingQueue<String> filesQueue = Mockito.mock(BlockingQueue.class);
	final ExecutorService engine = Mockito.mock(ExecutorService.class);

	Mockito.when(serviceConfig.getFileNamePrefix()).thenReturn(fileNamePrefix); 
	Mockito.when(recoveryManager.isFileProcessed(Mockito.isA(String.class))).thenReturn(false);
	Mockito.doNothing().when(filesQueue).put(Mockito.isA(String.class));
	final Service service = new AsyncService<>(serviceConfig, recoveryManager, logger);
	setFinalFieldValue(service, filesQueue, "filesQueue");
	setFinalFieldValue(service, engine, "engine");
	service.acceptFile(fileNamePrefix + "file-name");

	Mockito.verify(engine, Mockito.times(1)).execute(Mockito.isA(Runnable.class));
	Mockito.verify(filesQueue, Mockito.times(1)).put(Mockito.isA(String.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void newFileNotExecutionTest() throws Exception {
	final ServiceConfig<?> serviceConfig = Mockito.mock(ServiceConfig.class);
	final String fileNamePrefix = "bla";
	final RecoveryManager recoveryManager = Mockito.mock(RecoveryManager.class);
	final FPLogger logger = Mockito.mock(FPLogger.class);
	final BlockingQueue<String> filesQueue = Mockito.mock(BlockingQueue.class);
	final ExecutorService engine = Mockito.mock(ExecutorService.class);

	Mockito.when(serviceConfig.getFileNamePrefix()).thenReturn(fileNamePrefix); 
	Mockito.when(recoveryManager.isFileProcessed(Mockito.isA(String.class))).thenReturn(true);
	Mockito.doNothing().when(filesQueue).put(Mockito.isA(String.class));
	final Service service = new AsyncService<>(serviceConfig, recoveryManager, logger);
	setFinalFieldValue(service, filesQueue, "filesQueue");
	setFinalFieldValue(service, engine, "engine");
	service.acceptFile(fileNamePrefix + "file-name");

	Mockito.verify(engine, Mockito.times(0)).execute(Mockito.isA(Runnable.class));
	Mockito.verify(filesQueue, Mockito.times(0)).put(Mockito.isA(String.class));
    }

    public void setFinalFieldValue(Object objWithFinalField, Object valueForFinalField, String finalFieldName) throws Exception {
	final Field finalField = AsyncService.class.getDeclaredField(finalFieldName);
	final Field modifierField = Field.class.getDeclaredField("modifiers");
	finalField.setAccessible(true);
	modifierField.setAccessible(true);
	int modifiers = finalField.getModifiers();
	modifiers = modifiers & ~Modifier.FINAL;
	modifierField.setInt(finalField, modifiers);

	finalField.set(objWithFinalField, valueForFinalField);
    }
}
