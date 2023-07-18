package com.lgcns.dna.http3.demo.endpoint.v1;

import org.springframework.stereotype.Component;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Component
@Path("")
public class IndexEndpoint {

    @GET
    public String index() {
        return "hello jersey project!";
    }
}