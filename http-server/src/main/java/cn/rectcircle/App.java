package cn.rectcircle;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.net.*;
import java.nio.charset.Charset; 
import java.util.*;
import java.text.*;

public class App{
    //编码
    final static Charset charset = Charset.forName("utf-8");

    //时间
    final static SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
    static{
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public static void main(String[] args) throws Exception{
        //创建多路复用器
        Selector selector = Selector.open(); 
        //打开一个服务端套接字通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //服务端套接字设为false
        serverSocketChannel.configureBlocking(false);
        //绑定本机端口
        serverSocketChannel.socket().bind(new InetSocketAddress(8080));
        //在复用器中注册服务端socket
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);  
        //循环接收连接
        while(true){
            //阻塞等待套接字，准备就绪
            selector.select(); 
            //获取准备好的套接字Key的迭代器
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            //遍历
            while(keyIterator.hasNext()){
                SelectionKey key = keyIterator.next();   
                if(key.isValid()) handle(key);   
                keyIterator.remove(); 
            }
        }
    }

    private static void handle(SelectionKey key) throws Exception{
        //服务端socket
        if(key.isAcceptable()) {   
            //获取该通道
            ServerSocketChannel channel = (ServerSocketChannel) key.channel();   
            //获取socket
            SocketChannel socketChannel = channel.accept();   
            //设为非阻塞
            socketChannel.configureBlocking(false);   
            //注册到selector中
            //注册读
            socketChannel.register(key.selector(), SelectionKey.OP_READ);
        }
        if(key.isReadable() || key.isWritable()) {   
            httpService(key);
        }
    }

    private static void httpService(SelectionKey key) throws Exception{
        if(key.isReadable()){
            //创建缓冲
            ByteBuffer buf = ByteBuffer.allocate(2048);
            SocketChannel socketChannel = (SocketChannel) key.channel();   
            //读数据
            while (socketChannel.read(buf)!=-1) {
                buf.flip(); //反转Buffer
                String req = charset.decode(buf).toString();
                System.out.println(req);
                buf.clear();
                if(req.contains("\r\n\r\n")){
                    break;
                }
            }
            //写回数据
            buf.clear();
            String resp = buildBody("<!DOCTYPE html>"+
                    "<html lang=\"zh_CN\">"+
                    "<head>"+
                    "    <meta charset=\"UTF-8\">"+
                    "    <title>Test</title>"+
                    "</head>"+
                    "<body>"+
                    "    <h1>Hello World</h1>"+
                    "</body>"+
                    "</html>");
            buf.put(resp.getBytes());
            buf.flip(); //切换到buffer模式
            System.out.println(resp);
            while(buf.hasRemaining()){
                socketChannel.write(buf);
            }
            buf.clear();
            // key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);  
            key.cancel();
            socketChannel.close();
        }
    }

    private static String buildBody(String body){
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.0 200 OK\r\n");
        sb.append("Content-Type: text/html;charset=utf-8\r\n");
        sb.append("Content-Length: "+ body.getBytes().length +"\r\n");
        sb.append("Content-Encoding:  default\r\n");
        sb.append("Server: localhost\r\n");
        Calendar cd = Calendar.getInstance();
        sb.append("Date: "+ sdf.format(cd.getTime()) +"\r\n");
        sb.append("\r\n");
        sb.append(body);
        return sb.toString();        
    }
}