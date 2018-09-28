package io.gatling

import java.time.Duration

import io.gatling.app.RunResult
import io.gatling.charts.report.{ReportsGenerationInputs, ReportsGenerator}
import io.gatling.charts.stats.LogFileReader
import io.gatling.commons.stats.assertion.AssertionValidator
import io.gatling.commons.stats.{KO, OK}
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.config.GatlingFiles.simulationLogDirectory
import org.loadtest4j.driver.DriverResult
import org.loadtest4j.drivers.gatling.{GatlingResponseTime, GatlingResult}

private[gatling] class RunResultProcessorFacade(implicit configuration: GatlingConfiguration) {
  def processRunResult(runResult: RunResult): DriverResult = {
    val logFileReader = new LogFileReader(runResult.runId)

    generateReport(logFileReader, runResult)

    // We don't use these result filters in loadtest4j
    val requestName = None
    val group = None

    // Request count
    val total = logFileReader.requestGeneralStats(requestName, group, None)
    val ok = logFileReader.requestGeneralStats(requestName, group, Some(OK))
    val ko = logFileReader.requestGeneralStats(requestName, group, Some(KO))

    // Response time
    val responseTime = new GatlingResponseTime(total.percentile)

    // Gatling can also supply these statistics:
    //
    // val meanResponseTimeStatistics = Statistics(total.mean, ok.mean, ko.mean)
    // val stdDeviationStatistics = Statistics(total.stdDev, ok.stdDev, ko.stdDev)
    // val meanNumberOfRequestsPerSecondStatistics = Statistics(total.meanRequestsPerSec, ok.meanRequestsPerSec, ko.meanRequestsPerSec)
    //
    // val groupedCounts = logFileReader
    //  .numberOfRequestInResponseTimeRange(requestName, group).map {
    //  case (rangeName, count) => GroupedCount(rangeName, count, total.count)
    //}

    val actualDuration = Duration.ofMillis(logFileReader.runEnd - logFileReader.runStart)
    val okRequests = ok.count
    val koRequests = ko.count

    new GatlingResult(okRequests, koRequests, actualDuration, responseTime)
  }

  private def generateReport(logFileReader: LogFileReader, runResult: RunResult) = {
    if (reportsGenerationEnabled) {
      generateHtmlReport(logFileReader, runResult)
    } else {
      simulationLogDirectory(runResult.runId, create = false)
    }
  }

  private def generateHtmlReport(logFileReader: LogFileReader, runResult: RunResult) = {
    val assertionResults = AssertionValidator.validateAssertions(logFileReader)
    val reportsGenerationInputs = ReportsGenerationInputs(runResult.runId, logFileReader, assertionResults)
    new ReportsGenerator().generateFor(reportsGenerationInputs)
  }

  private def reportsGenerationEnabled =
    configuration.core.directory.reportsOnly.isDefined || (configuration.data.fileDataWriterEnabled && !configuration.charting.noReports)
}
