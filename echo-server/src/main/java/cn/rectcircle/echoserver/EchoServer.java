package cn.rectcircle.echoserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * 使用NIO实现的Echo服务端
 *
 * @author sunben
 * @date 2018-08-09
 * @version 0.0.1
 */
public class EchoServer {
	/** socket */
	private final ServerSocketChannel serverSocketChannel;
	private ByteBuffer buffer = ByteBuffer.allocate(4096);

	public EchoServer(String host, int port) throws IOException{
		SocketAddress socketAddress = new InetSocketAddress(host, port);
		this.serverSocketChannel = ServerSocketChannel.open();
		this.serverSocketChannel.socket().bind(socketAddress);
		System.out.println(String.format("Echo started on host %s, port %d.", host, port));
	}

	public EchoServer(int port) throws IOException {
		this("0.0.0.0", port);
	}
	
	public void run() throws IOException{
		// 创建多路复用器
		Selector selector = Selector.open();
		// 将通道设置为非阻塞模式
		serverSocketChannel.configureBlocking(false);
		// 将通道注册到selector中
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		// 循环接收连接
		while(true){
			//阻塞等待io就绪
			selector.select();
			// 获取准备好的套接字Key的迭代器
			Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
			// 遍历
			while (keyIterator.hasNext()) {
				SelectionKey key = keyIterator.next();
				if (key.isValid()){
					handle(key);
				}
				//清除，防止影响下一次select
				keyIterator.remove();
			}
		}
	}

	private void handle(SelectionKey key) throws IOException {
		if(key.isAcceptable()){
			accept(key);
		} else if(key.isReadable()) {
			echo(key);
		}
	}

	private void accept(SelectionKey key) throws IOException {
		// 获取通道
		ServerSocketChannel channel = (ServerSocketChannel) key.channel();
		// 获取此连接的socket通道
		SocketChannel socketChannel = channel.accept();
		// 设置为非阻塞
		socketChannel.configureBlocking(false);
		// 注册到selector中
		// 注册读
		socketChannel.register(key.selector(), SelectionKey.OP_READ);
	}

	private void echo(SelectionKey key)  {
		if (key.isReadable()) {
			// 获取到socket
			SocketChannel socketChannel = (SocketChannel) key.channel();
			// 读数据到buffer
			try {
				if (socketChannel.read(buffer) != -1) { //非阻塞关键，使用if而不是while
					// 切换到读模式
					buffer.flip();
					// 回显
					socketChannel.write(buffer);
					// 切换为写模式
					buffer.clear();
				}
			} catch (IOException e) {
				//将当前key取消注册
				key.cancel();
			}

		}
	}

}