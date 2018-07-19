package org.loadtest4j.drivers.gatling

import java.time.Duration
import java.util.Optional

import org.loadtest4j.driver.DriverResponseTime
import org.loadtest4j.driver.DriverResult

class GatlingResult(ok: Long, ko: Long, actualDuration: Duration, responseTime: DriverResponseTime, reportUrl: String) extends DriverResult {
  override def getKo: Long = ko

  override def getOk: Long = ok

  override def getReportUrl: Optional[String] = Optional.of(reportUrl)

  override def getActualDuration: Duration = actualDuration

  override def getResponseTime: DriverResponseTime = responseTime
}
