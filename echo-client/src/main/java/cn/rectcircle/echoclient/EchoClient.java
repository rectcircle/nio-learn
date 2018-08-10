package cn.rectcircle.echoclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 标准Socket实现的Echo客户端，
 * 启动1个线程用于接收消息
 * @author sunben
 * @date 2018-08-09
 * @version 0.0.1
 */
public class EchoClient {
	/** socket */
	private final Socket socket;

	public EchoClient(String host, int port) throws IOException{
		SocketAddress socketAddress = new InetSocketAddress(host, port);
		this.socket = new Socket();
		this.socket.connect(socketAddress);
	}
	
	public void run() throws IOException{
		ExecutorService executorService = Executors.newFixedThreadPool(1);
		executorService.execute(this::receive);
		send();
	}

	private void send() {
		Scanner in = new Scanner(System.in);
		try (PrintWriter out = new PrintWriter(socket.getOutputStream())){
			while (in.hasNext()) {
				String line = in.nextLine();
				out.println(line);
				out.flush();
			}
		} catch (IOException e){
			System.err.println(e.getMessage());
			System.exit(1);
		} finally {
			in.close();
		}


	}

	private void receive() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while (true) {
				System.out.println(in.readLine());
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}

	}

	
}