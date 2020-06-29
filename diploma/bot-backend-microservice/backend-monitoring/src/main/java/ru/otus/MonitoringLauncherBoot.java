package ru.otus;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//java -jar "target/backend-monitoring-2019-12-SNAPSHOT.jar"

@SpringBootApplication
public class MonitoringLauncherBoot {

    public static void main(String[] args) {
        SpringApplication.run(MonitoringLauncherBoot.class, args);
    }
}
