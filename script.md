# Kotlin <3 Spring 
## Intro
## Live coding
### 1. Kotlin basics
STORY 1 : See list of velib stations
- Spring initlizr :
    - Web
    - Thymeleaf
    - JPA
    - h2
    - Dev tools
- Remove mvn / .mvn cruft
- Test
- Import AssertJ
- Create Station()
    - Top level stuff
    - Var vs val
    - Secondary constructors
    - Properties
    - Null safety and null coalescing operator
    - Lazy properties
        - Test it with a data
    - Class vs data class (equality, toString)
    - Default values in primary constructor
- StationRepository, easy (talk about extends / implements)
- StationController, TDD style
    - Create StationController
    - Create WebMvcTest for status() and view name
    - Run it, explain how the feedback loop is horrible
    - Whenever vs `when`
    
Okay ! I won't do all of it TDD-style, because it would take forever

### 2. A more concrete example
- StationScraper : tests have been done, do
    - getStationsFromPage
    - getPage
    - getAllStations : more info on collection manipulation
        - Maybe do it funkily with filter and reversed

STORY : make the scraping faster
- First, measure time with measureTimeMillis
    - Kotlin block (remember map { } ?)
    - Run and show it in the the logs
- Ok fine, so we could do it in parallel
    - AsyncRestRemplates and ListenableFutures are one way
    - If you like RxJava, you could use that
- Change the injected RestTemplate to AsyncRestTemplate
    - Tests unhappy
    - Refactor a little bit
    - Change should work now
    - Make it blocking
- Cool, how about RxJava and extesnsion methods
    - Change getForEntity to getFlowable
    - Test, still works
    - Now, RxJava's zip to waitAll()
    - Still works
    - Measure the time : it should be faster !

### 3. More advanced stuff
- Lots of advanced stuff, e.g. with blocks, we can do custom DSLs
    - Show the HTML one
    - Pompier example ?
- Co-routines, show the sync / async
    - This will be available in the repo

