package org.loadtest4j.drivers.gatling;

import org.loadtest4j.driver.Driver;
import scala.concurrent.duration.FiniteDuration;

import java.time.Duration;

public class GatlingBuilder {

    private final Duration duration;
    private final String url;
    private final int usersPerSecond;

    private GatlingBuilder(Duration duration, String url, int usersPerSecond) {
        this.duration = duration;
        this.url = url;
        this.usersPerSecond = usersPerSecond;
    }

    public static GatlingBuilder withUrl(String url) {
        return new GatlingBuilder(Duration.ofSeconds(1), url, 1);
    }

    public GatlingBuilder withDuration(Duration duration) {
        return new GatlingBuilder(duration, url, usersPerSecond);
    }

    public GatlingBuilder withUsersPerSecond(int usersPerSecond) {
        return new GatlingBuilder(duration, url, usersPerSecond);
    }

    public Driver build() {
        final FiniteDuration d = asScalaDuration(duration);
        return new Gatling(d, url, usersPerSecond);
    }

    private static FiniteDuration asScalaDuration(Duration duration) {
        return scala.concurrent.duration.Duration.fromNanos(duration.toNanos());
    }
}
