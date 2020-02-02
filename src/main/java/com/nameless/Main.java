package com.nameless;

/**
 * This program can be use for your home PC or server.
 * Web file server give you the ability to have permanent access to files on your computer.
 * The author of this program does not call for its use as malicious software.
 * Anyway the author does not bear any responsibility!
 *
 * @author Mikhailov Danil(midaef).
 */

/**
 * This class is Main and it's do instance server class.
 */

public class Main {

    public static void main(String[] args) {
		Settings settings = new Settings();
		new Server(Integer.parseInt(settings.getPort()), settings.getPassword());
    }

}
