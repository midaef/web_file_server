package com.nameless;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread {

	private Boolean shutdown = false;
	private ArrayList<String> dirList = new ArrayList<>();

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
					String line = reader.readLine().replace("GET /?", "")
													.replace(" HTTP/1.1", "");
					Runnable run = ()-> getDir();
					Thread thread = new Thread(run);
					thread.start();
					String httpResponse = "HTTP/1.1 200 OK\r\n\r\n";
					socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));

				} catch (Exception e) {e.printStackTrace();}
			}
		} catch (Exception e) {e.printStackTrace();}
	}

	private void getDir() {
		String OS = "";
		OS = System.getProperty("os.name");
		String username = System.getProperty("user.name");
		File dir;
		if (OS.startsWith("Mac")) {dir = new File("/Users/" + username); }
		else {dir = new File("C:\\Users\\" + username);}
		for (File file : dir.listFiles()) {
			if (!file.getName().startsWith(".") && !dirList.contains(file.getName())) {
				dirList.add(file.getName());
			}
		}
	}

}
