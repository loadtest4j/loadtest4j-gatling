package com.github.loadtest4j.drivers.gatling;

import com.github.loadtest4j.loadtest4j.driver.DriverRequest;

import java.util.Collections;
import java.util.Map;

class DriverRequests {
    protected static DriverRequest get(String path) {
        return new DriverRequest("", Collections.emptyMap(), "GET", path, Collections.emptyMap());
    }

    protected static DriverRequest getWithQueryParams(String path, Map<String, String> queryParams) {
        return new DriverRequest("", Collections.emptyMap(), "GET", path, queryParams);
    }

    protected static DriverRequest post(String path, String body, Map<String, String> headers) {
        return new DriverRequest(body, headers, "POST", path, Collections.emptyMap());
    }
}
