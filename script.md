# Kotlin + Spring demo

---
## ERASE THIS

# BUSINESS GOALS 
Business goal is to demo Kotlin inside a Spring app.
Support would be an app that does analysis on live Velib Data
To make it sexy we'll throw websockets & d3js into the mix

What we wish to showcase in Kotlin :
- [x] String templating -> easy with logs, can have traces of scheduling happening
- [ ] Collection manipulation with lambdas -> filter and map ; can we flatmap some stuff ?
- [ ] Things around classes :
    - [ ] Constructor -> also init {} blocks
    - [x] data class -> Everything is a dataclass in this demo
    - [x] properties -> StationStatusResponse, convert integers to booleans
    - [ ] Delegates, i.e. lazy properties
    - [ ] Functions : named args and default values
    - [ ] Invoke stuff ?
- [ ] Things around types :
    - [x] Non-nullable by default -> Easy when doing the station thing
    - [ ] Pattern matching
    - [ ] Null-coalescing operator
- [ ] Extension methods
- [x] Coroutines -> Nice way to test it with the websockets things

Potentially interesting :
- DSLs with function literals
- Ranges

# Random thoughts
- Mmmmh populating the database is boring. Can paralellize it but that's about it
- Adding stuff to the DB doesn't work well with channels... Channels is for event-driven stuff.

# TODO
- Test RxJava and HTTP stuff for async clients
- Rework the database populating example


# NOTES WHILE BUILDING IT
## Getter for the list of stations
0. Initialize git repo

1. Build initializr package with : 
    - web
    - jpa
    - h2
    - websockets ??

2. Remove the mvn cruft

3. Add assertj : org.assertj / asserj-core

4. If you want to read from file (e.g. for test fixtures) : 
javaClass.classLoader.getResourceAsStream("fixtures/velib-station-list.html").reader().readText()

5. Lambdas in Kotlin are {s: String -> s.toUpperCase()}

6. H2 database config :
```
spring.datasource:
  url: jdbc:h2:file:~/workspace/demos/kotlin-spring-velib/h2
  driverClassName: org.h2.Driver

spring:
  jpa:
    hibernate:
      ddl-auto: update
```

7. Interesting stuff for command-line-runner


## Velib station scrapper
1. This is an interesting use of channels and launch { }

2. Handle exceptions when scraping !

3. To unmarshal XML, don't forget :
-> @XmlRootElement(name = "station")
-> var all the things, with default value

4. For testing ListenableFuture, think of AsyncResult


    
## SCRIPT STARTS HERE
---


