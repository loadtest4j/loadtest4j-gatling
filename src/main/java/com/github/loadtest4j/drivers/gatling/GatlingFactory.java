package com.github.loadtest4j.drivers.gatling;

import com.github.loadtest4j.loadtest4j.Driver;
import com.github.loadtest4j.loadtest4j.DriverFactory;

import java.time.Duration;
import java.util.*;

public class GatlingFactory implements DriverFactory {

    @Override
    public Set<String> getMandatoryProperties() {
        return setOf("url");
    }

    /**
     * Creates a Gatling driver using the following properties.
     *
     * Mandatory properties:
     *
     * - `url`
     *
     * TODO Optional properties:
     *
     */
    @Override
    public Driver create(Map<String, String> properties) {
        final Duration duration = Duration.ofSeconds(Long.valueOf(properties.get("duration")));
        final String url = properties.get("url");

        return new Gatling(url);
    }

    private static Set<String> setOf(String... values) {
        // This utility method can be replaced when Java 9+ is more widely adopted
        final Set<String> internalSet = new HashSet<>(Arrays.asList(values));
        return Collections.unmodifiableSet(internalSet);
    }
}
