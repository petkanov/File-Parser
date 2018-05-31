package com.egtinteractive.app;

public class ResponseData {

    private final int response;
    private final String domane;
    private final long time;
    
    public ResponseData(int response, String domane, long time) {
	super();
	this.response = response;
	this.domane = domane;
	this.time = time;
    }

    public int getResponse() {
        return response;
    }

    public String getDomane() {
        return domane;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
	return "ResponseData [response=" + response + ", domane=" + domane + ", time=" + time + "]";
    }
}