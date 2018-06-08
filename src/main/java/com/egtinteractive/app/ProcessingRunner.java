package com.egtinteractive.app;

import java.util.concurrent.BlockingQueue;

public interface ProcessingRunner<T> extends Runnable {

    void setUp(Parser<T> parser, Writer<T> writer, BlockingQueue<String> filesQueue);

}
