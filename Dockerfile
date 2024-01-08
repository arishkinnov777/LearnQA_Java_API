FROM maven:3.9.6-eclipse-temurin-21-jammy
LABEL authors="arinanovikova"

WORKDIR /tests

COPY . .

CMD mvn clean test