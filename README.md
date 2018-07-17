# loadtest4j-gatling

[![Build Status](https://travis-ci.com/loadtest4j/loadtest4j-gatling.svg?branch=master)](https://travis-ci.com/loadtest4j/loadtest4j-gatling)
[![Codecov](https://codecov.io/gh/loadtest4j/loadtest4j-gatling/branch/master/graph/badge.svg)](https://codecov.io/gh/loadtest4j/loadtest4j-gatling)
[![JitPack Release](https://jitpack.io/v/com.github.loadtest4j/loadtest4j-gatling.svg)](https://jitpack.io/#com.github.loadtest4j/loadtest4j-gatling)

A Gatling driver for loadtest4j.

## Setup

1. **Add the library** to your `pom.xml`:

    ```xml
     <dependency>
         <groupId>com.github.loadtest4j</groupId>
         <artifactId>loadtest4j-gatling</artifactId>
         <version>[version]</version>
     </dependency>   
     
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
    ```

2. **Configure the driver** in `src/test/resources/loadtest4j.properties`:
    
    ```properties
    loadtest4j.driver.duration = 10
    loadtest4j.driver.url = https://example.com
    loadtest4j.driver.usersPerSecond = 1
    ```

3. **Optional: Add advanced Gatling configuration** in `src/test/resources/gatling.conf`.

4. **Write your load tests** using the standard [LoadTester API](https://github.com/loadtest4j/loadtest4j).

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
