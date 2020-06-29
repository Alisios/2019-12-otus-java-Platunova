package ru.otus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;


//java -jar "/Users/alisa/Documents/Java JIDEA/maven/diploma-project-Ticket-monitoring-bot/bot-backend-microservice/backend-error-logging-service/target/backend-error-logging-service-2019-12-SNAPSHOT.jar"
//java -jar "backend-error-logging-service/target/backend-error-logging-service-2019-12-SNAPSHOT.jar"

@SpringBootApplication
@EnableEurekaClient
public class ErrorLoggerServiceBoot {

    public static void main(String[] args) {
        SpringApplication.run(ErrorLoggerServiceBoot.class, args);
    }
}

