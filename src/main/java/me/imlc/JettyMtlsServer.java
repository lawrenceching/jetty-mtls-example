package me.imlc;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public class JettyMtlsServer {

    private static Logger logger = Logger.getLogger(JettyMtlsServer.class.getSimpleName());
    private Server server;

    public void start() throws Exception {

        server = new Server();

        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());

        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(this.getClass().getResource(
                "/server.jks").toExternalForm());
        sslContextFactory.setKeyStorePassword("changeit");

        sslContextFactory.setTrustStorePath(this.getClass().getResource("/server_truststore.jks").toExternalForm());
        sslContextFactory.setTrustStorePassword("changeit");
        sslContextFactory.setNeedClientAuth(true);

        ServerConnector connector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, "http/1.1"),
                new HttpConnectionFactory(https));
        connector.setPort(8443);

        server.setConnectors(new Connector[] { connector });
        server.setHandler(new HelloWorldHandler());



        server.start();
        logger.info("Server is running at http://localhost:8443");
    }

    public void stop() throws Exception {
        server.stop();
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("javax.net.debug", "all");
        System.setProperty("org.eclipse.jetty.LEVEL", "DEBUG");

        JettyMtlsServer app = new JettyMtlsServer();
        app.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                app.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    private static class HelloWorldHandler extends AbstractHandler {
        @Override
        public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
            httpServletResponse.getWriter().print("Hello, world");
            request.setHandled(true);
        }
    }

}
