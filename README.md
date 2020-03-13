[![Build Status](https://travis-ci.com/themikelus/TaskSystem.svg?branch=master)](https://travis-ci.com/themikelus/TaskSystem)

## Task System
## Prerequisites

* Java 1.8
* Maven

## Get the sources

1. git clone
2. cd TaskSystem

## Running tests

3. mvn test

## Running app

4. mvn spring-boot:run

## REST API Endpoints

#### Create task
curl --location --request POST 'http://DOMAIN:PORT/tasks' --header 'Content-Type: application/json' --data-raw '{"name": "New task name", "description":"New task description"}'

#### Get all tasks
curl --location --request GET 'http://DOMAIN:PORT/tasks'

#### Get task
curl --location --request GET 'http://DOMAIN:PORT/tasks/1'

#### Update task
curl --location --request PATCH 'http://DOMAIN:PORT/tasks/1' --header 'Content-Type: application/json' --data-raw '{"status": "DONE"}'

#### Delete task
curl --location --request DELETE 'http://DOMAIN:PORT/tasks/1' --header 'Content-Type: application/json' --data-raw ''
