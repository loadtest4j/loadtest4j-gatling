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
    new Gatling(5, serviceUrl, 1)
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
    assertEquals(0, result.getErrors)
    assertGreaterThanOrEqualTo(1, result.getRequests)
    // And
    verifyHttp(httpServer).atLeast(1, method(Method.GET), uri("/"))
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
    assertGreaterThanOrEqualTo(1, result.getRequests)
    assertEquals(0, result.getErrors)
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

  private def assertGreaterThanOrEqualTo(expected: Long, actual: Long): Unit  ={
    val msg = "Expected %d to be >= %d, but it was not.".format(actual, expected)
    assertTrue(msg, actual >= expected)
  }
}

object GatlingTest {
  // Silence Restito logging.
  Logger.getLogger("org.glassfish.grizzly").setLevel(Level.OFF)
}