package com.github.loadtest4j.drivers.gatling;

import com.github.loadtest4j.loadtest4j.driver.Driver;
import com.github.loadtest4j.loadtest4j.driver.DriverFactory;
import org.junit.Test;

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
                .containsExactly("duration", "url");
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

        final Driver driver = sut.create(properties);

        assertThat(driver)
                .isNotNull();
    }

    private DriverFactory driverFactory() {
        return new GatlingFactory();
    }
}
