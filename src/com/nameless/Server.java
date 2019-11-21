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
			ServerSocket serverSocket = new ServerSocket(62226, 50, address);
			System.out.println("[SERVER STARTED]");
			while (!shutdown) {
				try (Socket socket = serverSocket.accept()) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String line = reader.readLine().split("\n")[0].replace(" HTTP/1.1", "");
					Page page = new Page();
					String request = getRequest(line, serverSocket, socket, page);
					sendRequest(socket, request);

				} catch (Exception e) {e.printStackTrace();}
			}
		} catch (Exception e) {e.printStackTrace();}
	}

	private String getRequest(String line, ServerSocket server, Socket socket, Page page) {
		String index = "";
		index = page.createIndexPage();
		return index;
	}

	private void sendRequest(Socket socket, String req) {
		try {
			String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + req;
			socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
		} catch (Exception ignored) {ignored.printStackTrace();}
	}

}
