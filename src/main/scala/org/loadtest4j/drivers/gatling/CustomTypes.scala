package org.loadtest4j.drivers.gatling

import io.gatling.http.request.builder.HttpRequestBuilder

object CustomTypes {
  type HttpRequestBuilderTransformer = HttpRequestBuilder => HttpRequestBuilder
}
