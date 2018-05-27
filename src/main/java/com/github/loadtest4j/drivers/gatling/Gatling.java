package com.github.loadtest4j.drivers.gatling;

import com.github.loadtest4j.loadtest4j.Driver;
import com.github.loadtest4j.loadtest4j.DriverRequest;
import com.github.loadtest4j.loadtest4j.DriverResult;
import com.github.loadtest4j.loadtest4j.LoadTesterException;

import java.time.Duration;
import java.util.Collection;
import java.util.List;

class Gatling implements Driver {
    private final Duration duration;
    private final String url;
    private final int users;

    Gatling(Duration duration, String url, int users) {
        this.duration = duration;
        this.url = url;
        this.users = users;
    }

    @Override
    public DriverResult run(List<DriverRequest> requests) {
        validateNotEmpty(requests);

        final GatlingScalaBridge gatlingScalaBridge = new GatlingScalaBridge(duration.getSeconds(), url, users);
        return gatlingScalaBridge.run(requests);
    }

    private static <T> void validateNotEmpty(Collection<T> requests) {
        if (requests.size() < 1) {
            throw new LoadTesterException("No requests were specified for the load test.");
        }
    }
}
