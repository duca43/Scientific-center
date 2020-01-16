package org.scientificcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableAsync
@EnableWebSocket
public class ScientificCenterApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ScientificCenterApplication.class, args);
    }

}
