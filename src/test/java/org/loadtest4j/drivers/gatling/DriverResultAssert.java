package org.loadtest4j.drivers.gatling;

import com.github.loadtest4j.loadtest4j.driver.DriverResult;
import org.assertj.core.api.AbstractAssert;

import java.time.Duration;

public class DriverResultAssert extends AbstractAssert<DriverResultAssert, DriverResult> {

    private DriverResultAssert(DriverResult driverResult) {
        super(driverResult, DriverResultAssert.class);
    }

    public static DriverResultAssert assertThat(DriverResult actual) {
        return new DriverResultAssert(actual);
    }

    public DriverResultAssert hasOkGreaterThan(long ok) {
        isNotNull();

        if (actual.getOk() < ok) {
            failWithMessage("Expected # OK requests to be greater than <%d> but it was not", ok);
        }

        return this;
    }

    public DriverResultAssert hasKo(long ko) {
        isNotNull();

        if (ko != actual.getKo()) {
            failWithMessage("Expected # KO requests to be <%d> but was <%d>", ko, actual.getKo());
        }

        return this;
    }

    public DriverResultAssert hasActualDurationGreaterThan(Duration actualDuration) {
        isNotNull();

        if (actual.getActualDuration().compareTo(actualDuration) < 1) {
            failWithMessage("Expected actual duration to be greater than <%s> but was <%s>", actualDuration, actual.getActualDuration());
        }

        return this;
    }

    public DriverResultAssert hasMaxResponseTimeGreaterThan(Duration responseTime) {
        isNotNull();

        if (actual.getResponseTime().getPercentile(100).compareTo(responseTime) < 1) {
            failWithMessage("Expected max response time to be greater than <%s> but was <%s>", responseTime, actual.getResponseTime().getPercentile(100));
        }

        return this;
    }

    public DriverResultAssert hasReportUrlWithScheme(String scheme) {
        isNotNull();

        if (!actual.getReportUrl().isPresent()) {
            failWithMessage("Expected report URL to be present but was absent");
        }

        if (!actual.getReportUrl().get().startsWith(scheme)) {
            failWithMessage("Expected report URL scheme to be <%s> but it was not", scheme);
        }

        return this;
    }
}