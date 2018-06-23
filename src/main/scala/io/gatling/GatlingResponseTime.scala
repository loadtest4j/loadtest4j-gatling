package io.gatling

import java.time.Duration

import com.github.loadtest4j.loadtest4j.ResponseTime

private[gatling] class GatlingResponseTime(percentile: Double => Int) extends ResponseTime {
  override def getPercentile(i: Int): Duration = {
    val millis = percentile(i)
    Duration.ofMillis(millis)
  }
}
