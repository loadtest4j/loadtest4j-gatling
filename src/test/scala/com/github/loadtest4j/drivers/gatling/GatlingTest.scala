package com.github.loadtest4j.drivers.gatling

import java.util

import com.github.loadtest4j.loadtest4j.{Driver, LoadTesterException}
import com.xebialabs.restito.server.StubServer
import org.glassfish.grizzly.http.Method
import org.glassfish.grizzly.http.util.HttpStatus
import org.junit._
import java.util.Collections
import java.util.logging._

import com.xebialabs.restito.builder.stub.StubHttp.whenHttp
import com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp
import com.xebialabs.restito.semantics.Action.status
import com.xebialabs.restito.semantics.Condition._
import org.junit.Assert._
import scala.util.{Try, Failure}
import scala.concurrent.duration._

class GatlingTest {

  private val httpServer = new StubServer()

  @Before
  def startServer(): Unit = {
    httpServer.run()
  }

  @After
  def stopServer(): Unit = {
    httpServer.stop()
  }

  private def sut(): Driver = {
    val serviceUrl = "http://localhost:%d".format(httpServer.getPort)
    new Gatling(3.seconds, serviceUrl, 1)
  }

  @Test
  def testRun(): Unit = {
    // Given
    val driver = sut()
    // And
    whenHttp(httpServer).`match`(get("/")).`then`(status(HttpStatus.OK_200))

    // When
    val result = driver.run(Collections.singletonList(DriverRequests.get("/")))

    // Then
    assertEquals(0, result.getKo)
    assertGreaterThanOrEqualTo(1, result.getOk)
    assertTrue(result.getActualDuration.toMillis > 0)
    assertStartsWith("file://", result.getReportUrl.get())
    assertEndsWith("html", result.getReportUrl.get())
    // And
    verifyHttp(httpServer).atLeast(1, method(Method.GET), uri("/"))
  }

  @Test
  def testRunWithElaborateRequest(): Unit = {
    // Given
    val driver = sut()
    // And
    whenHttp(httpServer)
      .`match`(post("/"), withHeader("Accept", "application/json"), withHeader("Content-Type", "application/json"), withPostBodyContaining("{}"))
      .`then`(status(HttpStatus.OK_200))

    // When
    val requests = Collections.singletonList(DriverRequests.post("/", "{}", Map("Accept" -> "application/json", "Content-Type" -> "application/json")))
    val result = driver.run(requests)

    // Then
    assertEquals(0, result.getKo)
    assertGreaterThanOrEqualTo(1, result.getOk)
    // And
    verifyHttp(httpServer).atLeast(1, method(Method.POST), uri("/"), withHeader("Accept", "application/json"), withHeader("Content-Type", "application/json"), withPostBodyContaining("{}"))
  }

  @Test
  def testRunWithMultipleRequests(): Unit = {
    // Given
    val driver = sut()
    // And
    whenHttp(httpServer).`match`(get("/")).`then`(status(HttpStatus.OK_200))
    // And
    whenHttp(httpServer).`match`(get("/pets")).`then`(status(HttpStatus.OK_200))

    // When
    val requests = util.Arrays.asList(DriverRequests.get("/"), DriverRequests.get("/pets"))
    val result = driver.run(requests)

    // Then
    assertGreaterThanOrEqualTo(1, result.getOk)
    assertEquals(0, result.getKo)
    // And
    verifyHttp(httpServer).atLeast(1, method(Method.GET), uri("/"))
    // And
    verifyHttp(httpServer).atLeast(1, method(Method.GET), uri("/pets"))
  }

  @Test
  def testRunWithNoRequests(): Unit = {
    // Given
    val driver = sut()

    // When
    Try(driver.run(Collections.emptyList())) match {
      case Failure(e: LoadTesterException) => assertEquals("No requests were specified for the load test.", e.getMessage)
      case _ => fail("This should not work.")
    }
  }

  private def assertStartsWith(prefix: String, actual: String): Unit = {
    val msg = "'%s' did not start with the substring '%s'.".format(actual, prefix)
    assertTrue(msg, actual.startsWith(prefix))
  }

  private def assertEndsWith(suffix: String, actual: String): Unit = {
    val msg = "'%s' did not end with the substring '%s'.".format(actual, suffix)
    assertTrue(msg, actual.endsWith(suffix))
  }

  private def assertGreaterThanOrEqualTo(expected: Long, actual: Long): Unit  ={
    val msg = "Expected %d to be >= %d, but it was not.".format(actual, expected)
    assertTrue(msg, actual >= expected)
  }
}

object GatlingTest {
  // Silence Restito logging.
  Logger.getLogger("org.glassfish.grizzly").setLevel(Level.OFF)
}