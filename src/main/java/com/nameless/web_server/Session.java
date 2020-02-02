package com.nameless.web_server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.HashMap;

/**
 * This program can be use for your home PC or server.
 * Web file server give you the ability to have permanent access to files on your computer.
 * The author of this program does not call for its use as malicious software.
 * Anyway the author does not bear any responsibility!
 *
 * @author Mikhailov Danil(midaef).
 */

/**
 * This class use for create session and token
 */

public class Session extends Thread {

	private HashMap<String, String> users;
	private Page page = new Page();
	private String password;
	private Socket socket;
	private String ROOT_PATH = "src/main/resources/control.html";

	public Session(Socket socket, HashMap<String, String> users, String password, String ROOT_PATH) {
		this.password = password;
		this.users = users;
		this.socket = socket;
		this.ROOT_PATH = ROOT_PATH;
	}

	@Override
	public void run() {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			String line = reader.readLine();
			String clientIP = getClientIP(socket);
			if (line != null) {
				String token = createToken(clientIP, reader);
				if (line.contains("control.html")) {
					Boolean isLogin = login(line, token);
					if (isLogin) {
						line = line.split("\n")[0].replace(" HTTP/1.1", "");
						String request = parser(line);
						sendRequest(socket, request);
					} else sendRequest(socket, page.readFile("src/main/resources/control.html"));
				} else sendRequest(socket, page.readFile(ROOT_PATH));
			}
		} catch (Exception e) {e.printStackTrace();}
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
		} else {
			return page.readFile("src/main/resources/control.html");
		}
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

}