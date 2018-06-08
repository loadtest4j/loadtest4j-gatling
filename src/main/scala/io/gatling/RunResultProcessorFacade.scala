package io.gatling

import java.time.Duration

import com.github.loadtest4j.drivers.gatling.GatlingResult
import com.github.loadtest4j.loadtest4j.DriverResult
import io.gatling.app.RunResult
import io.gatling.charts.report.{ReportsGenerationInputs, ReportsGenerator}
import io.gatling.charts.stats.LogFileReader
import io.gatling.commons.stats.assertion.AssertionValidator
import io.gatling.commons.stats.{KO, OK}
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.config.GatlingFiles.simulationLogDirectory

class RunResultProcessorFacade(implicit configuration: GatlingConfiguration) {
  def processRunResult(runResult: RunResult): DriverResult = {
    val logFileReader = new LogFileReader(runResult.runId)

    val reportPath = generateReport(logFileReader, runResult)

    // We don't use these result filters in loadtest4j
    val requestName = None
    val group = None

    val total = logFileReader.requestGeneralStats(requestName, group, None)
    val ok = logFileReader.requestGeneralStats(requestName, group, Some(OK))
    val ko = logFileReader.requestGeneralStats(requestName, group, Some(KO))

    // Gatling can also supply these statistics:
    //
    // val minResponseTimeStatistics = Statistics(total.min, ok.min, ko.min)
    // val maxResponseTimeStatistics = Statistics(total.max, ok.max, ko.max)
    // val meanResponseTimeStatistics = Statistics(total.mean, ok.mean, ko.mean)
    // val stdDeviationStatistics = Statistics(total.stdDev, ok.stdDev, ko.stdDev)
    // val meanNumberOfRequestsPerSecondStatistics = Statistics(total.meanRequestsPerSec, ok.meanRequestsPerSec, ko.meanRequestsPerSec)
    //
    // val responseTimePercentile1 = percentiles(configuration.charting.indicators.percentile1, total, ok, ko)
    // val responseTimePercentile2 = percentiles(configuration.charting.indicators.percentile2, total, ok, ko)
    // val responseTimePercentile3 = percentiles(configuration.charting.indicators.percentile3, total, ok, ko)
    // val responseTimePercentile4 = percentiles(configuration.charting.indicators.percentile4, total, ok, ko)
    //
    // val groupedCounts = logFileReader
    //  .numberOfRequestInResponseTimeRange(requestName, group).map {
    //  case (rangeName, count) => GroupedCount(rangeName, count, total.count)
    //}

    val actualDuration = Duration.ofMillis(logFileReader.runEnd - logFileReader.runStart)
    val reportUrl = reportPath.toUri.toString
    val okRequests = ok.count
    val koRequests = ko.count

    new GatlingResult(okRequests, koRequests, actualDuration, reportUrl)
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
