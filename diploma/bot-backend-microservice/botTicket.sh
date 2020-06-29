#!/usr/bin/env bash

java -jar  "/Users/alisa/Documents/Java JIDEA/maven/diploma-project-Ticket-monitoring-bot/bot-backend-microservice/backend-db-service/target/backend-db-service-jar-with-dependencies.jar" &
java -jar "/Users/alisa/Documents/Java JIDEA/maven/diploma-project-Ticket-monitoring-bot/bot-backend-microservice/backend-get-info/target/backend-get-info-jar-with-dependencies.jar" &
java -jar "/Users/alisa/Documents/Java JIDEA/maven/diploma-project-Ticket-monitoring-bot/bot-backend-microservice/backend-monitoring/target/backend-monitoring-jar-with-dependencies.jar" &
java -jar "/Users/alisa/Documents/Java JIDEA/maven/diploma-project-Ticket-monitoring-bot/bot-frontend-telegramApi/target/bot-frontend-jar-with-dependencies.jar" 