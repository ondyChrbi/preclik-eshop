# Preclík e-shop

Simple backend application created in Spring framework using Spring boot and other dependencies. To see full list of dependencies visit [build.gradle](build.gradle).

## Documentation

You can download documentation in Open Api 3 standard under the file [doc/api-docs.yaml]. Swagger editor is also part of the application so you dont have to download it. Just run the backend and visit URLs:

* http://localhost:8080/swagger-ui/index.html#/

## Databse

Application is using the H2 in memory database. No additional configuration like docker containers or installing database to your local machine is needed. 

Database history is managed by liquidbase. History of database changes are located in the [src/main/resources/db/changelog/changelog.sql](changelog.sql).

## Tests

Main test scenarios are covered by integration junit tests. To run them just type:

```
gradle test
```

## Run

To run application just clone this repository and run the folowing command:

```
gradle bootRun
```
