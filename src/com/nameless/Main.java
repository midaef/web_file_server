package com.nameless;

import java.util.Scanner;

public class Main {

	private static Integer port;
	private static String password;

	public Main() {
		settingsServer();
		new Server(port, password);
	}

    public static void main(String[] args) {
		new Main();
    }

    private static void settingsServer() {
		System.out.print("Configure your server\nPort: ");
		port = new Scanner(System.in).nextInt();
		System.out.print("Password: ");
		password = new Scanner(System.in).nextLine();
	}

}
