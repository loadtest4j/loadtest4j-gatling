package org.loadtest4j.drivers.gatling

import java.time.Duration

import com.github.loadtest4j.loadtest4j.driver.DriverResponseTime

class GatlingResponseTime(percentile: Double => Int) extends DriverResponseTime {
  override def getPercentile(i: Int): Duration = {
    val millis = percentile(i)
    Duration.ofMillis(millis)
  }
}
