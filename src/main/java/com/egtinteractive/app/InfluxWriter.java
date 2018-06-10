package com.egtinteractive.app;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class InfluxWriter<T> implements Writer<T> {

    private StringBuilder pointsBatch;
    private final int pointPerBatch;
    private int writtenPointsCounter;
    
    final String hostCreateDBQuery; 
    final String hostCreateDBQueryParams; 
    final String hostInsertDataQuery;
    
    private boolean isDatabaseCreated;

    public InfluxWriter(final String hostInsertDataQuery, final int pointsPerBatch, final String hostCreateDBQuery, final String hostCreateDBQueryParams) {
	this.hostInsertDataQuery = hostInsertDataQuery;
	this.hostCreateDBQuery = hostCreateDBQuery;
	this.hostCreateDBQueryParams = hostCreateDBQueryParams; 
	this.pointPerBatch = pointsPerBatch; 
    } 

    @Override
    public boolean consume(final T result) {
	if(!isDatabaseCreated) {
	    createDatabase();
	}
	final ResponseData rd = (ResponseData) result;
	if (rd.getTime() == -1 && pointsBatch != null) {
	    writePointsBath();
	    return true;
	}
	final String point = "response,domane=" + rd.getDomane() + " response=" + rd.getResponse() + " " + rd.getTime();

	if (pointsBatch == null) {
	    pointsBatch = new StringBuilder();
	    pointsBatch.append(point);
	    writtenPointsCounter = 1;
	    return false;
	}
	pointsBatch.append(System.lineSeparator() + point);
	writtenPointsCounter++;

	if (writtenPointsCounter == pointPerBatch) {
	    writePointsBath();
	    pointsBatch = null;
	    writtenPointsCounter = 0;
	    return true;
	}
	return false;
    }

    private void writePointsBath() {
	URL url;
	HttpURLConnection conn;
	OutputStream os;
	try {
	    url = new URL(hostInsertDataQuery);
	    conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod("POST");
	    conn.setDoOutput(true);

	    os = conn.getOutputStream();
	    os.write(pointsBatch.toString().getBytes());
	    os.flush();
	    os.close();
	    if (!(conn.getResponseCode() >= 200 && conn.getResponseCode() < 300)) {
		throw new RuntimeException("Failed one batch recording");
	    }
	} catch (Exception e) {
	    throw new RuntimeException("writePointsBath(): " + e.toString());
	}
    }

    private void createDatabase() {
	URL url;
	HttpURLConnection conn;
	OutputStream os;
	try {
	    url = new URL(hostCreateDBQuery);
	    conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod("POST");
	    conn.setDoOutput(true);

	    os = conn.getOutputStream();
	    os.write(hostCreateDBQueryParams.getBytes());
	    os.flush();
	    os.close();
	    if (!(conn.getResponseCode() >= 200 && conn.getResponseCode() < 300)) {
		throw new RuntimeException("createDatabase(): Couldn't create database");
	    }
	    isDatabaseCreated = true;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }
}
