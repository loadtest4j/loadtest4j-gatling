package com.github.loadtest4j.drivers.gatling;

import com.github.loadtest4j.drivers.gatling.junit.IntegrationTest;
import com.github.loadtest4j.loadtest4j.Driver;
import com.github.loadtest4j.loadtest4j.DriverRequest;
import com.github.loadtest4j.loadtest4j.DriverResult;
import com.github.loadtest4j.loadtest4j.LoadTesterException;
import com.xebialabs.restito.server.StubServer;
import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Condition.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
public class GatlingTest {

    private StubServer httpServer;

    static {
        // Silence Restito logging.
        Logger.getLogger("org.glassfish.grizzly").setLevel(Level.OFF);
    }

    @Before
    public void startServer() {
        httpServer = new StubServer().run();
    }

    @After
    public void stopServer() {
        httpServer.stop();
    }

    private String getServiceUrl() {
        return String.format("http://localhost:%d", httpServer.getPort());
    }

    private Driver sut() {
        return new Gatling(Duration.ofSeconds(20),getServiceUrl(), 1);
    }

    @Test
    public void testRun() {
        // Given
        final Driver driver = sut();
        // And
        whenHttp(httpServer).match(get("/")).then(status(HttpStatus.OK_200));

        // When
        final DriverResult result = driver.run(Collections.singletonList(DriverRequests.get("/")));

        // Then
        assertEquals(0, result.getErrors());
        assertGreaterThanOrEqualTo(1, result.getRequests());
        // And
        verifyHttp(httpServer).atLeast(1, method(Method.GET), uri("/"));
    }

    @Test
    public void testRunWithMultipleRequests() {
        // Given
        final Driver driver = sut();
        // And
        whenHttp(httpServer).match(get("/")).then(status(HttpStatus.OK_200));
        // And
        whenHttp(httpServer).match(get("/pets")).then(status(HttpStatus.OK_200));

        // When
        final List<DriverRequest> requests = Arrays.asList(DriverRequests.get("/"), DriverRequests.get("/pets"));
        final DriverResult result = driver.run(requests);

        // Then
        assertGreaterThanOrEqualTo(1, result.getRequests());
        assertEquals(0, result.getErrors());
        // And
        verifyHttp(httpServer).atLeast(1, method(Method.GET), uri("/"));
        // And
        verifyHttp(httpServer).atLeast(1, method(Method.GET), uri("/pets"));
    }

    @Test
    public void testRunWithNoRequests() {
        // Given
        final Driver driver = sut();

        // When
        try {
            driver.run(Collections.emptyList());

            fail("This should not work.");
        } catch (LoadTesterException e) {
            // Then
            assertEquals("No requests were specified for the load test.", e.getMessage());
        }
    }

    private static void assertGreaterThanOrEqualTo(long expected, long actual) {
        final String msg = String.format("Expected %d to be >= %d, but it was not.", actual, expected);
        assertTrue(msg, actual >= expected);
    }
}
