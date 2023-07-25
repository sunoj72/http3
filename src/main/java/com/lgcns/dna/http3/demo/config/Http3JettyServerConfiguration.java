// package com.lgcns.dna.http3.demo.config;

// import java.nio.file.Paths;

// import org.eclipse.jetty.http3.server.HTTP3ServerConnectionFactory;
// import org.eclipse.jetty.http3.server.HTTP3ServerConnector;
// import org.eclipse.jetty.server.ConnectionFactory;
// import org.eclipse.jetty.server.Connector;
// import org.eclipse.jetty.server.HttpConfiguration;
// import org.eclipse.jetty.server.HttpConnectionFactory;
// import org.eclipse.jetty.server.SecureRequestCustomizer;
// import org.eclipse.jetty.server.ServerConnector;
// import org.eclipse.jetty.util.ssl.SslContextFactory;
// import org.springframework.boot.actuate.autoconfigure.web.ManagementContextConfiguration;
// import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
// import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
// import org.springframework.boot.web.server.WebServerFactoryCustomizer;
// import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// @Configuration
// class Http3JettyServerConfiguration {

//     @Bean
//     public ServletWebServerFactory getServletContainer() {
//         JettyServletWebServerFactory jetty = new JettyServletWebServerFactory();

//         jetty.addServerCustomizers(getJettyServerHttp3Customizer());

//         jetty.setContextPath("");

//         return jetty;
//     }

//     private JettyServerCustomizer getJettyServerHttp3Customizer() {
//         return server -> {
//             /**
//              * https://www.eclipse.org/jetty/documentation/jetty-11/programming-guide/index.html#pg-server-http-connector-protocol-http3
//              **/
//             ConnectionFactory http3 = new HTTP3ServerConnectionFactory(getHttpConfiguration());

//             HTTP3ServerConnector http3Connector = new HTTP3ServerConnector(server, getSslContextFactory(), http3);
//             http3Connector.getQuicConfiguration().setVerifyPeerCertificates(true);
//             http3Connector.setHost("localhost");
//             http3Connector.setPort(8443);

//             //using setConnectors instead of addConnector to remove any existing connectors
//             server.setConnectors(new Connector[] {http3Connector});
//         };
//     }

//     private SslContextFactory.Server getSslContextFactory() {
//         SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();

//         sslContextFactory.setTrustAll(false);
//         String keySorePath = Paths.get("bin", "main", "pki", "fileserver.jks").toAbsolutePath().normalize().toString();
//         sslContextFactory.setKeyStorePath(keySorePath);
//         sslContextFactory.setKeyStorePassword("dna123");

//         sslContextFactory.setCertAlias("fileserver");
//         //sslContextFactory.set

//         // String trustStorePath = Paths.get("resources", "pki").toAbsolutePath().normalize().toString();
//         // sslContextFactory.setTrustStorePath(trustStorePath);

//         return sslContextFactory;
//     }

//     private HttpConfiguration getHttpConfiguration() {
//         HttpConfiguration httpConfiguration = new HttpConfiguration();
//         /*
//          * do not send header server with value jetty <version> this is a security risk
//          * and provides not benefit for production use
//          */
//         httpConfiguration.setSendServerVersion(true);
//         httpConfiguration.addCustomizer(getSecureRequestCustomizer());

//         return httpConfiguration;
//     }

//     private SecureRequestCustomizer getSecureRequestCustomizer() {
//         SecureRequestCustomizer secureRequestCustomizer = new SecureRequestCustomizer();

//         //for testing only enforces SNI checking and should be used
//         secureRequestCustomizer.setSniHostCheck(false);

//         return secureRequestCustomizer;
//     }
// }