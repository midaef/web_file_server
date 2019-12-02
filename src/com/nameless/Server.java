package com.nameless;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;

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

	public void start() {
		try {
			InetAddress address = InetAddress.getByName("::");
			ServerSocket serverSocket = new ServerSocket(port, 50, address);
			System.out.println("[SERVER STARTED]-[" + getNowDate() + "]");
			while (!shutdown) {
				try (Socket socket = serverSocket.accept()) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String line = reader.readLine();
					String clientIP = getClientIP(socket);
					if (line != null) {
						String token = createToken(clientIP, reader);
						Boolean isLogin = login(line, token, socket);
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

	private Boolean login(String line, String token, Socket socket) {
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
		if (line.contains("dir=")) {
			String directoryName = splitRequest(line);
			String directoryLink = "";
			try {
				directoryLink = page.getMainDir(directoryName);
			} catch (Exception e) {
				return "Couldn't open!";
			}
			if (!page.getFormatFile(directoryName).isEmpty()) {
				return page.openFile(page.getFormatFile(directoryName), directoryLink);
			}
			String index = page.createIndexPage(directoryLink, false);
			return index;
		}
		else if (line.equals("GET /") || line.contains("entry")) {
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
						.replace("%20", " ");
		}
		String[] data = {};
		if (directoryName.contains(password + "//")) data = directoryName.split("//");
		return data[data.length-1];
	}

	private void sendRequest(Socket socket, String req) {
		try {
			String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + req;
			socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
		} catch (Exception ignored) {}
	}

	private String getNowDate() {
		Date now = new Date();
		LocalDateTime nowDate = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formatDateTime = nowDate.format(formatter);
		return formatDateTime;
	}

}
