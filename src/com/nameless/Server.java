package com.nameless;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	private Boolean shutdown = false;

	public Server() {
		start();
	}

	public void start() {
		try {
			InetAddress address = InetAddress.getByName("::1");
			ServerSocket serverSocket = new ServerSocket(52225, 50, address);
			System.out.println("[SERVER STARTED]");
			//try {serverSocket = new ServerSocket(52225);} catch (Exception e) {} It's not local
			while (!shutdown) {
				try (Socket socket = serverSocket.accept()) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String line = reader.readLine().split("\n")[0].replace(" HTTP/1.1", "");
					Page page = new Page();
					parser(line);
					String request = getRequest(line, serverSocket, socket, page);
					sendRequest(socket, request);
				} catch (Exception e) {e.printStackTrace();}
			}
		} catch (Exception e) {e.printStackTrace();}
	}

	private void parser(String line) {
		if (line.contains("?") && !line.endsWith("?")) {
			String reqGet = line.split("\\?")[1];
			String[] dataArray = reqGet.split("&");
			String[] data;
			String dirName = "";
			for (String str : dataArray) {
				data = str.split("=");
				dirName = data[1];
			}
			System.out.println(dirName);
		}
	}

	private String getRequest(String line, ServerSocket server, Socket socket, Page page) {
		if (line.startsWith("GET /style.css")) {
			String indexCSS = page.readFile("style.css");
			return indexCSS;
		} else if (line.startsWith("GET /script.js")) {
			String scriptJS = page.readFile("script.js");
			return scriptJS;
		}
		return page.createIndexPage();
	}

	private void sendRequest(Socket socket, String req) {
		try {
			String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + req;
			socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
		} catch (Exception ignored) {ignored.printStackTrace();}
	}

}
