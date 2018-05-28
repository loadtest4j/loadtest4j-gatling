package com.github.loadtest4j.drivers.gatling

import com.github.loadtest4j.loadtest4j.DriverFactory
import org.junit.Test
import org.junit.Assert._

import scala.collection.JavaConverters

class GatlingFactoryTest {
    @Test
    def testGetMandatoryProperties(): Unit = {
        val sut = driverFactory()

        val mandatoryProperties = sut.getMandatoryProperties

        assertEquals(2, mandatoryProperties.size())
        assertTrue(mandatoryProperties.contains("duration"))
        assertTrue(mandatoryProperties.contains("url"))
    }

    @Test
    def testCreate(): Unit = {
        val sut = driverFactory()

        val properties = JavaConverters.mapAsJavaMap(Map("duration" -> "2", "url" -> "https://example.com"))

        val driver = sut.create(properties)

        assertNotNull(driver)
    }

    private def driverFactory(): DriverFactory = {
        new GatlingFactory()
    }
}
