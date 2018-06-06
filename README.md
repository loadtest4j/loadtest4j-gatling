# loadtest4j-gatling

[![Build Status](https://travis-ci.com/loadtest4j/loadtest4j-gatling.svg?branch=master)](https://travis-ci.com/loadtest4j/loadtest4j-gatling)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/22af7462aa80454094ae936613856f1d)](https://www.codacy.com/app/loadtest4j/loadtest4j-gatling)
[![JitPack Release](https://jitpack.io/v/com.github.loadtest4j/loadtest4j-gatling.svg)](https://jitpack.io/#com.github.loadtest4j/loadtest4j-gatling)

A Gatling driver for loadtest4j.

## Setup

Add the [JitPack](https://jitpack.io) repository to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Then add this library:

```xml
<dependency>
    <groupId>com.github.loadtest4j</groupId>
    <artifactId>loadtest4j-gatling</artifactId>
    <version>[git tag]</version>
</dependency>
```

## Usage

Add the file `loadtest4j.properties` to your `src/test/resources` directory and configure the load test driver:

```
loadtest4j.driver = com.github.loadtest4j.drivers.gatling.GatlingFactory
loadtest4j.driver.duration = 10
loadtest4j.driver.url = https://example.com
```

Then write your load tests in Java using the standard [LoadTester API](https://github.com/loadtest4j/loadtest4j).

## Control Gatling logging

The default Gatling logs are extremely noisy. To control the output, change the log threshold for its noisiest classes in your SLF4J logger configuration.

If you are using Logback (the default logger for Gatling), you could configure `logback.xml` like this:

```xml
<configuration>
    <!-- Set up the root logger and appender, then include these lines -->
    <logger name="akka.event.slf4j.Slf4jLogger" level="OFF" />
    <logger name="io.netty" level="OFF" />
    <logger name="io.gatling" level="OFF" />
    <logger name="org.asynchttpclient.netty" level="OFF" />
</configuration>
```
