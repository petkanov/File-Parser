package com.egtinteractive;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.egtinteractive.app.RecoveryManager;
import com.egtinteractive.config.Service;
import com.egtinteractive.config.ServiceConfig;    

public class ServiceTest {

    private final Runnable processingRunner = Mockito.mock(Runnable.class);
    private final ServiceConfig<?> serviceConfig = Mockito.mock(ServiceConfig.class);
    private final String fileNamePrefix = "bla";
    private final RecoveryManager recoveryManager = Mockito.mock(RecoveryManager.class);
    private final BlockingQueue<String> filesQueue = Mockito.mock(BlockingQueue.class);
    private final ExecutorService engine = Mockito.mock(ExecutorService.class);
    
    @Test
    public void test() throws Exception {

	Mockito.when(serviceConfig.getFileNamePrefix()).thenReturn("bla");
	Mockito.when(serviceConfig.getProcessingRunner(Mockito.isA(BlockingQueue.class),Mockito.isA(RecoveryManager.class))).thenReturn(processingRunner);
	Mockito.when(recoveryManager.isFileProcessed(Mockito.isA(String.class))).thenReturn(false);
	
	Mockito.doNothing().when(filesQueue).put(Mockito.isA(String.class));
	
	final Service service = new Service<>(serviceConfig, recoveryManager);
	
	setFinalFieldValue(service, filesQueue, "filesQueue");
	setFinalFieldValue(service, engine, "engine");
	
	
	service.acceptFile("bla");
	
//	PowerMockito.mockStatic(RecoveryManager.class);
//	when(RecoveryManager.isFileProcessed(isA(String.class))).thenReturn(true);
//	boolean result = RecoveryManager.isFileProcessed("SDSDD");
//	Mockito.verify(RecoveryManager.isFileProcessed(isA(String.class)),Mockito.times(1));

	Mockito.verify(engine,Mockito.times(1)).execute(Mockito.isA(Runnable.class));
	Assert.assertEquals(true,true);
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
