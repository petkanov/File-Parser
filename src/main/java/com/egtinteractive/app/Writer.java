package com.egtinteractive.app;

public interface Writer<T> {

    /**
     * Returns true only when write to DB operation has occurred
     */
    public boolean consume(T result);

}
