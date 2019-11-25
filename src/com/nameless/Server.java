package com.nameless;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	private Boolean shutdown = false;
	private Page page = new Page();

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
					parser(line, socket);
					String request = parser(line, socket);
					sendRequest(socket, request);
				} catch (Exception ignored) {}
			}
		} catch (Exception e) {e.printStackTrace();}
	}

	private String parser(String line, Socket socket) {
		if (line.contains("?") && !line.endsWith("?")) {
			String directoryName = splitRequest(line);
			String directory = page.getMainDir(directoryName);
			String index = page.createIndexPage(directory, false);
			return index;
		} else if (line.startsWith("GET /style.css")) {
			String indexCSS = page.readFile("style.css");
			return indexCSS;
		} else if (line.startsWith("GET /script.js")) {
			String scriptJS = page.readFile("script.js");
			return scriptJS;
		} else if (line.equals("GET /")) {
			String directory = page.getMainDir("");
			return page.createIndexPage(directory, true);
		}
		return "Page not found: 404";
	}

	private String splitRequest(String line) {
		String[] request = line.split("\\?");
		String directoryName = "";
		for (String str : request) {
			directoryName += str.replace("dir=", "").replace("GET /", "")
					.replace("%20", " ") + "/";
		}
		System.out.println(directoryName);
		return directoryName;
	}

	private void sendRequest(Socket socket, String req) {
		try {
			String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + req;
			socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
		} catch (Exception ignored) {ignored.printStackTrace();}
	}

}
