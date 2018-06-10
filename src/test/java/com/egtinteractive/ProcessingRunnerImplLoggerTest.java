package com.egtinteractive;

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.testng.annotations.Test;

import com.egtinteractive.app.FPLogger;
import com.egtinteractive.app.Parser;
import com.egtinteractive.app.ProcessingRunnerImpl;
import com.egtinteractive.app.RecoveryManager;
import com.egtinteractive.app.Writer;

public class ProcessingRunnerImplLoggerTest {

    

    @SuppressWarnings("unchecked")
    @Test
    public void runMethodTest() throws Exception { 

	final Parser<?> parser = Mockito.mock(Parser.class);
	final Writer<?> writer = Mockito.mock(Writer.class);
	final RecoveryManager recoveryManager = Mockito.mock(RecoveryManager.class);
	final BlockingQueue<String> filesQueue = Mockito.mock(BlockingQueue.class);
	final FPLogger logger = Mockito.mock(FPLogger.class);
	final String testFileName = "src/test/resources/1618LinesFile";
 
	
	
	
	PowerMockito.mockStatic(Logger.class);
	Logger log = PowerMockito.mock(Logger.class);
	System.out.println(log+"-------------------------------------------------------------------------");
	Mockito.doNothing().when(log).info(String.class);
	Mockito.when(Logger.getLogger(Class.class)).thenReturn(log);
	
	Mockito.when(filesQueue.take()).thenReturn(testFileName);
	
//	Mockito.when(Logger.getLogger(Class.class)).thenReturn(Logger.getLogger(Class.class));
	
	ProcessingRunnerImpl pri = new ProcessingRunnerImpl();
	pri.setUp(parser, writer, filesQueue, recoveryManager, logger);
	pri.run();
	
//	Assert.assertEquals("AES", Logger.getLogger());
//	Assert.assertEquals("DES", Logger.getMethod());
	
	
	
//
//	final Runnable processingRunner = Mockito.mock(Runnable.class);
//	final ServiceConfig<?> serviceConfig = Mockito.mock(ServiceConfig.class);
//	final String fileNamePrefix = "bla";
//	final ExecutorService engine = Mockito.mock(ExecutorService.class);
//
//	Mockito.when(serviceConfig.getFileNamePrefix()).thenReturn(fileNamePrefix);
//	Mockito.when(serviceConfig.getProcessingRunner(Mockito.isA(BlockingQueue.class), Mockito.isA(RecoveryManager.class)))
//		.thenReturn(processingRunner);
//	Mockito.when(recoveryManager.isFileProcessed(Mockito.isA(String.class))).thenReturn(true);
//	Mockito.doNothing().when(filesQueue).put(Mockito.isA(String.class));
//	final Service<?> service = new Service<>(serviceConfig, recoveryManager);
//	service.acceptFile(fileNamePrefix + "file-name");
//
//	Mockito.verify(engine, Mockito.times(0)).execute(Mockito.isA(Runnable.class));
//	Mockito.verify(filesQueue, Mockito.times(0)).put(Mockito.isA(String.class));
    }
}

 



