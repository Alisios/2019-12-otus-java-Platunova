#!/usr/bin/env bash

java -jar  "./bot-backend-microservice/backend-db-service/target/backend-db-service-jar-with-dependencies.jar" &
java -jar "./bot-backend-microservice/backend-get-info/target/backend-get-info-jar-with-dependencies.jar" &
java -jar "./bot-backend-microservice/backend-monitoring/target/backend-monitoring-jar-with-dependencies.jar" &
java -jar "./bot-frontend-telegramApi/target/bot-frontend-jar-with-dependencies.jar"