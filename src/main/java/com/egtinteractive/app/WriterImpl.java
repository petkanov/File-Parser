package com.egtinteractive.app;

public class WriterImpl<T> implements Writer<T> {

    private final String someField;

    public WriterImpl(String someField) {
	super();
	this.someField = someField;
    }

    public String getSomeField() {
	return someField;
    }

    @Override
    public void consume(T result) {
	System.out.println(this.getClass().getName() + ": " + result);
    }

}
