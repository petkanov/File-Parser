package com.egtinteractive.config;

import java.io.File;
import com.thoughtworks.xstream.XStream;

public class FileLoader {
    private static final Config CONFIG;

    static {
	final XStream x = new XStream();
	CONFIG = (Config) x.fromXML(new File("configuration.xml"));
    }

    public static Config getConfiguration() {
	return CONFIG;
    }
}
