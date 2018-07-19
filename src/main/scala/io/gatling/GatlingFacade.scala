package io.gatling

import io.gatling.core.Predef.Simulation
import io.gatling.core.config.GatlingConfiguration
import org.loadtest4j.driver.DriverResult

class GatlingFacade(implicit configuration: GatlingConfiguration) {
  def start(simulation: Simulation): DriverResult = {
    val runResult = new RunnerFacade().runSimulation(simulation)
    new RunResultProcessorFacade().processRunResult(runResult)
  }
}
