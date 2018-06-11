package com.egtinteractive;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.testng.annotations.Test;

import com.egtinteractive.app.InfluxWriter;

public class InfluxWriterTest {

    @Test
    public void consumeTest() throws Exception {

	
	
	
	URL u = PowerMockito.mock(URL.class);
        String url = "http://www.sdsgle.com";
        PowerMockito.whenNew(URL.class).withArguments(url).thenReturn(u);
        HttpURLConnection huc = PowerMockito.mock(HttpURLConnection.class);
        PowerMockito.when(u.openConnection()).thenReturn(huc);
        PowerMockito.when(huc.getResponseCode()).thenReturn(200);
	
	
	
	
	
	
	
	
	OutputStream os = Mockito.mock(OutputStream.class);
	URLConnection conn = Mockito.mock(URLConnection.class);
//	Mockito.doNothing().when(conn).setRequestMethod(Mockito.isA(String.class));
//	Mockito.doNothing().when(conn).setDoOutput(Mockito.isA(Boolean.class));
	
//	URL urlCreateDBQuery = PowerMockito.mock(URL.class);
//	URL urlInsertDataQuery = PowerMockito.mock(URL.class);
//	Mockito.when(urlCreateDBQuery.openConnection()).thenReturn(conn);
//	Mockito.when(urlInsertDataQuery.openConnection()).thenReturn(conn);
	
	InfluxWriter<?> writer = new InfluxWriter<>("http://someadress.url", 13, "http://otheradress.url","hostCreateDBQueryParams");
	
//	setFinalFieldValue(writer, urlCreateDBQuery, "urlCreateDBQuery");
//	setFinalFieldValue(writer, urlInsertDataQuery, "urlInsertDataQuery");
	
	
	
	
	
	
	
	
	
	
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
    
    public void setFinalFieldValue(Object objWithFinalField, Object valueForFinalField, String finalFieldName) throws Exception {
	Field finalField = InfluxWriter.class.getDeclaredField(finalFieldName);
	Field modifierField = Field.class.getDeclaredField("modifiers");
	finalField.setAccessible(true);
	modifierField.setAccessible(true);
	int modifiers = finalField.getModifiers();
	modifiers = modifiers & ~Modifier.FINAL;
	modifierField.setInt(finalField, modifiers);

	finalField.set(objWithFinalField, valueForFinalField);
    }
}
