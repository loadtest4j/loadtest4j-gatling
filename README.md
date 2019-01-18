# loadtest4j-gatling

[![Build Status](https://travis-ci.com/loadtest4j/loadtest4j-gatling.svg?branch=master)](https://travis-ci.com/loadtest4j/loadtest4j-gatling)
[![Codecov](https://codecov.io/gh/loadtest4j/loadtest4j-gatling/branch/master/graph/badge.svg)](https://codecov.io/gh/loadtest4j/loadtest4j-gatling)
[![Maven Central](https://img.shields.io/maven-central/v/org.loadtest4j.drivers/loadtest4j-gatling.svg)](http://repo2.maven.org/maven2/org/loadtest4j/drivers/loadtest4j-gatling/)

Gatling driver for [loadtest4j](https://www.loadtest4j.org).

## Usage

With a new or existing Maven project open in your favorite editor...

### 1. Add the library

Add the library to your Maven project POM.

```xml
<dependency>
    <groupId>org.loadtest4j.drivers</groupId>
    <artifactId>loadtest4j-gatling</artifactId>
    <scope>test</scope>
</dependency>
```

### 2. Create the load tester

Use **either** the Factory **or** the Builder.

#### Factory

```java
LoadTester loadTester = LoadTesterFactory.getLoadTester();
```

```properties
# src/test/resources/loadtest4j.properties

loadtest4j.driver.duration = 60
loadtest4j.driver.url = https://example.com
loadtest4j.driver.usersPerSecond = 1
```

#### Builder

```java
LoadTester loadTester = GatlingBuilder.withUrl("https://example.com")
                                      .withDuration(Duration.ofSeconds(60))
                                      .withUsersPerSecond(1)
                                      .build();
```

### 3. Write load tests

Write load tests with your favorite language, test framework, and assertions. See the [loadtest4j documentation](https://www.loadtest4j.org) for further instructions.

```java
public class PetStoreLT {

    private static final LoadTester loadTester = /* see step 2 */ ;

    @Test
    public void shouldFindPets() {
        List<Request> requests = List.of(Request.get("/pet/findByStatus")
                                                .withHeader("Accept", "application/json")
                                                .withQueryParam("status", "available"));

        Result result = loadTester.run(requests);

        assertThat(result.getResponseTime().getPercentile(90))
            .isLessThanOrEqualTo(Duration.ofMillis(500));
    }
}
```

## Advanced Gatling configuration

All advanced Gatling configuration can be specified in the usual [`gatling.conf`](https://github.com/gatling/gatling/blob/master/gatling-core/src/main/resources/gatling-defaults.conf) format. Create `src/test/resources/gatling.conf` and place configuration overrides in there.

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
