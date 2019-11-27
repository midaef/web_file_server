package com.nameless;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

public class Page {

	private ArrayList<String> dirList = new ArrayList<>();
	private String index = "";
	private File dir;

	public Page() {}

	public String createIndexPage(String directory, Boolean isMainDirectory) {
		index = readFile("index.html");
		isEmptyDirectory(directory);
		addButtonToPage(isMainDirectory);
		addDirectoryToPage(directory);
		dirList.clear();
		return index;
	}

	private void addDirectoryToPage(String directory) {
		for (int i = 0; i < dirList.size(); i++) {
			index = index.replace("&file&", "<a href=\"javascript:void(0);\">" +
					"<span class=\"open-dir\">" + dirList.get(i) + "</span></a>&file&")
					.replace("&title&", getUserName())
					.replace("&path&", "<h3>" + "Your directory: " + directory + "</h3>");
		}
		index = index.replace("&file&", "");
	}

	private void isEmptyDirectory(String directory) {
		if (dirList.isEmpty()) {
			index = index.replace("&file&", "<h3>Directory is empty</h3>")
					.replace("&path&", "<h3>" + directory + "</h3>")
					.replace("&title&", getUserName());
		}
	}

	private void addButtonToPage(Boolean isMainDirectory) {
		if (!isMainDirectory) {
			index = index.replace("&button&",
					"<input type=\"button\" value=\"UP\" onclick=\"toDir()\" class=\"button-up\">");
		}
		else index = index.replace("&button&", "");
	}

	public String readFile(String fileName) {
		String txt = "";
		try {
			File file = new File(fileName);
			Scanner sc = new Scanner(file);
			while (sc.hasNextLine()) {
				txt += sc.nextLine() + "\n";
			}
		} catch (Exception e) {e.printStackTrace();}
		return txt;
	}

	private String getUserName() {
		return System.getProperty("user.name");
	}

	public String getFormatFile(String directoryName) {
		String[] formatList = {".txt", ".png", ".jpg", ".gif", ".bmp", ".html"};
		for (String format : formatList) {
			if (directoryName.endsWith(format)) {
				return format;
			}
		}
		return "";
	}

	public String openFile(String format, String directoryLink) {
		String[] imageFormat = {".png", ".jpg", ".gif", ".bmp"};
		if (format.equals(".txt") || format.equals(".html")) return readFile(directoryLink);
		for (String image : imageFormat) {
			if (format.equals(image)) {
				String src = "data:" + "image/" +
						format.replace(".", "") + ";" + "base64," + openImage(directoryLink, format);
				return "<html><img src=\"" + src + "\"/></html>";
			}
		}
		return "";
	}

	private String openImage(String directoryLink, String format) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BufferedImage bufferedImage = ImageIO.read(new File(directoryLink));
			ImageIO.write(bufferedImage, format.replace(".", "") , outputStream);
			return Base64.getEncoder().encodeToString(outputStream.toByteArray());
		} catch (IOException e) {
			return "Couldn't open!";
		}
	}

	public String getMainDir(String directory) {
		String OS = "";
		OS = System.getProperty("os.name");
		String userName = getUserName();
		if (OS.startsWith("Mac")) dir = new File("/Users/" + userName + "/" + directory);
		else dir = new File("C:\\Users\\" + userName + "/" + directory);
		if (!getFormatFile(directory).isEmpty()) return dir.getPath();
		for (File file : dir.listFiles()) {
			if (!file.getName().startsWith(".") && !dirList.contains(file.getName())
				&& !file.getName().startsWith("ntuser") && !file.getName().startsWith("NTUSER")) {
				dirList.add(file.getName());
			}
		}
		return dir.getPath();
	}

}
