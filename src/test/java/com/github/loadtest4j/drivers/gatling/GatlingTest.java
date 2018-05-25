package com.github.loadtest4j.drivers.gatling;

import com.github.loadtest4j.drivers.gatling.junit.IntegrationTest;
import com.github.loadtest4j.loadtest4j.Driver;
import com.github.loadtest4j.loadtest4j.DriverResult;
import org.junit.Ignore;
import org.junit.experimental.categories.Category;

import java.time.Duration;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class GatlingTest {
    @Ignore
    public void testRun() {
        // Given
        final Driver driver = new Gatling(Duration.ofSeconds(1),"http://localhost:3000", 1);

        // When
        final DriverResult result = driver.run(Collections.emptyList());

        // Then
        assertEquals(0, result.getErrors());
        assertEquals(0, result.getRequests());
    }
}
