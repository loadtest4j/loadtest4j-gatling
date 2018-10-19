package org.loadtest4j.drivers.gatling;

import org.loadtest4j.Body;
import org.loadtest4j.BodyPart;
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

    protected static DriverRequest uploadMultiPart(String path, Path a, Path b, Map<String, String> headers) {
        final Body body = Body.parts(BodyPart.file(a), BodyPart.file(b));
        return new DriverRequest(body, headers, "POST", path, Collections.emptyMap());
    }

    protected static DriverRequest uploadMultiPart(String path, String a, String b, Map<String, String> headers) {
        final Body body = Body.parts(BodyPart.string(a), BodyPart.string(b));
        return new DriverRequest(body, headers, "POST", path, Collections.emptyMap());
    }
}
