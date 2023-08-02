package com.lgcns.dna.http3.demo.config;

import java.nio.file.Paths;

import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http.HttpCompliance;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.http3.server.HTTP3ServerConnectionFactory;
import org.eclipse.jetty.http3.server.HTTP3ServerConnector;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.web.ManagementContextConfiguration;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class JettyServerConfiguration {
    @Value("${server.port:8443}")
    Integer http2Port;

    @Value("${server.http3.port:8444}")
    Integer http3Port;

    @Bean
    public ServletWebServerFactory getServletContainer() {
        JettyServletWebServerFactory jetty = new JettyServletWebServerFactory();

        jetty.addServerCustomizers(getJettyServerHttp2Customizer());
        jetty.addServerCustomizers(getJettyServerHttp3Customizer());

        jetty.setContextPath("");

        return jetty;
    }

    // private JettyServerCustomizer getJettyServerHttpCustomizer() {
    //     return server -> {
    //         HttpConnectionFactory http1 = new HttpConnectionFactory(getHttpConfiguration());
    //         HTTP2ServerConnectionFactory http2 = new HTTP2ServerConnectionFactory(getHttpConfiguration());
    //         ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
    //         alpn.setDefaultProtocol(http1.getProtocol());

    //         HTTP3ServerConnectionFactory http3 = new HTTP3ServerConnectionFactory(getHttpConfiguration());

    //         HTTP3ServerConnector http3Connector = new HTTP3ServerConnector(server, getSslContextFactory(), alpn, http3, http2, http1);
    //         http3Connector.getQuicConfiguration().setVerifyPeerCertificates(false);
    //         http3Connector.setPort(http3Port);

    //         // server.addConnector(http3Connector);
    //         server.setConnectors(new Connector[] {http3Connector});
    //     };
    // }

    private JettyServerCustomizer getJettyServerHttp2Customizer() {
        return server -> {
            HttpConnectionFactory http1 = new HttpConnectionFactory(getHttpConfiguration());
            HTTP2ServerConnectionFactory http2 = new HTTP2ServerConnectionFactory(getHttpConfiguration());
            ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
            alpn.setDefaultProtocol(http1.getProtocol());

            ServerConnector http2Connector = new ServerConnector(server, getSslContextFactory(), alpn, http2, http1);
            // http3Connector.setHost("localhost");
            http2Connector.setPort(http2Port);

            // server.addConnector(http3Connector);
            server.setConnectors(new Connector[] {http2Connector});
        };
    }

    private JettyServerCustomizer getJettyServerHttp3Customizer() {
        return server -> {
            /**
             * https://www.eclipse.org/jetty/documentation/jetty-11/programming-guide/index.html#pg-server-http-connector-protocol-http3
             **/
            
            HTTP3ServerConnectionFactory http3 = new HTTP3ServerConnectionFactory(getHttpConfiguration());
            HTTP3ServerConnector http3Connector = new HTTP3ServerConnector(server, getSslContextFactory(), http3);
            http3Connector.getQuicConfiguration().setVerifyPeerCertificates(false);
            // http3Connector.setHost("localhost");
            http3Connector.setPort(http3Port);

            //using setConnectors instead of addConnector to remove any existing connectors
            // server.setConnectors(new Connector[] {http3Connector});
            server.addConnector(http3Connector);
        };
    }

    private SslContextFactory.Server getSslContextFactory() {
        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();

        sslContextFactory.setTrustAll(false);
        String keySorePath = Paths.get("bin", "main", "pki", "fileServer.jks").toAbsolutePath().normalize().toString();
        sslContextFactory.setKeyStorePath(keySorePath);
        sslContextFactory.setKeyStorePassword("dna123");
        sslContextFactory.setCertAlias("fileserver");
        //sslContextFactory.set

        // String trustStorePath = Paths.get("resources", "pki").toAbsolutePath().normalize().toString();
        // sslContextFactory.setTrustStorePath(trustStorePath);

        return sslContextFactory;
    }

    private HttpConfiguration getHttpConfiguration() {
        HttpConfiguration httpConfiguration = new HttpConfiguration();
        /*
         * do not send header server with value jetty <version> this is a security risk
         * and provides not benefit for production use
         */
        httpConfiguration.setSendServerVersion(true);
        httpConfiguration.setHttpCompliance(HttpCompliance.RFC7230);
        httpConfiguration.setRequestHeaderSize(8192);
        httpConfiguration.addCustomizer(getSecureRequestCustomizer());

        return httpConfiguration;
    }

    private SecureRequestCustomizer getSecureRequestCustomizer() {
        SecureRequestCustomizer secureRequestCustomizer = new SecureRequestCustomizer();

        //for testing only enforces SNI checking and should be used
        secureRequestCustomizer.setSniHostCheck(false);

        return secureRequestCustomizer;
    }
}