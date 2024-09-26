FROM gradle:8.8-jdk17-alpine

COPY . .

RUN gradle build

