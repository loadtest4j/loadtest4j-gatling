package com.github.loadtest4j.drivers.gatling

import com.github.loadtest4j.loadtest4j.DriverRequest
import java.util.Collections

import scala.collection.JavaConverters

object DriverRequests {
    def get(path: String): DriverRequest = {
        new DriverRequest("", Collections.emptyMap(), "GET", path)
    }

    def getWithBody(path: String, body: String): DriverRequest = {
        new DriverRequest(body, Collections.emptyMap(), "GET", path)
    }

    def getWithHeaders(path: String, headers: Map[String, String]): DriverRequest = {
        val javaHeaders = JavaConverters.mapAsJavaMap(headers)
        new DriverRequest("", javaHeaders, "GET", path)
    }

    def getWithBodyAndHeaders(path: String, body: String, headers: Map[String, String]): DriverRequest = {
        val javaHeaders = JavaConverters.mapAsJavaMap(headers)
        new DriverRequest(body, javaHeaders, "GET", path)
    }
}
