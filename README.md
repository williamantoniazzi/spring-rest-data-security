# spring-rest-data-security
This sample project contains a simple set of group/members API using:
- [Flyway](https://flywaydb.org/) to versioning database's SQL changesets 
- [Swagger.io](https://swagger.io/) to generate an executable API documentation
- [Spring](https://spring.io/) to be metioned:
  - [Spring Core Framework](https://spring.io/projects/spring-framework)
    - [Spring Web MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
    - [Testing](https://docs.spring.io/spring-framework/reference/testing.html)
  - [Spring Boot v3](https://spring.io/projects/spring-boot)
  - [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
  - [Spring Security](https://spring.io/projects/spring-security)
  - [Lombok](https://projectlombok.org/)
  - [modelmapper](https://modelmapper.org/)
  - [MariaDB Java Client](https://mariadb.com/kb/en/about-mariadb-connector-j/)

## Pre-requirements
- ![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
- ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
  - [OpenJDK 17](https://openjdk.org/projects/jdk/17/)
- ![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
  - [Docker Compose](https://docs.docker.com/compose/)

## Build App

```bash
mvn clean package
```

## Running

- Running MariaDB via Docker Compose
```bash
# To execute and view logs via Docker Compose v2
docker compose up --force-recreate -d ; docker compose logs -f

# To execute and view logs via Docker Compose v1
docker-compose up --force-recreate -d ; docker-compose logs -f
```

- Stopping MariaDB via Docker Compose
```bash
# To stop via Docker Compose v2
docker compose down --remove-orphans

# To stop via Docker Compose v1
docker-compose down --remove-orphans
```
- Running App
```bash
# To run App via Maven after start MariaDB via Docker Compose
mvn clean spring-boot:run
```