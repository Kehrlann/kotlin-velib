# Kotlin sample app

This app was used a demo support for the Paris Spring Meetup.
Here is a [link to the meetup](https://www.meetup.com/fr-FR/Spring-Meetup-Paris/events/245569984/).
Here is a [link to the screencast](https://youtu.be/At0Add4po3s). Unfortunately, it was cut off and
so the beginning is missing.

## How to run the code
To run the project, two solutions :
- Run both projects manually, e.g. 
    ```
    $ cd velib-app
    $ mvn spring-boot:run
    $ cd mock-server
    $ mvn spring-boot:run
    ```
- Run both projects in a multithreaded maven environment (experimental) :
    ```
    $ mvn -T 2 -pl velib-app,mock-server clean spring-boot:run -Dfork=true
    ```

You will have a mock server for the Velib API at localhost:8081, 
and the app will live at http://localhost:8080. You can visit :
- http://localhost:8080/stations
- http://localhost:8080/metrics

## About the code
- All the code produced during the meetup is under the tag `meetup_code`.
- The tag `coroutines` has another more stuff, to compare coroutines vs synchronous code.
- The most recent version is not thoroughly tested, it is more of a spike to show coroutine
- Also, the javascript is not great, and is not tested
- In intermediate commits between `meetup_code` and `coroutines`, there is some d3js crazyness,
  but I'd avoid running it if I were you. It hits the Velib API hard, and is a resource hog
  to render properly in your browser.
