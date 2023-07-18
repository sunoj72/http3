package com.lgcns.dna.http3.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestHttp3DemoAppApplication {

	public static void main(String[] args) {
		SpringApplication.from(Http3DemoAppApplication::main).with(TestHttp3DemoAppApplication.class).run(args);
	}

}
