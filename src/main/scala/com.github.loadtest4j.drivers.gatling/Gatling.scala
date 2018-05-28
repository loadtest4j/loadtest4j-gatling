package com.github.loadtest4j.drivers.gatling {
  import java.util

  import com.github.loadtest4j.loadtest4j.{Driver, DriverRequest, DriverResult, LoadTesterException}
  import io.gatling.app.{GatlingFacade, RunResultProcessorFacade}
  import io.gatling.core.Predef._
  import io.gatling.core.config.GatlingConfiguration
  import io.gatling.http.Predef._

  import scala.collection.JavaConverters
  import scala.concurrent.duration._

  class Gatling(durationInSeconds: Long, url: String, users: Int) extends Driver {

    // We are not in the conventional Gatling test runner, so explicitly load the Gatling config
    implicit val configuration: GatlingConfiguration = GatlingConfiguration.load()

    private val baseConfig = http.baseURL(url)

    override def run(requests: util.List[DriverRequest]): DriverResult = {
      validateNotEmpty(requests)

      val scn = createScenario(requests)

      val simulation = new Loadtest4jSimulation

      simulation.setUp(scn.inject(atOnceUsers(users)))
        .maxDuration(durationInSeconds.seconds)
        .protocols(baseConfig)

      runSimulation(simulation)
    }

    private def createScenario(requests: util.List[DriverRequest]) = {
      val gatlingRequests = scalaSeq(requests).map(r => toGatlingRequest(r))
      val blankScenario = scenario("My load test")
      gatlingRequests.foldLeft(blankScenario)((scn, r) => scn.exec(r))
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

    private def validateNotEmpty[T](requests: util.Collection[T]): Unit = {
      if (requests.size < 1) throw new LoadTesterException("No requests were specified for the load test.")
    }
  }
}