package com.egtinteractive.app;

public interface FPLogger {
    void logInfoMessage(Class<?> cls, String msg);

    void logWarnMessage(Class<?> cls, String msg);

    void logErrorMessage(Class<?> cls, String msg);

    void initializeLogger();
}
