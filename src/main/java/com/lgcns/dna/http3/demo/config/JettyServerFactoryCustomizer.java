package com.lgcns.dna.http3.demo.config;

import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

public class JettyServerFactoryCustomizer implements WebServerFactoryCustomizer<JettyServletWebServerFactory > {

    @Override
    public void customize(JettyServletWebServerFactory factory) {
        // customize the factory here
    }
 
}

