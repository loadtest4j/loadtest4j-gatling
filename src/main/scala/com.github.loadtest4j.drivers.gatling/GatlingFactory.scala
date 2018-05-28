package com.github.loadtest4j.drivers.gatling

import com.github.loadtest4j.loadtest4j.{Driver, DriverFactory}
import java.util

import scala.collection.JavaConverters
import scala.concurrent.duration._

class GatlingFactory extends DriverFactory {

  override def getMandatoryProperties: util.Set[String] = {
    JavaConverters.setAsJavaSet(Set("duration", "url"))
  }

  /**
    * Creates a Gatling driver using the following properties.
    *
    * Mandatory properties:
    *
    * - `duration`
    * - `url`
    *
    * Optional properties:
    *
    * - `usersPerSecond` (defaults to 1)
    */
  override def create(properties: util.Map[String, String]): Driver = {
    val duration = properties.get("duration").toLong.seconds
    val url = properties.get("url")
    val usersPerSecond = properties.getOrDefault("usersPerSecond", "1").toInt

    new Gatling(duration, url, usersPerSecond)
  }

}
