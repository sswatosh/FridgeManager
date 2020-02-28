A basic REST API for managing refrigerators full of items.

Start server with './gradlew run'.

Import the provided Postman collection or see the test requests in MainTest.java to try it out.

Run tests with './gradlew test' and build a coverage report with './gradlew jacocoTestReport'.
Find the coverage report at /build/reports/jacoco/test/html/index.html.

Application and request logs are generate in /logs.

Application metrics are reported via JMX - attach to the process via JConsole to view.
JVM metrics are reported, as well as custom metrics for fridge and item counts.
