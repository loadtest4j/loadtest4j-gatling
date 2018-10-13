package org.loadtest4j.drivers.gatling;

import org.junit.Test;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class GatlingResponseDistributionTest {
    @Test
    public void testWithSwappedMinMax() {
        final GatlingResponseDistribution distribution = new GatlingResponseDistribution(Collections.singletonMap(500, 2));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> distribution.getResponseCountBetween(Duration.ofSeconds(1), Duration.ZERO))
                .withMessage("Max must be greater than min.");
    }

    @Test
    public void testWithEqualMinMax() {
        final GatlingResponseDistribution distribution = new GatlingResponseDistribution(Collections.singletonMap(1000, 2));

        final long count = distribution.getResponseCountBetween(Duration.ofSeconds(1), Duration.ofSeconds(1));
        assertThat(count).isEqualTo(2);
    }

    @Test
    public void testWithEmptyDistribution() {
        final GatlingResponseDistribution distribution = new GatlingResponseDistribution(Collections.emptyMap());

        final long count = distribution.getResponseCountBetween(Duration.ZERO, Duration.ofSeconds(1));
        assertThat(count).isZero();
    }

    @Test
    public void testWithValueAboveRange() {
        final GatlingResponseDistribution distribution = new GatlingResponseDistribution(Collections.singletonMap(2001, 1));

        final long count = distribution.getResponseCountBetween(Duration.ofSeconds(1), Duration.ofSeconds(2));
        assertThat(count).isZero();
    }

    @Test
    public void testWithValueBelowRange() {
        final GatlingResponseDistribution distribution = new GatlingResponseDistribution(Collections.singletonMap(999, 1));

        final long count = distribution.getResponseCountBetween(Duration.ofSeconds(1), Duration.ofSeconds(2));
        assertThat(count).isZero();
    }

    @Test
    public void testMultipleValuesInRange() {
        final GatlingResponseDistribution distribution = new GatlingResponseDistribution(dualMap(500, 2, 501, 1));

        final long count = distribution.getResponseCountBetween(Duration.ZERO, Duration.ofSeconds(1));
        assertThat(count).isEqualTo(3);
    }

    @Test
    public void testValid() {
        final GatlingResponseDistribution distribution = new GatlingResponseDistribution(Collections.singletonMap(500, 2));

        final long count = distribution.getResponseCountBetween(Duration.ZERO, Duration.ofSeconds(1));
        assertThat(count).isEqualTo(2);
    }

    private static <K, V> Map<K, V> dualMap(K k1, V v1, K k2, V v2) {
        return new ConcurrentHashMap<K, V>() {{
            put(k1, v1);
            put(k2, v2);
        }};
    }
}
