package com.github.loadtest4j.drivers.gatling

import com.github.loadtest4j.loadtest4j.Driver
import com.github.loadtest4j.loadtest4j.DriverFactory
import java.util

import scala.collection.JavaConverters

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
     * - `users` (defaults to 1)
     */
    override def create(properties: util.Map[String, String]): Driver = {
        val durationInSeconds = properties.get("duration").toLong
        val url = properties.get("url")
        val users = properties.getOrDefault("users", "1").toInt

        new Gatling(durationInSeconds, url, users)
    }

}
