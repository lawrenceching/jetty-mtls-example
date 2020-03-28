package me.imlc;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class JettyMtlsServerTest {

    private static Logger logger = Logger.getLogger(JettyMtlsServerTest.class.getSimpleName());

    private JettyMtlsServer server;

    @Test
    public void canAuthenticateClient() throws Exception {
        SslContextFactory sslContextFactory = new SslContextFactory.Client();
        sslContextFactory.setKeyStorePath(this.getClass().getResource(
                "/client.jks").toExternalForm());
        sslContextFactory.setKeyStorePassword("changeit");

        sslContextFactory.setTrustAll(true);

        sslContextFactory.setTrustStorePath(this.getClass().getResource(
                "/client_truststore.jks").toExternalForm());
        sslContextFactory.setTrustStorePassword("changeit");

        HttpClient httpClient = new HttpClient(sslContextFactory);


        httpClient.start();

        ContentResponse response = httpClient.newRequest("https://localhost:8443")
                .send();

        assertThat(200, equalTo(response.getStatus()));
        assertThat("Hello, world", equalTo(response.getContentAsString()));

        httpClient.stop();
    }

    @Test
    public void canAuthenticateClient1() throws Exception {
        SslContextFactory sslContextFactory = new SslContextFactory.Client();
        sslContextFactory.setKeyStorePath(this.getClass().getResource(
                "/client_nontrust.jks").toExternalForm());
        sslContextFactory.setKeyStorePassword("changeit");

        HttpClient httpClient = new HttpClient(sslContextFactory);

        httpClient.start();

        Throwable t = null;
        try {
            httpClient.newRequest("https://localhost:8443")
                    .send();
        } catch (Throwable _t) {
            t = _t;
        }

        assertNotNull(t);
        assertNotNull(t.getMessage(), equalTo("Received fatal alert: certificate_unknown"));

        httpClient.stop();
    }

    @Test
    public void receiveBadCertificateAlert_IfNoClientCertProvide() throws Exception {
        SslContextFactory sslContextFactory = new SslContextFactory.Client();
        sslContextFactory.setTrustAll(true);

        HttpClient httpClient = new HttpClient(sslContextFactory);

        httpClient.start();

        Throwable t = null;
        try {
            httpClient.newRequest("https://localhost:8443")
                    .send();
        } catch (Throwable _t) {
            t = _t;
        }

        assertNotNull(t);
        assertNotNull(t.getMessage(), equalTo("Received fatal alert: bad_certificate"));

        httpClient.stop();
    }

    @Before
    public void setUp() throws Exception {
        server = new JettyMtlsServer();
        server.start();
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

}
