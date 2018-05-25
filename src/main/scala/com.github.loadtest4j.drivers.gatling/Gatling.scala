package com.github.loadtest4j.drivers.gatling

import java.time.Duration
import java.util

import com.github.loadtest4j.loadtest4j.{Driver, DriverRequest, DriverResult}
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.collection.JavaConverters

/**
 * Runs a load test using the 'gatling' library (https://github.com/gatling/gatling).
 */
class Gatling(duration: Duration, url: String, users: Int) extends Driver {

  private val baseConfig = http.baseURL(url)

  override def run(requests: util.List[DriverRequest]): DriverResult = {
    val scn = scenario("My load test")

    val gatlingRequests = toScalaList(requests).map(r => toGatlingRequest(r))

    gatlingRequests.foreach(r => scn.exec(r))

    val simulation: Simulation = new Loadtest4jSimulation

    simulation.setUp(scn.inject(atOnceUsers(users)))
      .maxDuration(toScalaDuration(duration))
      .protocols(baseConfig)

    new DriverResult(0, 0)
  }

  private def toGatlingRequest(request: DriverRequest) = {
    val body = StringBody(request.getBody)
    val headers = toScalaMap(request.getHeaders)
    val method = request.getMethod
    val path = request.getPath

    http("foo")
      .httpRequest(method, path)
      .headers(headers)
      .body(body)
  }

  private def toScalaDuration(d: java.time.Duration) =
    scala.concurrent.duration.Duration.fromNanos(d.toNanos)

  private def toScalaList[T](javaList: java.util.List[T]) = {
    JavaConverters.asScalaBuffer(javaList)
  }

  private def toScalaMap[T](javaMap: java.util.Map[T, T]) = {
    JavaConverters.mapAsScalaMap(javaMap).toMap
  }
}
