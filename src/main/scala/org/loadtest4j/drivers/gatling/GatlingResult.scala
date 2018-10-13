package org.loadtest4j.drivers.gatling

import java.time.Duration

import org.loadtest4j.driver.DriverResponseTime
import org.loadtest4j.driver.DriverResult

class GatlingResult(distribution: GatlingResponseDistribution, ok: Long, ko: Long, actualDuration: Duration, responseTime: DriverResponseTime) extends DriverResult {
  def getDistribution: GatlingResponseDistribution = distribution

  override def getKo: Long = ko

  override def getOk: Long = ok

  override def getActualDuration: Duration = actualDuration

  override def getResponseTime: DriverResponseTime = responseTime
}
