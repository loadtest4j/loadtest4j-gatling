package io.gatling.app

import com.github.loadtest4j.loadtest4j.DriverResult
import io.gatling.charts.ReportsGeneratorFacade
import io.gatling.charts.report.ReportsGenerationInputs
import io.gatling.charts.stats.LogFileReader
import io.gatling.commons.stats.assertion.AssertionValidator
import io.gatling.core.config.GatlingConfiguration

class RunResultProcessorFacade(implicit configuration: GatlingConfiguration) {
  def processRunResult(runResult: RunResult): DriverResult = {
    val reader = initLogFileReader(runResult)

    val assertionResults = AssertionValidator.validateAssertions(reader)

    val reportsGenerationInputs = ReportsGenerationInputs(runResult.runId, reader, assertionResults)

    new ReportsGeneratorFacade().generateFor(reportsGenerationInputs)
  }

  private def initLogFileReader(runResult: RunResult) = {
    new LogFileReader(runResult.runId)
  }
}
