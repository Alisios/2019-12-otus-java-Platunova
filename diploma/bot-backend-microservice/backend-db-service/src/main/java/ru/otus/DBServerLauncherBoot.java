package ru.otus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

//java -jar "./backend-db-service/target/backend-db-service-2019-12-SNAPSHOT.jar"
//java -jar "/Users/alisa/Documents/Java JIDEA/maven/diploma-project-Ticket-monitoring-bot/bot-backend-microservice/backend-db-service/target/backend-db-service-2019-12-SNAPSHOT.jar"

@SpringBootApplication
@EnableEurekaClient
public class DBServerLauncherBoot {
    public static void main(String[] args) {
        SpringApplication.run(DBServerLauncherBoot.class, args);
    }
}