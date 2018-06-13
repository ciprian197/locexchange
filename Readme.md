#Build project
.gradlew clean build

#Run project
.gradlew clean bootRun

#Use the application
1. Run the ReactiveWebSocketProvider application, the application will send messages to the web application that 
contain the username of the provider and its current location.
2. Run the ReactiveWebSocketClient application, the application sends messages to the web application, first a request
is made to get a provider's details, this provider will be assigned to receive the location send from the client. 
After that the client will send messages using the websocket endpoint to the application, and those messages will later
be received by the client.