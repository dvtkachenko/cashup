package com.dtkachenko.cashup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@SpringBootApplication
public class CashupApplication {
    public static void main(String[] args) {
        SpringApplication.run(CashupApplication.class, args);
    }
}
