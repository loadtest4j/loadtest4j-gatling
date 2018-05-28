package com.github.loadtest4j.drivers.gatling

import com.github.loadtest4j.loadtest4j.DriverRequest
import java.util.Collections

import scala.collection.JavaConverters

object DriverRequests {
    def get(path: String): DriverRequest = {
        new DriverRequest("", Collections.emptyMap(), "GET", path)
    }

    def post(path: String, body: String, headers: Map[String, String]): DriverRequest = {
        val javaHeaders = JavaConverters.mapAsJavaMap(headers)
        new DriverRequest(body, javaHeaders, "POST", path)
    }
}
