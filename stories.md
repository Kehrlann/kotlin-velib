# User stories

## 1. Get the list of stations
AS any user
GIVEN I navigate to the /stations page
THEN I see the list of all available stations, sorted by ID

Shows :
- TDD
- Spring starter
- Template strings (loggers)
- Data classes (specifically equality)
- Teeny weeny bit of list handling (flatMap, sortedBy, reversed)
- ... ?


# 2. Make it faster !
AS a system admin
GIVEN I deploy a new instance of the service
THEN it boots under X seconds

Shows :
- RxJava
- AsyncRestTemplate
- Refactoring
- Extension methods (AsyncRestTemplate.getFlowable, Collection<Flowable>.waitAll())
- ... ?



# 3. Show the status of stations, in real-time
AS any user
GIVEN I navigate to the /status page
THEN I see a nice graph describing the list of stations

Shows :
- Coroutines
- 
