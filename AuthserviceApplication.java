package com.ocs.authservice;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.ocs.authservice.util.RSAUtil;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
@EnableJpaRepositories(basePackages = {"com.*"})
@EntityScan(basePackages = {"com.*"})
@ComponentScan(basePackages = {"com.*"})
@EnableDiscoveryClient
@EnableFeignClients
@Configuration
public class AuthserviceApplication extends SpringBootServletInitializer{

	private static String keyStore = System.getenv("KEYSTORE");	
	private static String keyStorPwd = System.getenv("KEYSTORE_PWD");	
	private static String keyStoreType = System.getenv("KEYSTORE_TYPE");	
	private static String trustStore = System.getenv("TRUST_STORE");	
	private static String trustStorePwd = System.getenv("TRUST_STORE_PWD");
	
	public static void main(String[] args) {

		System.setProperty("javax.net.ssl.keyStore", keyStore);
		System.setProperty("javax.net.ssl.keyStorePassword", keyStorPwd);
		System.setProperty("javax.net.ssl.keyStoreType", keyStoreType);
		System.setProperty("javax.net.ssl.trustStore", trustStore);
		System.setProperty("javax.net.ssl.trustStorePassword", trustStorePwd); 
		System.setProperty("com.sun.net.ssl.checkRevocation", "false");

		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true; // Always returns true to bypass verification
			}
		});
		
		SpringApplication.run(AuthserviceApplication.class, args);
	}
	
	@EventListener(ApplicationReadyEvent.class)
	public void generateRSAKeys() {
		try {
			RSAUtil.generateRSAKeys();
		}catch(Exception e) {
			log.error("Error in generate RSA Keys", e);
		}
	}
	
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AuthserviceApplication.class);
    }


}
