package com.plaintext.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = { "com.plaintext.core", "com.plaintext.common" })
// CRITICAL: Look for database tables in the 'common' module
@EntityScan(basePackages = { "com.plaintext.common.model" })
// Look for repository interfaces in this module
@EnableJpaRepositories(basePackages = { "com.plaintext.core.repository" })
public class CoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }
}