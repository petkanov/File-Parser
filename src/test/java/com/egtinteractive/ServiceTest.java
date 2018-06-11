package com.egtinteractive;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import org.mockito.Mockito;
import org.testng.annotations.Test;

import com.egtinteractive.app.FPLogger;
import com.egtinteractive.app.RecoveryManager;
import com.egtinteractive.config.AsyncService;
import com.egtinteractive.config.Service;
import com.egtinteractive.config.ServiceConfig;

public class ServiceTest {

    @SuppressWarnings("unchecked")
    @Test
    public void newFileExecutionTest() throws Exception {
	final Runnable processingRunner = Mockito.mock(Runnable.class);
	final ServiceConfig<?> serviceConfig = Mockito.mock(ServiceConfig.class);
	final String fileNamePrefix = "bla";
	final RecoveryManager recoveryManager = Mockito.mock(RecoveryManager.class);
	final FPLogger logger = Mockito.mock(FPLogger.class);
	final BlockingQueue<String> filesQueue = Mockito.mock(BlockingQueue.class);
	final ExecutorService engine = Mockito.mock(ExecutorService.class);

	Mockito.when(serviceConfig.getFileNamePrefix()).thenReturn(fileNamePrefix);
	Mockito.when(serviceConfig.getProcessingRunner(Mockito.isA(BlockingQueue.class), Mockito.isA(RecoveryManager.class), Mockito.isA(FPLogger.class)))
		.thenReturn(processingRunner);
	Mockito.when(recoveryManager.isFileProcessed(Mockito.isA(String.class))).thenReturn(false);
	Mockito.doNothing().when(filesQueue).put(Mockito.isA(String.class));
	final AsyncService<?> service = new AsyncService<>(serviceConfig, recoveryManager, logger);
	setFinalFieldValue(service, filesQueue, "filesQueue");
	setFinalFieldValue(service, engine, "engine");
	service.acceptFile(fileNamePrefix + "file-name");

	Mockito.verify(engine, Mockito.times(1)).execute(Mockito.isA(Runnable.class));
	Mockito.verify(filesQueue, Mockito.times(1)).put(Mockito.isA(String.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void newFileNotExecutionTest() throws Exception {
	final Runnable processingRunner = Mockito.mock(Runnable.class);
	final ServiceConfig<?> serviceConfig = Mockito.mock(ServiceConfig.class);
	final String fileNamePrefix = "bla";
	final RecoveryManager recoveryManager = Mockito.mock(RecoveryManager.class);
	final FPLogger logger = Mockito.mock(FPLogger.class);
	final BlockingQueue<String> filesQueue = Mockito.mock(BlockingQueue.class);
	final ExecutorService engine = Mockito.mock(ExecutorService.class);

	Mockito.when(serviceConfig.getFileNamePrefix()).thenReturn(fileNamePrefix);
	Mockito.when(serviceConfig.getProcessingRunner(Mockito.isA(BlockingQueue.class), Mockito.isA(RecoveryManager.class), Mockito.isA(FPLogger.class)))
		.thenReturn(processingRunner);
	Mockito.when(recoveryManager.isFileProcessed(Mockito.isA(String.class))).thenReturn(true);
	Mockito.doNothing().when(filesQueue).put(Mockito.isA(String.class));
	final AsyncService<?> service = new AsyncService<>(serviceConfig, recoveryManager, logger);
	setFinalFieldValue(service, filesQueue, "filesQueue");
	setFinalFieldValue(service, engine, "engine");
	service.acceptFile(fileNamePrefix + "file-name");

	Mockito.verify(engine, Mockito.times(0)).execute(Mockito.isA(Runnable.class));
	Mockito.verify(filesQueue, Mockito.times(0)).put(Mockito.isA(String.class));
    }

    public void setFinalFieldValue(Object objWithFinalField, Object valueForFinalField, String finalFieldName) throws Exception {
	Field finalField = Service.class.getDeclaredField(finalFieldName);
	Field modifierField = Field.class.getDeclaredField("modifiers");
	finalField.setAccessible(true);
	modifierField.setAccessible(true);
	int modifiers = finalField.getModifiers();
	modifiers = modifiers & ~Modifier.FINAL;
	modifierField.setInt(finalField, modifiers);

	finalField.set(objWithFinalField, valueForFinalField);
    }
}
