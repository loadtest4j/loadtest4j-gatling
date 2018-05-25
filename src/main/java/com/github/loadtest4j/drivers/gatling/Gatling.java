package com.github.loadtest4j.drivers.gatling;

import com.github.loadtest4j.loadtest4j.DriverRequest;
import com.github.loadtest4j.loadtest4j.Driver;
import com.github.loadtest4j.loadtest4j.LoadTesterException;
import com.github.loadtest4j.loadtest4j.DriverResult;

import static io.gatling.core.Predef.*;
import static io.gatling.http.Predef.*;

import scala.Predef;
import scala.Tuple2;
import scala.collection.JavaConversions;
import scala.collection.JavaConverters;
import scala.collection.JavaConverters.*;
import io.gatling.commons.validation.Validation;
import io.gatling.core.body.Body;
import io.gatling.core.body.StringBody;
import io.gatling.core.session.Session;
import io.gatling.http.request.builder.Http;
import io.gatling.core.config.GatlingConfiguration;
import io.gatling.core.structure.ScenarioBuilder;
import io.gatling.http.request.builder.HttpRequestBuilder;
import org.asynchttpclient.uri.Uri;
import scala.Function1;


import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Runs a load test using the 'gatling' library (https://github.com/gatling/gatling).
 */
class Gatling implements Driver {

    private final String url;

    Gatling(String url) {
        this.url = url;
    }

    @Override
    public DriverResult run(List<DriverRequest> requests) {
        ScenarioBuilder scenario = scenario("My load test");

        for (DriverRequest request: requests) {
            Http gatlingRequest = toGatlingRequest(request);
            scenario = scenario.exec(gatlingRequest);
        }

        return new DriverResult(0, 0);
    }

    private static Http toGatlingRequest(DriverRequest request) {
        final Uri path = toUri(request.getPath());
        final Body body = toBody(request.getBody());
        final scala.collection.immutable.Map<String, String> headers = toScalaMap(request.getHeaders());

        Function1<Session, Validation<String>> myGatlingRequest = v1 -> null;
        HttpRequestBuilder httpBuilder = new Http(myGatlingRequest)
                .get(path)
                .headers(headers)
                .body(body);

        return httpBuilder.build();
    }

    private static Uri toUri(String str) {
        return Uri.create(str);
    }

    private static Body toBody(String body) {
        return new StringBody(body);
    }

    private static scala.collection.immutable.Map<String, String> toScalaMap(Map<String, String> javaMap) {
        return JavaConverters.mapAsScalaMap(javaMap).toMap(Predef.conforms());
    }
}
