package com.nameless;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Page {

	private ArrayList<String> dirList = new ArrayList<>();
	public String index = "";

	public Page() {}

	public String createIndexPage(String directory) {
		index = readFile("index.html");
		String dir = "";
		int j = 0;
		for (String i : dirList) {
			j++;
			if (dirList.size() != j) {
				index = index.replace("&file&", "<a href=\"javascript:void(0);\">" +
						"<span class=\"open-dir\">" + i + "</span></a>&file&")
						.replace("&title&", getUserName())
						.replace("&path&", "<h3>" + "Your directory: " + directory + "</h3>");
			} else {index = index.replace("&file&", "");}
		}
		dirList.clear();
		return index;
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
		if (OS.startsWith("Mac")) {dir = new File("/Users/" + userName + "/" + directory); }
		else {dir = new File("C:\\Users\\" + userName + "/" + directory);}
		for (File file : dir.listFiles()) {
			if (!file.getName().startsWith(".") && !dirList.contains(file.getName())) {
				dirList.add(file.getName());
			}
		}
		return dir.getPath();
	}

}
