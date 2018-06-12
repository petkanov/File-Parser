package com.egtinteractive.app;

import java.util.concurrent.BlockingQueue;

public interface ProcessingRunner<T> extends Runnable {

    void setFilesQueue(BlockingQueue<String> filesQueue);

}
