package com.github.loadtest4j.drivers.gatling

import com.github.loadtest4j.loadtest4j.DriverRequest
import java.util.Collections

import scala.collection.JavaConverters

object DriverRequests {
    def get(path: String): DriverRequest = {
        new DriverRequest("", Collections.emptyMap(), "GET", path, Collections.emptyMap())
    }

  def getWithQueryParams(path: String, queryParams: Map[String, String]): DriverRequest = {
    val javaQueryParams = JavaConverters.mapAsJavaMap(queryParams)
    new DriverRequest("", Collections.emptyMap(), "GET", path, javaQueryParams)
  }

    def post(path: String, body: String, headers: Map[String, String]): DriverRequest = {
        val javaHeaders = JavaConverters.mapAsJavaMap(headers)
        new DriverRequest(body, javaHeaders, "POST", path, Collections.emptyMap())
    }
}
