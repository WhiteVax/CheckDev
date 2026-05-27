package ru.checkdev.mock;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@OpenAPIDefinition(info = @Info(title = "Mock - interview service", description = "Description"))
@SpringBootApplication
@EnableEurekaClient
public class MockSrv {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MockSrv.class);
        application.addListeners(new ApplicationPidFileWriter("./mock.pid"));
        application.run();
    }

    @Bean
    public SpringLiquibase liquibase(DataSource ds) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath:db/liquibase-changeLog.xml");
        liquibase.setDataSource(ds);
        return liquibase;
    }
}

