package com.nameless.web_server;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

/**
 * This program can be use for your home PC or server.
 * Web file server give you the ability to have permanent access to files on your computer.
 * The author of this program does not call for its use as malicious software.
 * Anyway the author does not bear any responsibility!
 *
 * @author Mikhailov Danil(midaef).
 */

/**
 * This class use for create any page.
 */

public class Page {

	private ArrayList<String> dirList = new ArrayList<>();
	private String index = "";
	private File dir;

	public Page() {}

	public String createIndexPage(String directory, Boolean isMainDirectory) {
		index = readFile("src/main/resources/index.html");
		getEmptyDirectory(directory);
		addButtonToPage(isMainDirectory);
		addDirectoryToPage(directory);
		clearDirectoryList();
		return index;
	}

	private void addDirectoryToPage(String directory) {
		for (int i = 0; i < dirList.size(); i++) {
			index = index.replace("&file&",
					"<tr>&download&&size&<td>&icon&<a href=\"javascript:void(0);\" class=\"open-dir\">" +
					 dirList.get(i) + "</a>&file&</td></tr>").replace("&title&", getUserName())
					.replace("&path&", "<h3>" + "Your directory: " + directory + "</h3>");
			setDownloadLink(directory, i);
			setSize(directory, i);
			setIcon(directory, i);
		}
		index = index.replace("&file&", "");
	}

	private void setDownloadLink(String directory, int count) {
		dir = new File(directory + "/" + dirList.get(count));
		if (dir.isFile()) {
			index = index.replace("&download&", "<td><a onClick=\"downloadFile('" + dirList.get(count)
					+ "')\"><img src=\"" + readFile("src/main/resources/download_icon.txt") +"\"></a></td>");
		} else {
			index = index.replace("&download&", "<td></td>");
		}
	}

	public void clearDirectoryList() {dirList.clear();}

	private void setIcon(String directory, int count) {
		dir = new File(directory + "/" + dirList.get(count));
		if (dir.isDirectory()) {
			index = index.replace("&icon&",
					"<img src=\"" + readFile("src/main/resources/folder_icon.txt") + "\">");
		} else {
			index = index.replace("&icon&",
					"<img src=\"" + readFile("src/main/resources/file_icon.txt") + "\">");
		}
	}

	private void setSize(String directory, int count) {
		dir = new File(directory + "/" + dirList.get(count));
		if (dir.isFile()) {
			index = index.replace("&size&", "<td>" + dir.length() / 1024 + " KB</td>");
		} else {
			index = index.replace("&size&", "<td></td>");
		}
	}

	private void getEmptyDirectory(String directory) {
		if (dirList.isEmpty()) {
			index = index.replace("&file&", "<tr><td>Directory is empty</td></tr>")
					.replace("&path&", "<h3>" + directory + "</h3>")
					.replace("&title&", getUserName());
		}
	}

	private void addButtonToPage(Boolean isMainDirectory) {
		if (!isMainDirectory) {
			index = index.replace("&back-link&", "<a href=\"javascript:toDir();\">Parent directory</a>"
			).replace("&back-link-src&", readFile("src/main/resources/back_link_icon.txt"));
		}
		else index = index.replace("&back-link&", "").replace("&back-link-src&", "");
	}

	public String readFile(String fileName) {
		String txt = "";
		try {
			File file = new File(fileName);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while((line = bufferedReader.readLine()) != null) {
				txt += line + "\n";
			}
			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return txt;
	}

	public String createPageNotFound() {
		String page = readFile("src/main/resources/404.html")
								.replace("&cat&", readFile("src/main/resources/cat.txt"));
		return page;
	}

	public String createErrorPage() {
		String page = readFile("src/main/resources/error.html")
								.replace("&cat&", readFile("src/main/resources/cat.txt"));
		return page;
	}

	private String getUserName() {
		return System.getProperty("user.name");
	}

	public String getFormatFile(String directoryName) {
		String[] formatList = {".txt", ".xml", ".log", ".bat", ".cmd", ".py", ".acp", ".acpx", ".cfg", ".css", ".js",
				".html", ".htm", ".php", ".xhtml", ".c", ".cpp", ".cs", ".h", ".sh", ".java", ".swift", ".vb", ".ini",
				".png", ".jpg", ".gif", ".bmp", ".TXT"};
		for (String format : formatList) {
			if (directoryName.endsWith(format)) {
				return format;
			}
		}
		return "";
	}

	public String openFile(String format, String directoryLink) {
		String[] imageFormat = {".png", ".jpg", ".gif", ".bmp"};
		String[] textFormat = {".txt", ".xml", ".log", ".bat", ".cmd", ".py", ".acp", ".acpx", ".cfg", ".css", ".js",
		".html", ".htm", ".php", ".xhtml", ".c", ".cpp", ".cs", ".h", ".sh", ".java", ".swift", ".vb", ".ini", ".TXT"};
		for (String text : textFormat) {
			if (format.equals(text)) return readFile(directoryLink);
		}
		for (String image : imageFormat) {
			if (format.equals(image)) {
				String dataImage = openImage(directoryLink, format);
				String base64 = dataImage.split(":img:")[1];
				Integer width = Integer.parseInt(dataImage.split(":img:")[0].split("/")[0]) / 4;
				Integer height = Integer.parseInt(dataImage.split(":img:")[0].split("/")[1]) / 4;
				String src = "data:" + "image/" +
						format.replace(".", "") + ";" + "base64," + base64;
				return readFile("src/main/resources/img.html").replace("&src&", src)
						.replace("&width&", width.toString()).replace("&height&", height.toString());
			}
		}
		return "";
	}

	private String openImage(String directoryLink, String format) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BufferedImage bufferedImage = ImageIO.read(new File(directoryLink));
			ImageIO.write(bufferedImage, format.replace(".", "") , outputStream);
			Integer width = bufferedImage.getWidth();
			Integer height = bufferedImage.getHeight();
			return width + "/" + height + ":img:" + Base64.getEncoder().encodeToString(outputStream.toByteArray());
		} catch (IOException e) {
			return createErrorPage();
		}
	}

	public String getMainDir(String directory) {
		String OS = System.getProperty("os.name");
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