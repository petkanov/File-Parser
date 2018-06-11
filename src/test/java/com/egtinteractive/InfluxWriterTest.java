package com.egtinteractive;

import java.awt.Point;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;
import org.testng.annotations.Test;

import com.egtinteractive.app.InfluxWriter;
import com.egtinteractive.app.ResponseData;
import com.egtinteractive.app.Writer;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.spy;

@PrepareForTest(InfluxWriter.class)
public class InfluxWriterTest {

    @SuppressWarnings("unchecked")
    @Test
    public void consumeTest() throws Exception {

	
	
	OutputStream os = Mockito.mock(OutputStream.class);
	HttpURLConnection conn = Mockito.mock(HttpURLConnection.class);
	URL urlCreateDBQuery = Mockito.mock(URL.class);
	URL urlInsertDataQuery = Mockito.mock(URL.class);
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	final InfluxWriter<Object> writer = Mockito.mock(InfluxWriter.class);
//	 Mockito.doNothing().when(writer).createDatabase();
//	writer.consume(new Object());
//	Mockito.verify(writer, Mockito.times(1)).createDatabase();
	
	// final Parser<Object> parser = Mockito.mock(Parser.class);
	// final RecoveryManager recoveryManager =
	// Mockito.mock(RecoveryManager.class);
	// final BlockingQueue<String> filesQueue =
	// Mockito.mock(BlockingQueue.class);
	// final FPLogger logger = Mockito.mock(FPLogger.class);
	// final String testFileName = "src/test/resources/1618LinesFile";
	//
	// Mockito.when(filesQueue.take()).thenReturn(testFileName);
	// Mockito.when(parser.parseLine(Mockito.isA(String.class))).thenReturn(new
	// Object());
	// Mockito.when(writer.consume(Mockito.isA(String.class))).thenReturn(false);
	//
	// ProcessingRunner<Object> pri = new ProcessingRunnerImpl<>();
	// pri.setUp(parser, writer, filesQueue, recoveryManager, logger);
	// pri.run();
	//
//	Mockito.verify(filesQueue, Mockito.times(1)).take();
//	final int parsedLinesCount = 1619; // plus 'null' line
//	Mockito.verify(writer, Mockito.times(parsedLinesCount)).consume(Mockito.isA(Object.class));
//	Mockito.verify(logger, Mockito.times(1)).logInfoMessage(Mockito.isA(Class.class), Mockito.isA(String.class));
//	Mockito.verify(recoveryManager, Mockito.times(1)).saveFile(Mockito.isA(String.class));
//	Mockito.verify(recoveryManager, Mockito.times(1)).updateFileProcessingProgress(Mockito.isA(String.class), Mockito.isA(String.class),
//		Mockito.isA(Integer.class));
    }
}
