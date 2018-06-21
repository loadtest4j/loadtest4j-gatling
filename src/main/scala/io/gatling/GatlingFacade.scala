package io.gatling

import akka.actor.ActorSystem
import akka.pattern.ask
import io.gatling.app.{RunResult, SelectedSimulationClass, Selection}
import io.gatling.commons.util.ClockSingleton.nowMillis
import io.gatling.core.CoreComponents
import io.gatling.core.Predef.Simulation
import io.gatling.core.action.Exit
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.controller.throttle.Throttler
import io.gatling.core.controller.{Controller, ControllerCommand}
import io.gatling.core.stats.DataWritersStatsEngine
import io.gatling.core.stats.writer.RunMessage

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Try}

class GatlingFacade(implicit configuration: GatlingConfiguration) {
  def runSimulation(simulation: Simulation): RunResult = {
    // start actor system before creating simulation instance, some components might need it (e.g. shutdown hook)
    val system = ActorSystem("GatlingSystem", GatlingConfiguration.loadActorSystemConfiguration())

    val simClass = Class.forName(simulation.getClass.getName).asInstanceOf[Class[Simulation]]
    val selectedSimulationClass: SelectedSimulationClass = Some(simClass)

    val selection = Selection(selectedSimulationClass, configuration)
    // Derive class from passed-in instance, rather than Gatling's preference for the other way round
    // val simulation = selection.simulationClass.newInstance
    val simulationParams = simulation.params(configuration)
    val runMessage = RunMessage(simulationParams.name, selection.userDefinedSimulationId, selection.defaultSimulationId, nowMillis, selection.description)
    val statsEngine = DataWritersStatsEngine(system, simulationParams, runMessage, configuration)
    val throttler = Throttler(system, simulationParams)
    val controller = system.actorOf(Controller.props(statsEngine, throttler, simulationParams, configuration), Controller.ControllerActorName)
    val exit = new Exit(controller, statsEngine)
    val coreComponents = CoreComponents(controller, throttler, statsEngine, exit, configuration)
    val scenarios = simulationParams.scenarios(system, coreComponents)

    gc()

    val timeout = Int.MaxValue.milliseconds - 10.seconds
    val whenRunDone = coreComponents.controller.ask(ControllerCommand.Start(scenarios))(timeout).mapTo[Try[String]]
    val runResult = Await.result(whenRunDone, timeout) match {
      case Failure(t) => throw t
      case _ =>
        simulation.executeAfter()
        RunResult(runMessage.runId, simulationParams.assertions.nonEmpty)
    }
    val whenTerminated = system.terminate()
    Await.result(whenTerminated, 2.seconds)

    runResult
  }
  
  private def gc() = {
    // Suppress Spotbugs warning about GC call...
    // because this is benchmarking code, and we really do want it.
    System.gc()
  }
}
