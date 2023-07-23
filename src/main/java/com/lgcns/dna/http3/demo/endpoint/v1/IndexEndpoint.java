package com.lgcns.dna.http3.demo.endpoint.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1")
public class IndexEndpoint {

    @GetMapping("/")
    public String index() {
        return "hello jersey project!\n";
    }
}