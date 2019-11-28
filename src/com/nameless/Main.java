package com.nameless;

public class Main {

	private Integer port;

	public Main(Integer port) {
		this.port = port;
		new Server(port);
	}

    public static void main(String[] args) {
		Integer port;
        if (args.length != 1) {
			System.out.println("Usage: java -jar [name].jar [port]");
			System.exit(1);
		}
		port = Integer.parseInt(args[0]);
        new Main(port);
    }

}
