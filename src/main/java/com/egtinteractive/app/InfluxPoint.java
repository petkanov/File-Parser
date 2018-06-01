package com.egtinteractive.app;

public class InfluxPoint {

    private final String measurement;
    private final String tagValue;
    private final int fieldValue;
    private final long timeKey;
    
    public InfluxPoint(String measurement, String domane, int response, long time) {
	this.measurement = measurement;
	this.tagValue = domane;
	this.fieldValue = response;
	this.timeKey = time;
    }

    public String getMeasurement() {
        return measurement;
    }

    public String getTagValue() {
        return tagValue;
    }

    public int getFieldValue() {
        return fieldValue;
    }

    public long getTimeKey() {
        return timeKey;
    } 
    
    public String toLineProtocol() {
	return this.measurement+","+this.tagValue+" "+this.fieldValue+" "+this.timeKey;
    }
}