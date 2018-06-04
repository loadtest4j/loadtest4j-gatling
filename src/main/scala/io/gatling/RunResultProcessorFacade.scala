package io.gatling

import java.time.Duration

import com.github.loadtest4j.drivers.gatling.GatlingResult
import com.github.loadtest4j.loadtest4j.DriverResult
import io.gatling.app.RunResult
import io.gatling.charts.report.{ReportsGenerationInputs, ReportsGenerator}
import io.gatling.charts.stats.LogFileReader
import io.gatling.commons.stats.assertion.AssertionValidator
import io.gatling.commons.stats.{GeneralStats, KO, OK}
import io.gatling.core.config.GatlingConfiguration

class RunResultProcessorFacade(implicit configuration: GatlingConfiguration) {
  def processRunResult(runResult: RunResult): DriverResult = {
    val logFileReader = new LogFileReader(runResult.runId)

    val reportIndexPath = generateCustomReport(logFileReader, runResult)

    // We don't use these result filters in loadtest4j
    val requestName = None
    val group = None

    val total = logFileReader.requestGeneralStats(requestName, group, None)
    val ok = logFileReader.requestGeneralStats(requestName, group, Some(OK))
    val ko = logFileReader.requestGeneralStats(requestName, group, Some(KO))

    val numberOfRequestsStatistics = Statistics(total.count, ok.count, ko.count)
    val minResponseTimeStatistics = Statistics(total.min, ok.min, ko.min)
    val maxResponseTimeStatistics = Statistics(total.max, ok.max, ko.max)
    val meanResponseTimeStatistics = Statistics(total.mean, ok.mean, ko.mean)
    val stdDeviationStatistics = Statistics(total.stdDev, ok.stdDev, ko.stdDev)

    val responseTimePercentile1 = percentiles(configuration.charting.indicators.percentile1, total, ok, ko)
    val responseTimePercentile2 = percentiles(configuration.charting.indicators.percentile2, total, ok, ko)
    val responseTimePercentile3 = percentiles(configuration.charting.indicators.percentile3, total, ok, ko)
    val responseTimePercentile4 = percentiles(configuration.charting.indicators.percentile4, total, ok, ko)

    val meanNumberOfRequestsPerSecondStatistics = Statistics(total.meanRequestsPerSec, ok.meanRequestsPerSec, ko.meanRequestsPerSec)

    val groupedCounts = logFileReader
      .numberOfRequestInResponseTimeRange(requestName, group).map {
      case (rangeName, count) => GroupedCount(rangeName, count, total.count)
    }

    val actualDuration = Duration.ofMillis(logFileReader.runEnd - logFileReader.runStart)
    val reportUrl = reportIndexPath.toUri.toString
    val okRequests = numberOfRequestsStatistics.success
    val koRequests = numberOfRequestsStatistics.failure

    new GatlingResult(okRequests, koRequests, actualDuration, reportUrl)
  }

  private def generateCustomReport(logFileReader: LogFileReader, runResult: RunResult) = {
    val assertionResults = AssertionValidator.validateAssertions(logFileReader)
    val reportsGenerationInputs = ReportsGenerationInputs(runResult.runId, logFileReader, assertionResults)
    new ReportsGenerator().generateFor(reportsGenerationInputs)
  }

  private case class Statistics[T: Numeric](total: T, success: T, failure: T) {
    def all = List(total, success, failure)
  }

  private def percentiles(rank: Double, total: GeneralStats, ok: GeneralStats, ko: GeneralStats) =
    Statistics(total.percentile(rank), ok.percentile(rank), ko.percentile(rank))

  private case class GroupedCount(name: String, count: Long, total: Long) {
    val percentage: Int = if (total == 0) 0 else (count.toDouble / total * 100).round.toInt
  }
}
