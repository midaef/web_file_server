package com.nameless;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	private Boolean shutdown = false;
	private Page page = new Page();
	private Integer port;

	public Server(Integer port) {
		this.port = port;
		start();
	}

	public void start() {
		try {
			InetAddress address = InetAddress.getByName("::");
			ServerSocket serverSocket = new ServerSocket(port, 50, address);
			System.out.println("[SERVER STARTED]");
			while (!shutdown) {
				try (Socket socket = serverSocket.accept()) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					try {
						String line = reader.readLine();
						line = line.split("\n")[0].replace(" HTTP/1.1", "");
						String request = parser(line);
						sendRequest(socket, request);
					} catch (Exception e) {e.printStackTrace();}
				} catch (Exception e) {e.printStackTrace();}
			}
		} catch (Exception e) {e.printStackTrace();}
	}

	private String parser(String line) {
		if (line.contains("?") && !line.endsWith("?")) {
			String directoryName = splitRequest(line);
			String directoryLink = "";
			try {directoryLink = page.getMainDir(directoryName);
			} catch (Exception e) {return "Couldn't open!";}
			if (!page.getFormatFile(directoryName).isEmpty()) {
				return page.openFile(page.getFormatFile(directoryName), directoryLink);
			}
			String index = page.createIndexPage(directoryLink, false);
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
			directoryName += "/" + str.replace("dir=", "").replace("GET /", "")
					.replace("%20", " ") ;
		}
		return directoryName;
	}

	private void sendRequest(Socket socket, String req) {
		try {
			String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + req;
			socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
		} catch (Exception ignored) {}
	}

}
