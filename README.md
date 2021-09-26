KRY
--------------------

Poll Service
---------------------
In This project I try to implement a service for finding all urls which were saved in database and then call them priodically to check the health of them. Plus, There are some web services to create ,delete or update a service Identity.

Requirements and Installation
-----------------------------
First of all, you should run docker compose with docker-compose.yml to make connection with kry database, After that you can run the main method of the application
and then you can find all the services in this address "http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config".

Tools
-----------------------------
- Using swagger as a ui of Api
- Mysql database
- liquibase
- java 11
- Last verion of spring boot
- Gradle
- jupiter

Explanation
-----------------------------
This application including this five services Api:
 - /service/poll/health-check
 - /service/get/{serviceId}
 - /service/create/{userId}
 - /service/update
 - /service/delete

All of them have their own test in test directory.

1) First, Polling service "poll/health-check" put the services of each users on blocking queue in a thread and, then the other thread run scheduling evere 15 seconds to take from this queue. After that, call the web service with given url.Finally, If get web service can call successfully the web service status gonna be OK or else Fail.

2) Second, service "service/get/{serviceId}" can get web service with it's id and send it to the client.

3) Third this web service "service/create/{userId}" can help us to create a new service related to current user.

4) Forth this web service "service/update" can help us to update an exists service.

5) Fifth this web service "service/delete" can help us to delete an exists service.







