package com.egtinteractive.app;

import com.egtinteractive.config.Bootstrap;

public class App {
    public static void main(String[] args) throws Exception {
	Bootstrap.generateConfig();
	FileParser.startApp();
    }
}