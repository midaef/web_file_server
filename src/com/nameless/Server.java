package com.nameless;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
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
	private Page page = new Page();
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
			ServerSocket serverSocket = new ServerSocket(port, 50, address);
			System.out.println("[SERVER STARTED]-[" + getNowDate() + "]");
			serverTask();
			while (!shutdown) {
				try (Socket socket = serverSocket.accept()) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String line = reader.readLine();
					String clientIP = getClientIP(socket);
					if (line != null) {
						String token = createToken(clientIP, reader);
						Boolean isLogin = login(line, token);
						if (isLogin) {
							line = line.split("\n")[0].replace(" HTTP/1.1", "");
							String request = parser(line);
							sendRequest(socket, request);
						} else sendRequest(socket, page.readFile("login.html"));
					}
				} catch (Exception e) {e.printStackTrace();}
			}
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

	private String createToken(String clientIP, BufferedReader reader) {
		try {
			String userAgent = "";
			for (int i = 0; i < 7; i++) {
				userAgent = reader.readLine();
				if (userAgent.startsWith("User-Agent: ")) {
					break;
				}
			}
			userAgent = userAgent + clientIP;
			if (userAgent.startsWith("User-Agent: ")) {
				MessageDigest m = MessageDigest.getInstance("MD5");
				m.reset();
				m.update(userAgent.getBytes("utf-8"));
				String token = new BigInteger(1, m.digest()).toString(16);
				while (token.length() < 32) {
					token = "0" + token;
				}
				return token;
			}
		} catch (Exception e) {e.printStackTrace();}
		return "";
	}

	private String getPasswordAndLogin(String line) {
		String req[] = line.split("=");
		String data = req[req.length-1].replace("/ HTTP/1.1", "");
		return data;
	}

	private Boolean login(String line, String token) {
		if (!users.containsKey(token)) {
			if (line.contains("entry=")) {
				String req = getPasswordAndLogin(line);
				if (req.split("/")[1].equals(password)) {
					users.put(token, req.split("/")[0]);
					return true;
				} else return false;
			} else return false;
		}
		return true;
	}

	private String getClientIP(Socket socket) {
		return socket.getInetAddress().toString();
	}

	private String parser(String line) {
		String directoryLink = "";
		if (line.contains("dir=") && !line.contains("download=")) {
			String directoryName = splitRequest(line, "dir=");
			try {
				directoryLink = page.getMainDir(directoryName);
			} catch (Exception e) {
				return page.createErrorPage();
			}
			if (!page.getFormatFile(directoryName).isEmpty()) {
				return page.openFile(page.getFormatFile(directoryName), directoryLink);
			}
			String index = page.createIndexPage(directoryLink, false);
			return index;
		} else if (line.contains("download=")) {
			String filePath = splitRequest(line, "dir=").replace("download=", "")
								.replace("//", "");
			String path = page.getMainDir("") + "/" + filePath;
			page.clearDirectoryList();
			if (filePath.contains("/")) {
				String[] data = filePath.split("/");
				return path + "&" + data[data.length-1] + "&" + new File(path).length() + "&keyword=download";
			}
			return path + "&" + filePath + "&" + new File(path).length() + "&keyword=download";
		} else if (line.equals("GET /") || line.contains("entry")) {
			String directory = page.getMainDir("");
			return page.createIndexPage(directory, true);
		}
		return page.createPageNotFound();
	}

	private String splitRequest(String line, String type) {
		String[] request = line.split("\\?");
		String directoryName = "";
		for (String str : request) {
				directoryName += "/" + str.replace(type, "").replace("GET /", "")
						.replace("%20", " ");
		}
		String[] data = {};
		if (directoryName.contains(password + "//")) data = directoryName.split("//");
		else if (data.length == 0) return directoryName;
		return data[data.length-1];
	}

	private byte[] getBytes(String path) {
		byte[] fileInArray = new byte[(int)new File(path).length()];
		try {
			FileInputStream f = new FileInputStream(path);
			f.read(fileInArray);
			return fileInArray;
		} catch (Exception e) {e.printStackTrace();}
		return fileInArray;
	}

	private void sendRequest(Socket socket, String req) {
		try {
			String httpResponse = "HTTP/1.1 200 OK\r\n";
			OutputStream outputStream = socket.getOutputStream();
			PrintStream ps = new PrintStream(outputStream);
			if (req.contains("keyword=download")) {
				String data[] = req.split("&");
				byte[] fileInArray = getBytes(data[0]);
				ps.printf(httpResponse);
				ps.print("Content-Disposition: form-data; name=\"myFile\"; filename=\"" + data[1] + "\"\r\n");
				ps.printf("Content-Type: text/plain; charset=utf-8\r\n\r\n");
				outputStream.write(fileInArray);
			} else {
				httpResponse+="\r\n" + req;
				socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
			}
		} catch (Exception e) {e.printStackTrace();}
	}

	private String getNowDate() {
		Date now = new Date();
		LocalDateTime nowDate = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formatDateTime = nowDate.format(formatter);
		return formatDateTime;
	}

}
