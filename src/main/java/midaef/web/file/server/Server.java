package midaef.web.file.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
 * This class use for start web file server.
 * It's server can get request from browser and send to client.
 * 1. If you want to start the server in your PC
 * 2. You should to set-up the port in your router's settings.
 * 3. Start program and set port and password.
 * 4. Next, in the browser, connect in the format "localhost:port" or "ip:port".
 * 5. Input password and any login.
 * By the way, when you set-up port, you have to check port is not busy
 */

public class Server {
    private Boolean shutdown = false;
    private Integer port;
    private String password;

    public Server(Integer port, String password) {
        this.port = port;
        this.password = password;
        start();
    }

    private void start() {
        try {
            InetAddress address = InetAddress.getByName("::");
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("[SERVER STARTED]-[" + getNowDate() + "]");
            while (!shutdown) {
                try {
                    Socket socket = serverSocket.accept();
                    new Session(socket, password).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("[Server stopped]-" + "[" + getNowDate() + "]");
    }

    private String getNowDate() {
        Date now = new Date();
        LocalDateTime nowDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDateTime = nowDate.format(formatter);
        return formatDateTime;
    }
}
