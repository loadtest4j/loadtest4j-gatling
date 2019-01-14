package org.loadtest4j.drivers.gatling;

import org.junit.Test;
import org.loadtest4j.driver.Driver;
import scala.concurrent.duration.FiniteDuration;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class GatlingBuilderTest {

    private final GatlingBuilder builder = GatlingBuilder.withUrl("https://example.com");

    @Test
    public void shouldRequireUrl() {
        final Gatling gatling = (Gatling) builder.build();

        assertThat(gatling).hasFieldOrPropertyWithValue("url", "https://example.com");
    }

    @Test
    public void shouldSetDuration() {
        final Gatling gatling = (Gatling) builder
                .withDuration(Duration.ofSeconds(2))
                .build();

        assertThat(gatling).hasFieldOrPropertyWithValue("duration", FiniteDuration.create(2, TimeUnit.SECONDS));
    }

    @Test
    public void shouldSetDurationTo1SecondByDefault() {
        final Gatling gatling = (Gatling) builder.build();

        assertThat(gatling).hasFieldOrPropertyWithValue("duration", FiniteDuration.create(1, TimeUnit.SECONDS));
    }

    @Test
    public void shouldSetUsersPerSecond() {
        final Gatling gatling = (Gatling) builder
                .withUsersPerSecond(2)
                .build();

        assertThat(gatling).hasFieldOrPropertyWithValue("usersPerSecond", 2);
    }

    @Test
    public void shouldSetUsersPerSecondTo1ByDefault() {
        final Gatling gatling = (Gatling) builder.build();

        assertThat(gatling).hasFieldOrPropertyWithValue("usersPerSecond", 1);
    }

    @Test
    public void shouldBeImmutable() {
        final Driver before = builder.build();

        builder.withDuration(Duration.ofSeconds(2));
        builder.withUsersPerSecond(2);

        final Driver after = builder.build();

        assertThat(after).isEqualToComparingFieldByField(before);
    }
}
