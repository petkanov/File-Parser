package com.egtinteractive.app;

public class InfluxDbWriter<T> implements Writer<T> {

    private final String someField;

    public InfluxDbWriter(String someField) {
	this.someField = someField;
    }

    public String getSomeField() {
	return someField;
    }

    @Override
    public void consume(T result) {
	System.out.println(result);
    }

}
