package org.loadtest4j.drivers.gatling

import java.time.Duration

import org.loadtest4j.driver.DriverResponseTime

class GatlingResponseTime(percentile: Double => Int) extends DriverResponseTime {
  override def getPercentile(i: Double): Duration = {
    val millis = percentile(i)
    Duration.ofMillis(millis)
  }
}
