# TaskScheduler

This is a very simple tool to manage and schedule tasks.

## Requirements

Before running it make sure you have Java 17 and docker set up.

## Build and run

1. Clone this git repository: `git clone https://github.com/kulikg/tasks.git`
1. Build it: `./gradlew build` (this will build the app's docker container, run some and run some tests on it. Should take a couple of minutes)
1. Fire it up: `docker-compose up` (will start a mysql container, provision a database, and link it to the application)
1. You may explore it's endpoints by visiting `http://localhost:8080/swagger-ui/index.html`

