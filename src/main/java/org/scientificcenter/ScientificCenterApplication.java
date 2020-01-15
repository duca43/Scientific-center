package org.scientificcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ScientificCenterApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ScientificCenterApplication.class, args);
    }

}
