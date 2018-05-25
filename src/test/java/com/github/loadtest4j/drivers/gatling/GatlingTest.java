package com.github.loadtest4j.drivers.gatling;

import com.github.loadtest4j.loadtest4j.Driver;
import com.github.loadtest4j.loadtest4j.DriverResult;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class GatlingTest {
    @Test
    public void testRun() {
        // Given
        final Driver driver = new Gatling("http://localhost:3000");

        // When
        final DriverResult result = driver.run(Collections.emptyList());

        // Then
        assertEquals(0, result.getErrors());
        assertEquals(0, result.getRequests());
    }
}
