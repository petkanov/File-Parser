package com.egtinteractive.app.writers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.egtinteractive.app.moduls.ResponseData;

public class InfluxWriter<T> implements Writer<T> {

    private final int pointPerBatch;
    private final String hostCreateDBQueryParams;
    private final URL urlCreateDBQuery;
    private final URL urlInsertDataQuery;
    private StringBuilder pointsBatch;
    private boolean isDatabaseCreated;
    private int writtenPointsCounter;

    public InfluxWriter(final String hostInsertDataQuery, final int pointsPerBatch, final String hostCreateDBQuery,
	    final String hostCreateDBQueryParams) {
	this.hostCreateDBQueryParams = hostCreateDBQueryParams;
	this.pointPerBatch = pointsPerBatch;
	try {
	    this.urlCreateDBQuery = new URL(hostCreateDBQuery);
	    this.urlInsertDataQuery = new URL(hostInsertDataQuery);
	} catch (MalformedURLException e) {
	    throw new RuntimeException(e);
	}
    }

    @Override
    public boolean consume(final T result) {
	if (!isDatabaseCreated) {
	    isDatabaseCreated = initiateQuery(urlCreateDBQuery, hostCreateDBQueryParams);
	}
	final ResponseData rd = (ResponseData) result;
	if (rd.getTime() == -1 && pointsBatch != null) {
	    initiateQuery(urlInsertDataQuery, pointsBatch.toString());
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
	    if (!initiateQuery(urlInsertDataQuery, pointsBatch.toString())) {
		throw new RuntimeException("writePointsBath(): Failed batch recording");
	    }
	    pointsBatch = null;
	    writtenPointsCounter = 0;
	    return true;
	}
	return false;
    }

    public boolean initiateQuery(final URL url, final String data) {
	HttpURLConnection conn = null;
	OutputStream os = null;
	try {
	    conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod("POST");
	    conn.setDoOutput(true);
	    
	    os = conn.getOutputStream();
	    os.write(data.getBytes());
	    os.flush();
	    if (!(conn.getResponseCode() >= 200 && conn.getResponseCode() < 300)) {
		return false;
	    }
	    return true;
	} catch (Exception e) {
	    return false;
	} finally {
	    try {
		os.close();
	    } catch (IOException e) {
	    }
	}
    } 
}
