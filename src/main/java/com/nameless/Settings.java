package com.nameless;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * This program can be use for your home PC or server.
 * Web file server give you the ability to have permanent access to files on your computer.
 * The author of this program does not call for its use as malicious software.
 * Anyway the author does not bear any responsibility!
 *
 * @author Mikhailov Danil(midaef).
 */

/**
 * This class use for get properties server
 */

public class Settings {

	private String PROPERTIES_PATH = "src/main/resources/config.properties";
	private String SERVER_PORT = "";
	private String SERVER_PASSWORD = "";

	public Settings() {
		readProperties();
	}

	private void readProperties() {
		Properties property = new Properties();
		try {
			FileInputStream fileInputStream = new FileInputStream(PROPERTIES_PATH);
			property.load(fileInputStream);
			SERVER_PORT = property.getProperty("SERVER_PORT");
			SERVER_PASSWORD = property.getProperty("SERVER_PASSWORD");
		} catch (IOException e) {
			System.out.println("Config not found!");
		}
	}

	public String getPort() {return SERVER_PORT;}

	public String getPassword() {return SERVER_PASSWORD;}

}
