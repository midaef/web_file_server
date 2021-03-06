package com.nameless.web_server;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

/**
 * This program can be use for your home PC or server.
 * Web file server give you the ability to have permanent access to files on your computer.
 * The author of this program does not call for its use as malicious software.
 * Anyway the author does not bear any responsibility!
 *
 * @author Mikhailov Danil(midaef).
 */

/**
 * This class use for start web file server.
 * It's server can get request from browser and send to client.
 * For online documentation and support please refer to https://github.com/midaef/web_file_server.
 */

public class Server {

	private Boolean shutdown = false;
	private Integer port;
	private HashMap<String, String> users = new HashMap<>();
	private Settings settings = new Settings();

	public Server() {
		start();

	}

	private void start() {
		port = Integer.parseInt(settings.getPort());
		try {
			InetAddress address = InetAddress.getByName("::");
			try (ServerSocket serverSocket = new ServerSocket(port, 50, address)) {
				System.out.println("[SERVER STARTED]-[" + getNowDate() + "]");
				serverTask();
				while (!shutdown) {
					Socket socket = serverSocket.accept();
					Session session = new Session(socket, users);
					session.start();
				}
			} catch (Exception e) {e.printStackTrace();}
		} catch (Exception e) {e.printStackTrace();}
		System.out.println("[Server stopped]-" + "[" + getNowDate() + "]");
	}

	private void serverTask() {
		Runnable task = () -> {
			while (!shutdown) {
				System.out.print("\nCommands:\nexit - stop server\nusers - print users on server\n>>>");
				String value = new Scanner(System.in).nextLine();
				switch (value) {
					case "exit":
						System.out.println("[Server stopped]-" + "[" + getNowDate() + "]");
						System.exit(1);
					case "users":
						for (String user : users.keySet()) {
							System.out.println("User:" + users.get(user) + " | Token:" + user);
						}
						break;
				}
			}
		};
		Thread thread = new Thread(task);
		thread.start();
	}

	private String getNowDate() {
		Date now = new Date();
		LocalDateTime nowDate = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formatDateTime = nowDate.format(formatter);
		return formatDateTime;
	}

}