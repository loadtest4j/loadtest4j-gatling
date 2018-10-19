package org.loadtest4j.drivers.gatling;

import org.loadtest4j.Body;
import org.loadtest4j.driver.DriverRequest;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

class DriverRequests {
    protected static DriverRequest get(String path) {
        return new DriverRequest(Body.string(""), Collections.emptyMap(), "GET", path, Collections.emptyMap());
    }

    protected static DriverRequest getWithQueryParams(String path, Map<String, String> queryParams) {
        return new DriverRequest(Body.string(""), Collections.emptyMap(), "GET", path, queryParams);
    }

    protected static DriverRequest post(String path, String body, Map<String, String> headers) {
        return new DriverRequest(Body.string(body), headers, "POST", path, Collections.emptyMap());
    }

    protected static DriverRequest upload(String path, Path file, Map<String, String> headers) {
        return new DriverRequest(Body.file(file), headers, "POST", path, Collections.emptyMap());
    }
}
