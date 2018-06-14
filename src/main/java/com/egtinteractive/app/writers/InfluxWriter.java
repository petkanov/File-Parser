package com.egtinteractive.app.writers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.egtinteractive.app.moduls.ResponseData;

public class InfluxWriter<T> implements Writer<T> {

    private final int pointPerBatch;
    private final URL urlInsertDataQuery;
    private StringBuilder pointsBatch;
    private int writtenPointsCounter;

    public InfluxWriter(final String hostInsertDataQuery, final int pointsPerBatch) {
	this.pointPerBatch = pointsPerBatch;
	try {
	    this.urlInsertDataQuery = new URL(hostInsertDataQuery);
	} catch (MalformedURLException e) {
	    throw new RuntimeException(e);
	}
    }

    @Override
    public boolean consume(final T result) {
	final ResponseData rd = (ResponseData) result;
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
	    initiateQuery();
	    pointsBatch = null;
	    writtenPointsCounter = 0;
	    return true;
	}
	return false;
    }

    @Override
    public void flush() {
	initiateQuery();
    }

    private void initiateQuery() {
	HttpURLConnection conn = null;
	OutputStream os = null;
	try {
	    conn = (HttpURLConnection) urlInsertDataQuery.openConnection();
	    conn.setRequestMethod("POST");
	    conn.setDoOutput(true);

	    os = conn.getOutputStream();
	    os.write(pointsBatch.toString().getBytes());
	    os.flush();
	    if (!(conn.getResponseCode() >= 200 && conn.getResponseCode() < 300)) {
		throw new RuntimeException("writePointsBath(): Failed batch recording");
	    }
	} catch (Exception e) {
	    throw new RuntimeException(e);
	} finally {
	    try {
		os.close();
	    } catch (IOException e) {
	    }
	}
    }
}
