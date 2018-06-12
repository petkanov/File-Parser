package com.egtinteractive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
 
@SuppressWarnings("restriction")
public class TestHttpServer {
    public static void main(String[] args) throws Exception {
	final int port = 16661;
	final HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
	server.createContext("/write", new WriteDataHandler());
        server.createContext("/query", new CreateDBHandler());
        server.createContext("/postdatareceiver", new PostDataReceiverHandler());
	server.setExecutor(null);
	server.start();
    }
}    

@SuppressWarnings("restriction")
class PostDataReceiverHandler implements HttpHandler {
    final String data = "big secret";
    
    @Override
    public void handle(HttpExchange he) throws IOException {
	final InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
	final BufferedReader br = new BufferedReader(isr);
	final String query = br.readLine();
	
	if(query.equals(data)) {
	    he.sendResponseHeaders(200, 0); 
	}
	else {
	    he.sendResponseHeaders(400, 0);
	} 
    } 
}

@SuppressWarnings("restriction")
class CreateDBHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange he) throws IOException {

	final InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
	final BufferedReader br = new BufferedReader(isr);
	final String query = br.readLine();

	if(query.equals("q=CREATE DATABASE someDb3")) {
	    he.sendResponseHeaders(200, 0); 
	}
	else {
	    he.sendResponseHeaders(400, 0);
	} 
    } 
}

@SuppressWarnings("restriction")
class WriteDataHandler implements HttpHandler {
    final String twoPointsInLineProtocolNoSeparatorRepresentation = "response,domane=thanos response=131 110response,domane=thanos response=313 111";
    @Override
    public void handle(HttpExchange he) throws IOException {
	
	final InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
	final BufferedReader br = new BufferedReader(isr);
	String query = br.readLine(); 
	query += br.readLine();
	
	if(query.equals(twoPointsInLineProtocolNoSeparatorRepresentation)) {
	    he.sendResponseHeaders(200, 0); 
	}else {
	    he.sendResponseHeaders(400, 0); 
	}
    } 
}

