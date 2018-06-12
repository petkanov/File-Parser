package com.egtinteractive.app.parsers;

public interface Parser<T> {

    public T parseLine(String line);

}
