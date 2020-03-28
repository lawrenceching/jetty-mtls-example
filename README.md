# Jetty mTLS Example

> This project creates a Jetty HTTP server with mutual TLS

`src/main/java/me/imlc/JettyMtlsServer` The mTLS server implementation  
`src/test/java/me/imlc/JettyMtlsServerTest` The mTLS client implementation in a form of junit test  
`src/main/resources/*` The server and the client keys and certificates

## mTLS Server

Check out `src/main/java/me/imlc/JettyMtlsServer` for implementation details.

```bash
mvn install
java -jar target/jetty-mtls-example-1.0-SNAPSHOT.jar
``` 


## mTLS Client

There are mTLS client examples in `JettyMtlsServerTest`.
Or you can have fun with `curl` commands.

```bash
curl -k https://localhost:8443 \
  --cert ./src/main/resources/client.crt \
  --key ./src/main/resources/client.key
```


## Debug

To enable debug log or verbose log, add below properties in command line or in main method.

Note: `java.security.debug` only takes effect as a command line argument.

```bash
java -Djava.security.debug=all \
  -Djavax.net.debug=all \
  -Dorg.eclipse.jetty.LEVEL=DEBUG \
  -jar target/jetty-mtls-example-1.0-SNAPSHOT.jar
```

```java
System.setProperty("javax.net.debug", "all");
System.setProperty("org.eclipse.jetty.LEVEL", "DEBUG");
```



