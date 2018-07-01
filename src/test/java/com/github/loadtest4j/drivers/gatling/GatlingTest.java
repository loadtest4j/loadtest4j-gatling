package com.github.loadtest4j.drivers.gatling;

import com.github.loadtest4j.loadtest4j.driver.Driver;
import static com.github.loadtest4j.drivers.gatling.DriverResultAssert.assertThat;
import com.github.loadtest4j.loadtest4j.LoadTesterException;
import com.github.loadtest4j.loadtest4j.driver.DriverRequest;
import com.github.loadtest4j.loadtest4j.driver.DriverResult;
import com.xebialabs.restito.server.StubServer;
import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Condition.*;
import org.junit.rules.ExpectedException;
import scala.concurrent.duration.FiniteDuration;

public class GatlingTest {
    private StubServer httpServer = new StubServer();

    @Before
    public void startServer() {
        httpServer.run();
    }

    @After
    public void stopServer() {
        httpServer.stop();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Driver sut() {
        final String serviceUrl = String.format("http://localhost:%d", httpServer.getPort());
        return new Gatling(new FiniteDuration(3, TimeUnit.SECONDS), serviceUrl, 1);
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
        assertThat(result)
                .hasKo(0)
                .hasOkGreaterThan(1)
                .hasActualDurationGreaterThan(java.time.Duration.ZERO)
                .hasMaxResponseTimeGreaterThan(java.time.Duration.ZERO)
                .hasReportUrlWithScheme("file");
        // And
        verifyHttp(httpServer).atLeast(1, method(Method.GET), uri("/"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRunWithInvalidPercentile() {
        final Driver driver = sut();
        whenHttp(httpServer).match(get("/")).then(status(HttpStatus.OK_200));

        final DriverResult result = driver.run(Collections.singletonList(DriverRequests.get("/")));

        result.getResponseTime().getPercentile(102);
    }

    @Test
    public void testRunWithQueryString() {
        // Given
        final Driver driver = sut();
        // And
        whenHttp(httpServer).match(get("/")).then(status(HttpStatus.NOT_FOUND_404));
        // And
        whenHttp(httpServer).match(get("/"), parameter("foo", "bar")).then(status(HttpStatus.OK_200));

        // When
        final DriverResult result = driver.run(Collections.singletonList(DriverRequests.getWithQueryParams("/", Collections.singletonMap("foo", "bar"))));

        // Then
        assertThat(result)
                .hasOkGreaterThan(1)
                .hasKo(0);
    }

    @Test
    public void testRunWithElaborateRequest() {
        // Given
        final Driver driver = sut();
        // And
        whenHttp(httpServer)
                .match(post("/"), withHeader("Accept", "application/json"), withHeader("Content-Type", "application/json"), withPostBodyContaining("{}"))
                .then(status(HttpStatus.OK_200));

        // When
        final Map<String, String> headers = new ConcurrentHashMap<String, String>() {{
            put("Accept", "application/json");
            put("Content-Type", "application/json");
        }};
        final List<DriverRequest> requests = Collections.singletonList(DriverRequests.post("/", "{}", headers));
        final DriverResult result = driver.run(requests);

        // Then
        assertThat(result)
                .hasOkGreaterThan(1)
                .hasKo(0);
        // And
        verifyHttp(httpServer).atLeast(1, method(Method.POST), uri("/"), withHeader("Accept", "application/json"), withHeader("Content-Type", "application/json"), withPostBodyContaining("{}"));
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
        assertThat(result)
                .hasOkGreaterThan(1)
                .hasKo(0);
        // And
        verifyHttp(httpServer).atLeast(1, method(Method.GET), uri("/"));
        // And
        verifyHttp(httpServer).atLeast(1, method(Method.GET), uri("/pets"));
    }

    @Test
    public void testRunWithNoRequests() {
        // Given
        final Driver driver = sut();

        // Expect
        thrown.expect(LoadTesterException.class);
        thrown.expectMessage("No requests were specified for the load test.");

        // When
        driver.run(Collections.emptyList());
    }

    static {
        // Silence Restito logging.
        Logger.getLogger("org.glassfish.grizzly").setLevel(Level.OFF);
    }
}
