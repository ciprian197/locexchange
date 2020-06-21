## Build project

.gradlew clean build

## Start mongoDB locally

sudo docker-compose up -d

## Run project

.gradlew clean bootRun

## Use the application

This application is a proof of concept.

The main feature of the application is to stream the location of a client (RSocketClientApplication) to a
provider (RSocketProviderApplication).

    1) Start the web application using the command for running the project.
    2) Run the RSocketProviderApplication application, the application will send messages to the web application that contain the username of the provider and its current location.
    3) Run the RSocketClientApplication application, the application shares user's current location with the server
