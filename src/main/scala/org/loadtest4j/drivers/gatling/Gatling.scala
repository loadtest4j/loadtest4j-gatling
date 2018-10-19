package org.loadtest4j.drivers.gatling

import java.nio.file.Path
import java.util

import io.gatling.GatlingFacade
import io.gatling.core.Predef._
import io.gatling.core.body.{RawFileBody => _, _}
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.stats.writer.FileDataWriterType
import io.gatling.http.Predef._
import org.loadtest4j.LoadTesterException
import org.loadtest4j.driver.{Driver, DriverRequest, DriverResult}

import scala.collection.JavaConverters
import scala.concurrent.duration._

private class Gatling(duration: FiniteDuration, url: String, usersPerSecond: Int) extends Driver {

  // We are not in the conventional Gatling test runner, so explicitly load the Gatling config
  implicit val configuration: GatlingConfiguration = loadGatlingConfiguration()

  private def loadGatlingConfiguration() = {
    val original = GatlingConfiguration.load()

    val dataWithoutConsoleAppender = original.data.copy(dataWriters = Seq(FileDataWriterType))

    original.copy(data = dataWithoutConsoleAppender)
  }

  private val baseConfig = http.baseURL(url)

  override def run(requests: util.List[DriverRequest]): DriverResult = {
    validateNotEmpty(requests)

    val scn = createScenario(requests)

    val simulation = new Loadtest4jSimulation

    simulation.setUp(scn.inject(constantUsersPerSec(usersPerSecond).during(duration)))
      .maxDuration(duration)
      .protocols(baseConfig)

    runSimulation(simulation)
  }

  private def createScenario(requests: util.List[DriverRequest]) = {
    val gatlingRequests = scalaSeq(requests).map(r => toGatlingRequest(r))
    val blankScenario = scenario("loadtest4j load test")
    gatlingRequests.foldLeft(blankScenario)((scn, r) => scn.exec(r))
  }

  private def toGatlingRequest(request: DriverRequest) = {
    val body = gatlingBody(request.getBody)
    val headers = scalaMap(request.getHeaders)
    val method = request.getMethod
    val path = request.getPath
    val queryParams = scalaMap(request.getQueryParams)

    http("loadtest4j request")
      .httpRequest(method, path)
      .headers(headers)
      .body(body)
      .queryParamMap(queryParams)
  }

  private def gatlingBody(body: org.loadtest4j.Body): Body = {
    body.accept(new GatlingBodyVisitor)
  }

  private def runSimulation(simulation: Simulation) = {
    new GatlingFacade().start(simulation)
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

  private class GatlingBodyVisitor extends org.loadtest4j.Body.Visitor[Body] {
    override def string(str: String): Body = CompositeByteArrayBody(str)

    override def file(path: Path): Body = {
      val theFile = path.toAbsolutePath.toString

      // TODO remove this
      // backup exploration plan if this doesn't work
      // val fileWithCachedBytes = FileWithCachedBytes(theFile, None)
      // new RawFileBody(fileWithCachedBytes)

      // FIXME RawFileBody does not actually do a "file upload"...
      // ...what it does is to let a user store a standard HTTP POST body (e.g. JSON) in a file rather than in a string
      // and then it literally dumps the file contents into the HTTP request and sends it.
      // It does not wrap it in "multipart form" or "-----Webkit form boundary-----"
      val rawFileBodies = new RawFileBodies()(configuration)
      RawFileBody(theFile)(configuration, rawFileBodies)
    }
  }
}