package org.loadtest4j.drivers.gatling;

import org.junit.Test;
import org.loadtest4j.driver.Driver;
import org.loadtest4j.driver.DriverFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class GatlingFactoryTest {
    @Test
    public void testGetMandatoryProperties() {
        final DriverFactory sut = driverFactory();

        final Set<String> mandatoryProperties = sut.getMandatoryProperties();

        assertThat(mandatoryProperties)
                .containsExactly("duration", "url", "usersPerSecond");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetMandatoryPropertiesIsImmutable() {
        final DriverFactory sut = driverFactory();

        sut.getMandatoryProperties().add("foobarbaz123");
    }

    @Test
    public void testCreate() {
        final DriverFactory sut = driverFactory();

        final Map<String, String> properties = new ConcurrentHashMap<>();
        properties.put("duration", "2");
        properties.put("url", "https://example.com");
        properties.put("usersPerSecond", "1");

        final Driver driver = sut.create(properties);

        assertThat(driver)
                .isNotNull();
    }

    private DriverFactory driverFactory() {
        return new GatlingFactory();
    }
}
