package com.egtinteractive.app;

public class SingleLineParserConfig<T> implements Parser<T> {

    private final String pattern;

    public SingleLineParserConfig(String pattern) {
	super();
	this.pattern = pattern;
    }

    public String getPattern() {
	return pattern;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T parseLine(String line) {
	System.out.println(this.getClass().getName() + ": " + line);
	return (T) line;
    }

}
