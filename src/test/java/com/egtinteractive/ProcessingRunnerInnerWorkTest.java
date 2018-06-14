package com.egtinteractive;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.mockito.Mockito;
import org.testng.annotations.Test;

import com.egtinteractive.app.moduls.logger.FPLogger;
import com.egtinteractive.app.moduls.recovery.RecoveryManager;
import com.egtinteractive.app.parsers.ResponseTimeDomainParser;
import com.egtinteractive.app.services.AsyncService;
import com.egtinteractive.app.services.Service;
import com.egtinteractive.app.writers.Writer;
import com.egtinteractive.config.ServiceConfig;

public class ProcessingRunnerInnerWorkTest {

    @SuppressWarnings("unchecked")
    @Test
    public void runMethodTest() throws Exception { 
	
	final String fileNamePrefix = "bla";
	final ExecutorService engine = Executors.newSingleThreadExecutor(); 
	final ResponseTimeDomainParser<Object> parser = Mockito.mock(ResponseTimeDomainParser.class);
	final Writer<Object> writer = Mockito.mock(Writer.class);
	final ServiceConfig<?> serviceConfig = new ServiceConfig<>(fileNamePrefix,parser,writer);
	final RecoveryManager recoveryManager = Mockito.mock(RecoveryManager.class);
	final FPLogger logger = Mockito.mock(FPLogger.class);
	final BlockingQueue<String> filesQueue = Mockito.mock(BlockingQueue.class);
	final String testFileName = "src/test/resources/1618LinesFile";

	Mockito.when(recoveryManager.isFileProcessed(Mockito.isA(String.class))).thenReturn(false);
	Mockito.doNothing().when(filesQueue).put(Mockito.isA(String.class));
	Mockito.when(filesQueue.take()).thenReturn(testFileName);
	
	Mockito.doNothing().when(logger).logInfoMessage(Mockito.isA(Class.class),Mockito.isA(String.class));
	Mockito.when(parser.parseLine(Mockito.isA(String.class))).thenReturn(new Object());
	Mockito.when(writer.consume(Mockito.isA(String.class))).thenReturn(false);
	
	final Service service = new AsyncService<>(serviceConfig, recoveryManager, logger);
	setFinalFieldValue(service, filesQueue, "filesQueue");
	setFinalFieldValue(service, engine, "engine");
	service.acceptFile(fileNamePrefix + "file-name");

	engine.shutdown();
	engine.awaitTermination(1, TimeUnit.DAYS);
	
	Mockito.verify(filesQueue, Mockito.times(1)).take();
	final int parsedLinesCount = 1619; // plus one
	Mockito.verify(parser, Mockito.times(parsedLinesCount)).parseLine(Mockito.isA(String.class)); 
	Mockito.verify(writer, Mockito.times(parsedLinesCount)).consume(Mockito.isA(Object.class));  
	Mockito.verify(logger, Mockito.times(1)).logInfoMessage(Mockito.isA(Class.class),Mockito.isA(String.class)); 
	Mockito.verify(recoveryManager, Mockito.times(1)).updateFileProcessingProgress(Mockito.isA(String.class),Mockito.isA(String.class),Mockito.isA(Integer.class)); 
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

 



