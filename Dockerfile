FROM openjdk:17-jdk-alpine

COPY . /app
RUN apk add curl

WORKDIR /app 
 
