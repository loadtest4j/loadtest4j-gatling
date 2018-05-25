package com.github.loadtest4j.drivers.gatling;

import com.github.loadtest4j.drivers.gatling.junit.UnitTest;
import com.github.loadtest4j.loadtest4j.Driver;
import com.github.loadtest4j.loadtest4j.DriverFactory;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class GatlingFactoryTest {
    @Test
    public void testGetMandatoryProperties() {
        final DriverFactory sut = sut();

        final Set<String> mandatoryProperties = sut.getMandatoryProperties();

        assertEquals(2, mandatoryProperties.size());
        assertTrue(mandatoryProperties.contains("duration"));
        assertTrue(mandatoryProperties.contains("url"));
    }

    @Test
    public void testCreate() {
        final DriverFactory sut = sut();

        final Map<String, String> properties = new HashMap<>();
        properties.put("duration", "2");
        properties.put("url", "https://example.com");

        final Driver driver = sut.create(properties);

        assertNotNull(driver);
    }

    private DriverFactory sut() {
        return new GatlingFactory();
    }
}
