package org.loadtest4j.drivers.gatling

import java.time.Duration
import scala.collection.JavaConverters._

class GatlingResponseDistribution(timesVsCounts: java.util.Map[Int, Int]) {

  def getResponseCountBetween(min: Duration, max: Duration): Int = {
    if (min.compareTo(max) > 0) {
      throw new IllegalArgumentException("Max must be greater than min.")
    }

    val minMillis = min.toMillis
    val maxMillis = max.toMillis

    val count = timesVsCounts.asScala.foldLeft(0) { (accumulator: Int, timeVsCount: (Int, Int)) =>
      val time = timeVsCount._1

      if (time >= minMillis && time <= maxMillis) {
        val count = timeVsCount._2
        accumulator + count
      } else {
        accumulator
      }
    }

    count
  }
}
