package io.gatling.charts

import com.github.loadtest4j.loadtest4j.DriverResult
import io.gatling.charts.component.{GroupedCount, Statistics}
import io.gatling.charts.report.ReportsGenerationInputs
import io.gatling.commons.stats.{GeneralStats, KO, OK}
import io.gatling.commons.util.NumberHelper._
import io.gatling.core.config.GatlingConfiguration

class ReportsGeneratorFacade(implicit configuration: GatlingConfiguration) {
  def generateFor(reportsGenerationInputs: ReportsGenerationInputs): DriverResult = {
    import reportsGenerationInputs._

    // We don't use these result filters in loadtest4j
    val name = "Global Information"
    val requestName = None
    val group = None

    val total = logFileReader.requestGeneralStats(requestName, group, None)
    val ok = logFileReader.requestGeneralStats(requestName, group, Some(OK))
    val ko = logFileReader.requestGeneralStats(requestName, group, Some(KO))

    val numberOfRequestsStatistics = Statistics("request count", total.count, ok.count, ko.count)
    val minResponseTimeStatistics = Statistics("min response time", total.min, ok.min, ko.min)
    val maxResponseTimeStatistics = Statistics("max response time", total.max, ok.max, ko.max)
    val meanResponseTimeStatistics = Statistics("mean response time", total.mean, ok.mean, ko.mean)
    val stdDeviationStatistics = Statistics("std deviation", total.stdDev, ok.stdDev, ko.stdDev)

    val percentilesTitle = (rank: Double) => s"response time ${rank.toRank} percentile"

    val percentiles1 = percentiles(configuration.charting.indicators.percentile1, percentilesTitle, total, ok, ko)
    val percentiles2 = percentiles(configuration.charting.indicators.percentile2, percentilesTitle, total, ok, ko)
    val percentiles3 = percentiles(configuration.charting.indicators.percentile3, percentilesTitle, total, ok, ko)
    val percentiles4 = percentiles(configuration.charting.indicators.percentile4, percentilesTitle, total, ok, ko)
    val meanNumberOfRequestsPerSecondStatistics = Statistics("mean requests/sec", total.meanRequestsPerSec, ok.meanRequestsPerSec, ko.meanRequestsPerSec)

    val groupedCounts = logFileReader
      .numberOfRequestInResponseTimeRange(requestName, group).map {
      case (rangeName, count) => GroupedCount(rangeName, count, total.count)
    }

    toDriverResult(numberOfRequestsStatistics)
  }

  private def percentiles(rank: Double, title: Double => String, total: GeneralStats, ok: GeneralStats, ko: GeneralStats) =
    Statistics(title(rank), total.percentile(rank), ok.percentile(rank), ko.percentile(rank))

  private def toDriverResult(numberOfRequestsStatistics: Statistics[Long]) = {
    val errors = numberOfRequestsStatistics.failure
    val requests = numberOfRequestsStatistics.total
    new DriverResult(errors, requests)
  }
}
