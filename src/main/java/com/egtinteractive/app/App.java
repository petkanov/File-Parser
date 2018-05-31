package com.egtinteractive.app;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import com.egtinteractive.config.Config;
import com.egtinteractive.config.FileLoader;
import com.egtinteractive.config.Service;
import com.egtinteractive.config.ServiceConfig;
import com.thoughtworks.xstream.XStream;

public class App {
    public static void main(String[] args) throws Exception {

	String host = "http://localhost:8086/write?db=someDb3";
	String point = "response,domaine=http://127.0.0.1:8040/EGTPortalBean/Withdraw response=140i 1826669569311009876";

	URL url = new URL(host);

	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	conn.setRequestMethod("POST");
	conn.setDoOutput(true);
	
	
	OutputStream os = conn.getOutputStream();
	os.write(point.getBytes());
	os.flush();
	os.close();
	System.out.println(conn.getResponseCode());
	
//	DataOutputStream out = new DataOutputStream(conn.getOutputStream());
//	System.out.println(conn.);
//	out.writeBytes(point);
//	out.flush();
//	out.close();
	
	
//	OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//	wr.write(point);
//	wr.flush();

	// generateConfig();
	// readConfig();
    }

    private static void readConfig() throws IOException {
	final Config config = FileLoader.getConfiguration();
	for (ServiceConfig<?> serviceConfig : config.getServices()) {
	    new Service<>(serviceConfig).start();
	}
    }

    private static void generateConfig() throws FileNotFoundException {
	final List<ServiceConfig<?>> services = new LinkedList<>();

	final Parser<ResponseData> parser = new ResponseTimeDomaneParser<>("some pattern");
	final Writer<ResponseData> writer = new InfluxDbWriter<>("someField");

	services.add(new ServiceConfig<ResponseData>("/big_device/veto/micro.log.thanos", parser, writer));

	final XStream x = new XStream();
	final Config config = new Config(services);
	x.toXML(config, new FileOutputStream(new File("configuration.xml")));
    }
}
