package com.egtinteractive;

import java.net.URL;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.egtinteractive.app.moduls.ResponseData;
import com.egtinteractive.app.writers.InfluxWriter;
import com.egtinteractive.app.writers.Writer;

public class InfluxWriterTest {

    @Test
    public void consumeTest() throws Exception {

	final ResponseData point1 = new ResponseData(131, "thanos", 110);
	final ResponseData point2 = new ResponseData(313, "thanos", 111);
	
	final Writer<ResponseData> writer = new InfluxWriter<>("http://localhost:16661/write?db=someDb3", 2,
		"http://localhost:16661/query", "q=CREATE DATABASE someDb3");
	writer.consume(point1);
	Assert.assertTrue( writer.consume(point2) ); // means DB created & ResponseData points recorded
    }
    
    @Test
    public void initiateQueryTest() throws Exception {
	
	final String data = "big secret";
	final InfluxWriter<ResponseData> writer = new InfluxWriter<>("http://localhost:16661/write?db=someDb3", 1,
		"http://localhost:16661/query", "q=CREATE DATABASE someDb3");
	final boolean isDataReceived = writer.initiateQuery(new URL("http://localhost:16661/postdatareceiver"), data);
	Assert.assertTrue(isDataReceived);
    } 
}
