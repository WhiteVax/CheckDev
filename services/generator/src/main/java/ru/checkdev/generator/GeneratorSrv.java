package ru.checkdev.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableEurekaClient
@EnableScheduling
public class GeneratorSrv {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(GeneratorSrv.class);
        application.addListeners(new ApplicationPidFileWriter("./generator.pid"));
        application.run();
    }
}
