package org.loadtest4j.drivers.gatling;

import org.junit.Test;
import org.loadtest4j.driver.Driver;
import scala.concurrent.duration.FiniteDuration;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class GatlingBuilderTest {
    @Test
    public void shouldHaveDefaultValues() {
        final Gatling gatling = (Gatling) GatlingBuilder.withUrl("https://example.com").build();

        assertThat(gatling)
                .hasFieldOrPropertyWithValue("duration", FiniteDuration.create(1, TimeUnit.SECONDS))
                .hasFieldOrPropertyWithValue("usersPerSecond", 1)
                .hasFieldOrPropertyWithValue("url", "https://example.com");
    }

    @Test
    public void shouldSetCustomValues() {
        final Gatling gatling = (Gatling) GatlingBuilder.withUrl("https://example.com")
                .withDuration(Duration.ofSeconds(2))
                .withUsersPerSecond(2)
                .build();

        assertThat(gatling)
                .hasFieldOrPropertyWithValue("duration", FiniteDuration.create(2, TimeUnit.SECONDS))
                .hasFieldOrPropertyWithValue("usersPerSecond", 2);
    }

    @Test
    public void shouldBeImmutable() {
        final GatlingBuilder builder = GatlingBuilder.withUrl("https://example.com");

        final Driver before = builder.build();

        builder.withDuration(Duration.ofSeconds(2));
        builder.withUsersPerSecond(2);

        final Driver after = builder.build();

        assertThat(after).isEqualToComparingFieldByField(before);
    }
}
