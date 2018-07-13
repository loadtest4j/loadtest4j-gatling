package com.github.loadtest4j.drivers.gatling

import com.github.loadtest4j.loadtest4j.driver.{Driver, DriverFactory}
import java.util

import scala.collection.JavaConverters
import scala.concurrent.duration._

class GatlingFactory extends DriverFactory {

  override def getMandatoryProperties: util.Set[String] = {
    JavaConverters.setAsJavaSet(Set("duration", "url", "usersPerSecond"))
  }

  /**
    * Creates a Gatling driver using the following properties.
    *
    * Mandatory properties:
    *
    * - `duration`
    * - `url`
    * - `usersPerSecond`
    *
    */
  override def create(properties: util.Map[String, String]): Driver = {
    val duration = properties.get("duration").toLong.seconds
    val url = properties.get("url")
    val usersPerSecond = properties.get("usersPerSecond").toInt

    new Gatling(duration, url, usersPerSecond)
  }

}
