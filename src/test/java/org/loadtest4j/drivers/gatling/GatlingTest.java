package org.loadtest4j.drivers.gatling;

import com.xebialabs.restito.builder.verify.VerifyHttp;
import com.xebialabs.restito.server.StubServer;
import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.loadtest4j.LoadTesterException;
import org.loadtest4j.driver.Driver;
import org.loadtest4j.driver.DriverRequest;
import org.loadtest4j.driver.DriverResult;
import scala.concurrent.duration.FiniteDuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Condition.*;
import static org.loadtest4j.drivers.gatling.DriverResultAssert.assertThat;

public class GatlingTest {
    static {
        // Silence Restito logging.
        Logger.getLogger("org.glassfish.grizzly").setLevel(Level.OFF);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Path createTempFile(String name, String content) {
        final Path file;
        try {
            file = temporaryFolder.newFile(name).toPath();
            Files.write(file, Collections.singleton(content));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    private StubServer httpServer = new StubServer();

    @Before
    public void startServer() {
        httpServer.run();
    }

    @After
    public void stopServer() {
        httpServer.stop();
    }

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
                .hasMaxResponseTimeGreaterThan(java.time.Duration.ZERO);
        // And
        VerifyHttp.verifyHttp(httpServer).atLeast(1, method(Method.GET), uri("/"));
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
                .match(post("/"),
                        withHeader("Accept", "application/json"),
                        withHeader("Content-Type", "application/json"),
                        withPostBodyContaining("{}"))
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
        VerifyHttp.verifyHttp(httpServer).atLeast(1,
                method(Method.POST), uri("/"),
                withHeader("Accept", "application/json"),
                withHeader("Content-Type", "application/json"),
                withPostBodyContaining("{}"));
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
        VerifyHttp.verifyHttp(httpServer).atLeast(1, method(Method.GET), uri("/"));
        // And
        VerifyHttp.verifyHttp(httpServer).atLeast(1, method(Method.GET), uri("/pets"));
    }

    @Test
    public void testRunWithMultiPartFileUpload() {
        // Given
        final Driver driver = sut();
        // And
        final Path foo = createTempFile("foo.txt", "foo");
        final Path bar = createTempFile("bar.txt", "bar");
        // And
        whenHttp(httpServer)
                .match(post("/"),
                        withHeader("Authorization", "Bearer abc123"),
                        MultiPartConditions.withMultipartFormHeader(),
                        MultiPartConditions.withPostBodyContainingFilePart("foo.txt", "text/plain", "foo"),
                        MultiPartConditions.withPostBodyContainingFilePart("bar.txt", "text/plain", "bar"))
                .then(status(HttpStatus.OK_200));

        // When
        final Map<String, String> headers = Collections.singletonMap("Authorization", "Bearer abc123");
        final List<DriverRequest> requests = Collections.singletonList(DriverRequests.uploadMultiPart("/", foo, bar, headers));
        final DriverResult result = driver.run(requests);

        // Then
        assertThat(result)
                .hasOkGreaterThan(1)
                .hasKo(0);
        // And
        VerifyHttp.verifyHttp(httpServer).atLeast(1,
                method(Method.POST),
                uri("/"),
                withHeader("Authorization", "Bearer abc123"),
                MultiPartConditions.withMultipartFormHeader(),
                MultiPartConditions.withPostBodyContainingFilePart("foo.txt", "text/plain", "foo"),
                MultiPartConditions.withPostBodyContainingFilePart("bar.txt", "text/plain", "bar"));
    }

    @Test
    public void testRunWithMultiPartStringUpload() {
        // Given
        final Driver driver = sut();
        // And
        whenHttp(httpServer)
                .match(post("/"),
                        withHeader("Authorization", "Bearer abc123"),
                        MultiPartConditions.withMultipartFormHeader(),
                        MultiPartConditions.withPostBodyContainingStringPart("a", "foo"),
                        MultiPartConditions.withPostBodyContainingStringPart("b", "bar"))
                .then(status(HttpStatus.OK_200));

        // When
        final Map<String, String> headers = Collections.singletonMap("Authorization", "Bearer abc123");
        final List<DriverRequest> requests = Collections.singletonList(DriverRequests.uploadMultiPart("/", "a", "foo", "b", "bar", headers));
        final DriverResult result = driver.run(requests);

        // Then
        assertThat(result)
                .hasOkGreaterThan(1)
                .hasKo(0);
        // And
        VerifyHttp.verifyHttp(httpServer).atLeast(1,
                method(Method.POST),
                uri("/"),
                withHeader("Authorization", "Bearer abc123"),
                MultiPartConditions.withMultipartFormHeader(),
                MultiPartConditions.withPostBodyContainingStringPart("a", "foo"),
                MultiPartConditions.withPostBodyContainingStringPart("b", "bar"));
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
}
