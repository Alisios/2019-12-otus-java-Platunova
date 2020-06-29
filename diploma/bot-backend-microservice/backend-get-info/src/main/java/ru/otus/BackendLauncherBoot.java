package ru.otus;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//java -jar "target/backend-get-info-2019-12-SNAPSHOT.jar"
//java -jar "/Users/alisa/Documents/Java JIDEA/maven/diploma-project-Ticket-monitoring-bot/bot-backend-microservice/backend-get-info/target/backend-get-info-2019-12-SNAPSHOT.jar"

@SpringBootApplication
public class BackendLauncherBoot {

    public static void main(String[] args) {
        SpringApplication.run(BackendLauncherBoot.class, args);
    }
}
