package com.eappcat.isi.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableWebSocket
public class AliyunIsiServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AliyunIsiServerApplication.class, args);
    }

}
