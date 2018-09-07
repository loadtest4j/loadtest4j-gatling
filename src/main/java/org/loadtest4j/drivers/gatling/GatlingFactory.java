package org.loadtest4j.drivers.gatling;

import org.loadtest4j.driver.Driver;
import org.loadtest4j.driver.DriverFactory;
import scala.concurrent.duration.FiniteDuration;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class GatlingFactory implements DriverFactory {
    @Override
    public Set<String> getMandatoryProperties() {
        return setOf("duration", "url", "usersPerSecond");
    }

    /**
     * Creates a Gatling driver using the following properties.
     *
     * Mandatory properties:
     *
     * - `duration`
     * - `url`
     * - `usersPerSecond`
     *
     */
    @Override
    public Driver create(Map<String, String> properties) {
        final FiniteDuration duration = seconds(Long.parseLong(properties.get("duration")));
        final String url = properties.get("url");
        final int usersPerSecond = Integer.parseInt(properties.get("usersPerSecond"));

        return new Gatling(duration, url, usersPerSecond);
    }

    private static FiniteDuration seconds(long seconds) {
        return new FiniteDuration(seconds, TimeUnit.SECONDS);
    }

    private static Set<String> setOf(String... values) {
        // This utility method can be replaced when Java 9+ is more widely adopted
        final Set<String> internalSet = new LinkedHashSet<>(Arrays.asList(values));
        return Collections.unmodifiableSet(internalSet);
    }
}
