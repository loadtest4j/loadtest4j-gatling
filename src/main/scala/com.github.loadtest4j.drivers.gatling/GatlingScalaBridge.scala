package com.github.loadtest4j.drivers.gatling

import java.util

import com.github.loadtest4j.loadtest4j.{DriverRequest, DriverResult}
import io.gatling.app.{GatlingFacade, RunResultProcessorFacade}
import io.gatling.core.Predef._
import io.gatling.core.config.GatlingConfiguration
import io.gatling.http.Predef._

import scala.collection.JavaConverters
import scala.concurrent.duration._

/**
  * A private Java-Scala bridge class needed because Gatling does not currently expose a Java API.
  *
  * Do not test this class directly in JUnit. Do not expose it publicly. Instead use its Java wrapper {@see Gatling}.
  */
class GatlingScalaBridge(durationInSeconds: Long, url: String, users: Int) {

  // We are not in the conventional Gatling test runner, so explicitly load the Gatling config
  implicit val configuration: GatlingConfiguration = GatlingConfiguration.load()

  private val baseConfig = http.baseURL(url)

  def run(requests: util.List[DriverRequest]): DriverResult = {
    val scn = createScenario(requests)

    val simulation = new Loadtest4jSimulation

    simulation.setUp(scn.inject(atOnceUsers(users)))
      .maxDuration(durationInSeconds.seconds)
      .protocols(baseConfig)

    // FIXME redirect the results directory (if possible)
    runSimulation(simulation)
  }

  private def createScenario(requests: util.List[DriverRequest]) = {
    var scn = scenario("My load test")
    val gatlingRequests = scalaSeq(requests).map(r => toGatlingRequest(r))
    for (r <- gatlingRequests) {
      scn = scn.exec(r)
    }
    scn
  }

  private def toGatlingRequest(request: DriverRequest) = {
    val headers = scalaMap(request.getHeaders)
    val method = request.getMethod
    val path = request.getPath

    val httpRequestBuilder = http("foo")
      .httpRequest(method, path)
      .headers(headers)

    val requestBody = request.getBody
    if (requestBody.isEmpty) {
      httpRequestBuilder
    } else {
      httpRequestBuilder.body(StringBody(requestBody))
    }
  }

  private def runSimulation(simulation: Simulation) = {
    val runResult = new GatlingFacade().runSimulation(simulation)
    new RunResultProcessorFacade().processRunResult(runResult)
  }

  private def scalaSeq[T](javaList: util.List[T]): Seq[T] = {
    JavaConverters.asScalaBuffer(javaList)
  }

  private def scalaMap[K, V](javaMap: util.Map[K, V]) = {
    JavaConverters.mapAsScalaMap(javaMap).toMap
  }
}
