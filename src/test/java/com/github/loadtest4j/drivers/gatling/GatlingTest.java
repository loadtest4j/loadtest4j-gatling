package com.github.loadtest4j.drivers.gatling;

import com.github.loadtest4j.drivers.gatling.junit.IntegrationTest;
import com.github.loadtest4j.loadtest4j.Driver;
import com.github.loadtest4j.loadtest4j.DriverResult;
import com.xebialabs.restito.server.StubServer;
import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.Duration;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Condition.*;
import static org.junit.Assert.assertEquals;

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
        assertEquals(0, result.getRequests());
        // And
        verifyHttp(httpServer).once(method(Method.POST), uri("/"));
    }

    @Ignore
    public void testRunWithNoRequests() {
        // Given
        final Driver driver = sut();

        // When
        final DriverResult result = driver.run(Collections.emptyList());

        // Then
        assertEquals(0, result.getErrors());
        assertEquals(0, result.getRequests());
    }
}
