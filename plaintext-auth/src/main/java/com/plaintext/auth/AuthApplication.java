package com.plaintext.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = { "com.plaintext.auth", "com.plaintext.common" })
// @EntityScan: Critical!
// By default, Spring only looks in 'com.plaintext.auth'.
// We must tell it: "Go look in 'com.plaintext.common.model' for database
// entities too."
@EntityScan(basePackages = { "com.plaintext.common.model" })
// @EnableJpaRepositories:
// Tells Spring where to find our Repository interfaces (which we will create
// next).
@EnableJpaRepositories(basePackages = { "com.plaintext.auth.repository" })
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}