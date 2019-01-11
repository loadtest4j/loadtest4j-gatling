package org.loadtest4j.drivers.gatling;

import org.loadtest4j.driver.DriverResponseTime;
import org.loadtest4j.driver.DriverResult;

import java.time.Duration;

public class GatlingResult implements DriverResult {

    private final long ok;
    private final long ko;
    private final Duration actualDuration;
    private final DriverResponseTime responseTime;

    public GatlingResult(long ok, long ko, Duration actualDuration, DriverResponseTime responseTime) {
        this.ok = ok;
        this.ko = ko;
        this.actualDuration = actualDuration;
        this.responseTime = responseTime;
    }

    @Override
    public long getOk() {
        return ok;
    }

    @Override
    public long getKo() {
        return ko;
    }

    @Override
    public Duration getActualDuration() {
        return actualDuration;
    }

    @Override
    public DriverResponseTime getResponseTime() {
        return responseTime;
    }
}
