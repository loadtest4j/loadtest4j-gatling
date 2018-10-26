package org.loadtest4j.drivers.gatling

import java.nio.file.Path

import io.gatling.core.body.RawFileBodies
import io.gatling.core.config.GatlingConfiguration
import io.gatling.http.request.BodyPart
import io.gatling.core.Predef._
import io.gatling.http.Predef.{RawFileBodyPart, StringBodyPart}

class GatlingBodyPartMatcher(implicit configuration: GatlingConfiguration) extends org.loadtest4j.BodyPart.Matcher[BodyPart] {
  override def stringPart(name: String, content: String): BodyPart = StringBodyPart(name, content)

  override def filePart(part: Path): BodyPart = {
    val theFile = part.toAbsolutePath.toString
    val rawFileBodies = new RawFileBodies
    RawFileBodyPart(theFile)(rawFileBodies)
  }
}