package com.github.loadtest4j.drivers.gatling

import java.util

import com.github.loadtest4j.loadtest4j.Driver
import com.github.loadtest4j.loadtest4j.LoadTesterException
import com.xebialabs.restito.server.StubServer
import org.glassfish.grizzly.http.Method
import org.glassfish.grizzly.http.util.HttpStatus
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Collections
import java.util.logging.Level
import java.util.logging.Logger

import com.xebialabs.restito.builder.stub.StubHttp.whenHttp
import com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp
import com.xebialabs.restito.semantics.Action.status
import com.xebialabs.restito.semantics.Condition._
import org.junit.Assert._

class GatlingTest {

  private var httpServer: StubServer = _

  @Before
  def startServer(): Unit = {
    httpServer = new StubServer().run()
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
    try {
      driver.run(Collections.emptyList())
      fail("This should not work.");
    } catch {
      // Then
      case (e: LoadTesterException) => assertEquals("No requests were specified for the load test.", e.getMessage)
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