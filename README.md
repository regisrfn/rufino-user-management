# ITEM - API
Spring boot rest api

## PREREQUISITES
- Java
- Docker | https://docs.docker.com/engine/install/ubuntu/ 

## DOCKER
- RUNNING POSTGRES
- create:
docker run --name user_management_db -p 5432:5432 -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_DB=user_management -d postgres:alpine
- stop:
docker stop postgres
- start:
docker start postgres

## DATABASE DESCRIPTION

|       Users           |
|--------------------   |
|userId: UUID (PK)      |
|userName: String       |
|userEmail: String      |
|userPassword: String   |
|createdAt: Timestamp   |

