package com.egtinteractive;

import java.util.concurrent.BlockingQueue;
import org.mockito.Mockito;
import org.testng.annotations.Test;
import com.egtinteractive.app.FPLogger;
import com.egtinteractive.app.Parser;
import com.egtinteractive.app.ProcessingRunner;
import com.egtinteractive.app.ProcessingRunnerImpl;
import com.egtinteractive.app.RecoveryManager;
import com.egtinteractive.app.Writer;

public class ProcessingRunnerImplLoggerTest {

    @SuppressWarnings("unchecked")
    @Test
    public void runMethodTest() throws Exception { 

	final Parser<Object> parser = Mockito.mock(Parser.class);
	final Writer<Object> writer = Mockito.mock(Writer.class);
	final RecoveryManager recoveryManager = Mockito.mock(RecoveryManager.class);
	final BlockingQueue<String> filesQueue = Mockito.mock(BlockingQueue.class);
	final FPLogger logger = Mockito.mock(FPLogger.class);
	final String testFileName = "src/test/resources/1618LinesFile";
	
	Mockito.doNothing().when(logger).logInfoMessage(Mockito.isA(Class.class),Mockito.isA(String.class));
	Mockito.when(filesQueue.take()).thenReturn(testFileName);
	Mockito.when(parser.parseLine(Mockito.isA(String.class))).thenReturn(new Object());
	Mockito.when(writer.consume(Mockito.isA(String.class))).thenReturn(false);
	
	ProcessingRunner<Object> pri = new ProcessingRunnerImpl<>();
	pri.setUp(parser, writer, filesQueue, recoveryManager, logger);
	pri.run();
	
	Mockito.verify(filesQueue, Mockito.times(1)).take();
	final int parsedLinesCount = 1619; // plus 'null' line
	Mockito.verify(parser, Mockito.times(parsedLinesCount)).parseLine(Mockito.isA(String.class)); 
	Mockito.verify(writer, Mockito.times(parsedLinesCount)).consume(Mockito.isA(Object.class));  
	Mockito.verify(logger, Mockito.times(1)).logInfoMessage(Mockito.isA(Class.class),Mockito.isA(String.class)); 
	Mockito.verify(recoveryManager, Mockito.times(1)).updateFileProcessingProgress(Mockito.isA(String.class),Mockito.isA(String.class),Mockito.isA(Integer.class)); 
    }
}

 



