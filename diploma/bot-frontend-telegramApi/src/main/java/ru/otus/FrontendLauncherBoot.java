package ru.otus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

//java -jar "bot-frontend-telegramApi/target/bot-frontend-2019-12-SNAPSHOT.jar"

@SpringBootApplication
@EnableEurekaClient
public class FrontendLauncherBoot {

    public static void main(String[] args) {
        SpringApplication.run(FrontendLauncherBoot.class, args);
    }
}

