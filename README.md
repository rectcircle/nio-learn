# NIO学习代码

NIO实现的一套Echo服务端和客户端、和一个简单的http服务


## http服务端
### 编译
```bash
cd http-server/
mvn package
```

### 运行
```bash
java -jar target/http-server.jar
```

### 使用
打开浏览器访问 http://localhost:8080


## Echo服务端

### 编译
```bash
cd echo-client/
mvn package
```

### 运行
```bash
java -jar target/echo-client.jar
```

### 使用
```
Usage: java -jar echo-client.jar [port | host port]
```

## Echo客户端

### 编译
```bash
cd echo-server/
mvn package
```

### 运行
```bash
java -jar target/echo-server.jar
```

### 使用
```
Usage: java -jar echo-server.jar [port | host port]
```