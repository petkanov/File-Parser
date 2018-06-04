package com.egtinteractive.app;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

public class InfluxWriter<T> implements Writer<T> {

    private static Object lock = new Object();

    private final String host;
    private final String database;
    private StringBuilder pointsBatch;
    private final int pointPerBatch;
    private int writtenPointsCounter;
    
    private ThreadLocal<StringBuilder> tLocal = new ThreadLocal<>();

    public InfluxWriter(final String host, final String database, final int pointsPerBatch) {
	this.host = host;
	this.database = database;
	this.pointPerBatch = pointsPerBatch;
	if (!createDatabase()) {
	    Logger.getLogger(this.getClass()).error("Failed DB Creation");
	}
	tLocal.set(pointsBatch);
    }

    public String getSomeField() {
	return host;
    }

    @Override
    public void consume(final T result) {
	final ResponseData rd = (ResponseData) result;
	if (rd.getTime() == -1 && pointsBatch != null) {
	    writePointsBath();
	    return;
	}
	final String point = "response,domane=" + rd.getDomane() + " response=" + rd.getResponse() + " " + rd.getTime();

	if (pointsBatch == null) {
	    pointsBatch = new StringBuilder();
	    pointsBatch.append(point);
	    writtenPointsCounter = 1;
	    return;
	}
	pointsBatch.append(System.lineSeparator() + point);
	writtenPointsCounter++;

	if (writtenPointsCounter == pointPerBatch) {
	    writePointsBath();
	    pointsBatch = null;
	    writtenPointsCounter = 0;
	}
    }

    private void writePointsBath() {
	synchronized (lock) {
	    System.out.println(host + "/write?db=" + database + "  ==================== OPEN");
	    final String hostInsert = host + "/write?db=" + database;
	    URL url;
	    HttpURLConnection conn;
	    OutputStream os;
	    try {
		url = new URL(hostInsert);
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);

		os = conn.getOutputStream();
		os.write(pointsBatch.toString().getBytes());
		os.flush();
		os.close();
		if (!(conn.getResponseCode() >= 200 && conn.getResponseCode() < 300)) {
		    Logger.getLogger(this.getClass()).error("Failed one batch recording");
		}
	    } catch (Exception e) {
		Logger.getLogger(this.getClass()).error("writePointsBath(): " + e.toString());
	    }
	    System.out.println(host + "/write?db=" + database + "  ==================== CLOSED ");

	}
    }

    private boolean createDatabase() {
	final String hostQuery = "http://localhost:8086/query";
	final String query = "q=CREATE DATABASE " + database;
	URL url;
	HttpURLConnection conn;
	OutputStream os;
	try {
	    url = new URL(hostQuery);
	    conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod("POST");
	    conn.setDoOutput(true);

	    os = conn.getOutputStream();
	    os.write(query.getBytes());
	    os.flush();
	    os.close();
	    if (!(conn.getResponseCode() >= 200 && conn.getResponseCode() < 300)) {
		return false;
	    }
	} catch (Exception e) {
	    Logger.getLogger(this.getClass()).error("Couldn't create database");
	    throw new RuntimeException(e);
	}
	return true;
    }
}
