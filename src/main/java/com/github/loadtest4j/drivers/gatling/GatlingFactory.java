package com.github.loadtest4j.drivers.gatling;

import com.github.loadtest4j.loadtest4j.Driver;
import com.github.loadtest4j.loadtest4j.DriverFactory;

import java.time.Duration;
import java.util.*;

public class GatlingFactory implements DriverFactory {

    @Override
    public Set<String> getMandatoryProperties() {
        return setOf("duration", "url");
    }

    /**
     * Creates a Gatling driver using the following properties.
     *
     * Mandatory properties:
     *
     * - `duration`
     * - `url`
     *
     * Optional properties:
     *
     * - `users` (defaults to 1)
     */
    @Override
    public Driver create(Map<String, String> properties) {
        final Duration duration = Duration.ofSeconds(Long.valueOf(properties.get("duration")));
        final String url = properties.get("url");
        final int users = Integer.valueOf(properties.getOrDefault("users", "1"));

        return new Gatling(duration, url, users);
    }

    private static Set<String> setOf(String... values) {
        // This utility method can be replaced when Java 9+ is more widely adopted
        final Set<String> internalSet = new HashSet<>(Arrays.asList(values));
        return Collections.unmodifiableSet(internalSet);
    }
}
