KRY
--------------------

Poll Service
---------------------
In This project I try to implement a service for finding all urls wich are save in database and then call them priodically to check the health of them.Plus, There are some web services to create ,delete or update a service Identity.

Requirements and Installation
-----------------------------
First of all, you should run docker compose with docker-compose.yml to make connection with kry database, After that you can run the main method of the application
and then you can find all the services in this address "http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config".

- Using swagger as a ui of Api
- Mysql database
- liquibase
- java 11
- Last verion of spring boot
- Gradle

Explanation
-----------------------------
This application including this five services Api:
 - /service/poll/health-check
 - /service/get/{serviceId}
 - /service/create/{userId}
 - /service/update
 - /service/delete

Whole of them have their own test in test directory. First service run with user fairness mechanism, On the other word, for each users I get just a service to check the health of it ,so if I have two users which one of them have 100 services but the other one just have a service to check ,this system behave fairness between these two users services.  
