package com.nameless;

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
 *	1. If you want to start the server in your PC
 *	2. You should to set-up the port in your router's settings.
 *	3. Start program and set port and password.
 *	4. Next, in the browser, connect in the format "localhost:port" or "ip:port".
 *	5. Input password and any login.
 * By the way, when you set-up port, you have to check port is not busy
 */

public class Server {

	private Boolean shutdown = false;
	private Integer port;
	private String password;
	private HashMap<String, String> users = new HashMap<>();

	public Server(Integer port, String password) {
		this.port = port;
		this.password = password;
		start();
	}

	private void start() {
		try {
			InetAddress address = InetAddress.getByName("::");
			try (ServerSocket serverSocket = new ServerSocket(port, 50, address)) {
				System.out.println("[SERVER STARTED]-[" + getNowDate() + "]");
				serverTask();
				while (!shutdown) {
					Socket socket = serverSocket.accept();
					Session session = new Session(socket, users, password);
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