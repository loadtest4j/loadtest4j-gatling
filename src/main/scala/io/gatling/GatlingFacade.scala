package io.gatling

import com.github.loadtest4j.loadtest4j.driver.DriverResult
import io.gatling.core.Predef.Simulation
import io.gatling.core.config.GatlingConfiguration

class GatlingFacade(implicit configuration: GatlingConfiguration) {
  def start(simulation: Simulation): DriverResult = {
    val runResult = new RunnerFacade().runSimulation(simulation)
    new RunResultProcessorFacade().processRunResult(runResult)
  }
}
