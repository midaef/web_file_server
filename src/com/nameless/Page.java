package com.nameless;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Page {

	private ArrayList<String> dirList = new ArrayList<>();
	private String index = "";

	public Page() {}

	public String createIndexPage(String directory, Boolean isMainDirectory) {
		index = readFile("index.html");
		addButtonToPage(isMainDirectory);
		addDirectoryToPage(directory);
		dirList.clear();
		return index;
	}

	private void addDirectoryToPage(String directory) {
		int count = 0;
		for (String i : dirList) {
			count++;
			if (dirList.size() != count) {
				index = index.replace("&file&", "<a href=\"javascript:void(0);\">" +
						"<span class=\"open-dir\">" + i + "</span></a>&file&")
						.replace("&title&", getUserName())
						.replace("&path&", "<h3>" + "Your directory: " + directory + "</h3>");

			} else index = index.replace("&file&", "");
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

	public String getMainDir(String directory) {
		String OS = "";
		OS = System.getProperty("os.name");
		String userName = getUserName();
		File dir;
		if (OS.startsWith("Mac")) dir = new File("/Users/" + userName + "/" + directory);
		else dir = new File("C:\\Users\\" + userName + "/" + directory);
		for (File file : dir.listFiles()) {
			if (!file.getName().startsWith(".") && !dirList.contains(file.getName())
				&& !file.getName().startsWith("ntuser") && !file.getName().startsWith("NTUSER")) {
				dirList.add(file.getName());
			}
		}
		if (!fileIsEmpty(dir)) return dir.getPath();
		return "";
	}

	private Boolean fileIsEmpty(File dir) {
		return dir.getPath().isEmpty();
	}

}
