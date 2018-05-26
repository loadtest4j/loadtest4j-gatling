package io.gatling.app

import java.util

import akka.actor.ActorSystem
import akka.pattern.ask
import com.github.loadtest4j.loadtest4j.{DriverRequest, DriverResult}
import io.gatling.core.CoreComponents
import io.gatling.core.Predef._
import io.gatling.core.action.Exit
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.controller.{Controller, ControllerCommand}
import io.gatling.core.controller.throttle.Throttler
import io.gatling.core.stats.DataWritersStatsEngine
import io.gatling.core.stats.writer.RunMessage
import io.gatling.http.Predef._
import io.gatling.commons.util.ClockSingleton.nowMillis

import scala.collection.JavaConverters
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.Try

/**
  * A private Java-Scala bridge class needed because Gatling does not currently expose a Java API.
  *
  * It is placed in the same package as the Gatling Runner class to gain access to private Gatling APIs.
  *
  * Do not test this class directly in JUnit. Do not expose it publicly. Instead use its Java wrapper {@see Gatling}.
  */
class GatlingScalaBridge(durationInSeconds: Long, url: String, users: Int) {

  // We are not in the conventional Gatling test runner, so explicitly load the Gatling config
  implicit val configuration: GatlingConfiguration = GatlingConfiguration.load()

  private val baseConfig = http.baseURL(url)

  def run(requests: util.List[DriverRequest]): DriverResult = {
    var scn = scenario("My load test")
    val gatlingRequests = scalaSeq(requests).map(r => toGatlingRequest(r))
    for (r <- gatlingRequests) {
      scn = scn.exec(r)
    }

    val simulation = new Loadtest4jSimulation

    simulation.setUp(scn.inject(atOnceUsers(users)))
      .maxDuration(durationInSeconds.seconds)
      .protocols(baseConfig)

    runSimulation(simulation)

    new DriverResult(0, 0)
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
    // start actor system before creating simulation instance, some components might need it (e.g. shutdown hook)
    val system = ActorSystem("GatlingSystem", GatlingConfiguration.loadActorSystemConfiguration())

    val simClass: Class[Simulation] = Class.forName(simulation.getClass.getName).asInstanceOf[Class[Simulation]]
    val selectedSimulationClass: SelectedSimulationClass = Some(simClass)

    val selection = Selection(selectedSimulationClass, configuration)
    // FIXME got rid of this because it breaks:
    // val simulation = selection.simulationClass.newInstance
    val simulationParams = simulation.params(configuration)
    val runMessage = RunMessage(simulationParams.name, selection.userDefinedSimulationId, selection.defaultSimulationId, nowMillis, selection.description)
    val statsEngine = DataWritersStatsEngine(system, simulationParams, runMessage, configuration)
    val throttler = Throttler(system, simulationParams)
    val controller = system.actorOf(Controller.props(statsEngine, throttler, simulationParams, configuration), Controller.ControllerActorName)
    val exit = new Exit(controller, statsEngine)
    val coreComponents = CoreComponents(controller, throttler, statsEngine, exit, configuration)
    val scenarios = simulationParams.scenarios(system, coreComponents)

    val timeout = Int.MaxValue.milliseconds - 10.seconds
    val whenRunDone: Future[Try[String]] = coreComponents.controller.ask(ControllerCommand.Start(scenarios))(timeout).mapTo[Try[String]]
    Await.result(whenRunDone, timeout)
  }

  private def scalaSeq[T](javaList: util.List[T]): Seq[T] = {
    JavaConverters.asScalaBuffer(javaList)
  }

  private def scalaMap[K, V](javaMap: util.Map[K, V]) = {
    JavaConverters.mapAsScalaMap(javaMap).toMap
  }
}
