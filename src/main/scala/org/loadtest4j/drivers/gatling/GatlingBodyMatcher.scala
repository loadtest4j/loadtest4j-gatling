package org.loadtest4j.drivers.gatling

import java.util

import io.gatling.core.body.{CompositeByteArrayBody, RawFileBody => _}
import io.gatling.core.config.GatlingConfiguration
import io.gatling.http.request.BodyPart
import io.gatling.http.request.builder.HttpRequestBuilder
import org.loadtest4j
import org.loadtest4j.drivers.gatling.CustomTypes.HttpRequestBuilderTransformer

import scala.collection.JavaConverters

class GatlingBodyMatcher(implicit configuration: GatlingConfiguration) extends org.loadtest4j.Body.Matcher[HttpRequestBuilderTransformer] {
  override def string(str: String): HttpRequestBuilderTransformer = {
    val gatlingBody = CompositeByteArrayBody(str)
    builder => builder.body(gatlingBody)
  }

  override def multipart(body: util.List[loadtest4j.BodyPart]): HttpRequestBuilderTransformer = {
    val gatlingBodyParts = JavaConverters.asScalaBuffer(body).map(bp => bp.`match`(new GatlingBodyPartMatcher()))

    builder => {
      gatlingBodyParts.foldLeft(builder)((accumulatingBuilder: HttpRequestBuilder, gatlingBodyPart: BodyPart) => {
        accumulatingBuilder.bodyPart(gatlingBodyPart)
      })
    }
  }
}