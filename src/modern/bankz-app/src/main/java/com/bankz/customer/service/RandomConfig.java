package com.bankz.customer.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;
import java.util.random.RandomGenerator;

@Configuration
class RandomConfig {

    @Bean
    RandomGenerator randomGenerator() {
        return new SecureRandom();
    }
}
