package com.nameless;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Page {

	private ArrayList<String> dirList = new ArrayList<>();

	public Page() {}

	public String createIndexPage() {
		getMainDir();
		String index = readFile("index.html");
		String dir = "";
		for (String i : dirList) {
			dir+=i + " ";

		}
		index = index.replace("&file&", "<p>" + dir + "</p>");
		return index;
	}

	private String readFile(String fileName) {
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

	public void getMainDir() {
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
