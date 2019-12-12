package midaef.web.file.server;

import java.util.Scanner;

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
        System.out.println("Web file server v1.0");
        System.out.println("GitHub: https://github.com/midaef/web_file_server");
        System.out.print("Configure your server\nPort: ");
//        port = new Scanner(System.in).nextInt();
        System.out.print("Password: ");
//        password = new Scanner(System.in).nextLine();
        port = 8888;
        password = "password";
    }

}
