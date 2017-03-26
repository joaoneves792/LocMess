# LocMess
Mobile and Ubiquitous Computing Project (2017)


# Instructions
1. cd crypto-lib

2. mvn clean compile install

3. cd LocMessServer

4. mvn clean compile spring-boot:run

## Server API
Server is accessible at localhost:8080

-/register (ex:localhost:8080/register?username=admin&password=12345)

-/login (ex:localhost:8080/login?username=admin&password=12345)

All of the other calls require the user id

-/logout (ex:localhost:8080/logout?id=-2768685926897593927)

-/location (GET lists all locations)(POST adds a new location)

-/location/{locationName} (DELETE removes this location)
