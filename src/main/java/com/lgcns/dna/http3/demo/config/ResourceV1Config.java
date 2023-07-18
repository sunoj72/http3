package com.lgcns.dna.http3.demo.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import com.lgcns.dna.http3.demo.endpoint.v1.IndexEndpoint;

import jakarta.ws.rs.ApplicationPath;

@Component
@ApplicationPath("/v1")
public class ResourceV1Config extends ResourceConfig {

    public ResourceV1Config() {
        register(IndexEndpoint.class);
    }
}