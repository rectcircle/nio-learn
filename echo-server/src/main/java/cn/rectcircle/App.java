package cn.rectcircle;

import java.io.IOException;

import cn.rectcircle.echoserver.EchoServer;

public class App {
	private static String host = "0.0.0.0";
	private static int port = 7;
	public static void main(String[] args) throws IOException {
		try {
			switch (args.length) {
			case 0:
				break;
			case 1:
				port = Integer.parseInt(args[0]);
				break;
			case 2:
				host = args[0];
				port = Integer.parseInt(args[1]);
				break;
			default:
				System.out.println("Usage: java -jar echo-server.jar [port | host port]");
				break;
			}
			if(port<=0 || port >= 65536){
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			System.out.println("port must is integer ( 0 < port < 65536 )");
			return;
		}
		try {
			new EchoServer(host, port).run();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
