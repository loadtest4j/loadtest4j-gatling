# loadtest4j-gatling

[![Build Status](https://travis-ci.com/loadtest4j/loadtest4j-gatling.svg?branch=master)](https://travis-ci.com/loadtest4j/loadtest4j-gatling)
[![Codecov](https://codecov.io/gh/loadtest4j/loadtest4j-gatling/branch/master/graph/badge.svg)](https://codecov.io/gh/loadtest4j/loadtest4j-gatling)
[![Maven Central](https://img.shields.io/maven-central/v/org.loadtest4j.drivers/loadtest4j-gatling.svg)](http://repo2.maven.org/maven2/org/loadtest4j/drivers/loadtest4j-gatling/)

A Gatling driver for loadtest4j.

## Setup

1. **Open your Web service project** or make a new project.

2. **Add the library** to your project:

    ```xml
    <dependency>
        <groupId>org.loadtest4j.drivers</groupId>
        <artifactId>loadtest4j-gatling</artifactId>
        <scope>test</scope>
    </dependency>
    ```

3. **Configure the driver** in `src/test/resources/loadtest4j.properties`:
    
    ```properties
    loadtest4j.driver.duration = 10
    loadtest4j.driver.url = https://example.com
    loadtest4j.driver.usersPerSecond = 1
    ```

4. **Optional: Add advanced Gatling configuration** in `src/test/resources/gatling.conf`.

5. **Write your load tests** using the standard [LoadTester API](https://github.com/loadtest4j/loadtest4j).

## Control Gatling logging

To reduce Gatling log output, change the log threshold for its noisiest classes in your SLF4J logger configuration.

If you are using Logback (the default Gatling logger), you could configure `logback.xml` like this:

```xml
<configuration>
    <!-- Set up the root logger and appender, then include these lines -->
    <logger name="akka.event.slf4j.Slf4jLogger" level="OFF" />
    <logger name="io.netty" level="OFF" />
    <logger name="io.gatling" level="OFF" />
    <logger name="org.asynchttpclient.netty" level="OFF" />
</configuration>
```
