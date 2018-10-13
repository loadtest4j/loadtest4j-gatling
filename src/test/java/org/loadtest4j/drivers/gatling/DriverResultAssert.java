package org.loadtest4j.drivers.gatling;

import org.assertj.core.api.AbstractAssert;
import org.loadtest4j.driver.DriverResult;

import java.time.Duration;

class DriverResultAssert extends AbstractAssert<DriverResultAssert, DriverResult> {

    private DriverResultAssert(DriverResult driverResult) {
        super(driverResult, DriverResultAssert.class);
    }

    protected static DriverResultAssert assertThat(DriverResult actual) {
        return new DriverResultAssert(actual);
    }

    DriverResultAssert hasOkGreaterThan(long ok) {
        isNotNull();

        if (actual.getOk() < ok) {
            failWithMessage("Expected # OK requests to be greater than <%d> but it was not", ok);
        }

        return this;
    }

    DriverResultAssert hasKo(long ko) {
        isNotNull();

        if (ko != actual.getKo()) {
            failWithMessage("Expected # KO requests to be <%d> but was <%d>", ko, actual.getKo());
        }

        return this;
    }

    DriverResultAssert hasActualDurationGreaterThan(Duration actualDuration) {
        isNotNull();

        if (actual.getActualDuration().compareTo(actualDuration) < 1) {
            failWithMessage("Expected actual duration to be greater than <%s> but was <%s>", actualDuration, actual.getActualDuration());
        }

        return this;
    }

    DriverResultAssert hasMaxResponseTimeGreaterThan(Duration responseTime) {
        isNotNull();

        if (actual.getResponseTime().getPercentile(100).compareTo(responseTime) < 1) {
            failWithMessage("Expected max response time to be greater than <%s> but was <%s>", responseTime, actual.getResponseTime().getPercentile(100));
        }

        return this;
    }

    DriverResultAssert hasSamplesInResponseTimeWindow(Duration min, Duration max) {
        isNotNull();

        // FIXME remove this hack once core API has the right methods
        final GatlingResult result = (GatlingResult) actual;


        if (result.getDistribution().getResponseCountBetween(min, max) < 1) {
            failWithMessage("Expected there to be at least 1 sample in the response time window %s-%s", min, max);
        }

        return this;
    }

}