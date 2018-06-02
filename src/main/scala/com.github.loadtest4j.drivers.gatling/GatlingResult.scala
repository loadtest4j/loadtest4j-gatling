package com.github.loadtest4j.drivers.gatling

import java.time.Duration
import java.util.Optional

import com.github.loadtest4j.loadtest4j.DriverResult

class GatlingResult(ok: Long, ko: Long, actualDuration: Duration, reportUrl: String) extends DriverResult {
  override def getKo: Long = ko

  override def getOk: Long = ok

  override def getReportUrl: Optional[String] = Optional.of(reportUrl)

  override def getActualDuration: Duration = actualDuration
}
