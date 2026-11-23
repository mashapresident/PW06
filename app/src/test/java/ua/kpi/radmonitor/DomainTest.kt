package ua.study.smartgrid
import ua.study.smartgrid.*
import ua.study.smartgrid.domain.*
import ua.study.smartgrid.data.*
import ua.study.smartgrid.viewmodel.*
import ua.study.smartgrid.device.*
import ua.study.smartgrid.ui.*


import org.junit.Assert.*
import org.junit.Test

class DomainTest {
    @Test
    fun doseCalculation() {
        assertEquals(.96, MonitorLogic.energy(.12, 8.0), 1e-10)
    }

    @Test
    fun zeroDuration() {
        assertEquals(0.0, MonitorLogic.energy(.3, 0.0), 0.0)
    }

    @Test
    fun rejectInvalidDose() {
        for (v in listOf(-1.0, Double.NaN, Double.POSITIVE_INFINITY)) {
            assertThrows(IllegalArgumentException::class.java) { MonitorLogic.energy(v, 2.0) }
        }
    }

    @Test
    fun rejectOverflow() {
        assertThrows(IllegalArgumentException::class.java) {
            MonitorLogic.energy(Double.MAX_VALUE, 2.0)
        }
    }

    @Test
    fun coordinateLimits() {
        assertNull(MonitorLogic.validate(Measurement(latitude = -90.0, longitude = 180.0)))
        assertNotNull(MonitorLogic.validate(Measurement(latitude = 91.0)))
        assertNotNull(MonitorLogic.validate(Measurement(longitude = Double.NaN)))
    }

    @Test
    fun rejectNegativeReading() {
        assertNotNull(MonitorLogic.validate(Measurement(loadMw = -.01)))
    }

    @Test
    fun rejectEmptyName() {
        assertNotNull(MonitorLogic.validate(Measurement(name = "  ")))
    }

    @Test
    fun filterAndSort() {
        val result = MonitorLogic.filter(MonitorLogic.samples(), "вузол", "Усі", "Показник")
        assertEquals("post-c", result.first().id)
        assertEquals(1, MonitorLogic.filter(result, " B ", "Фідер", "Дата").size)
        assertTrue(MonitorLogic.filter(result, "zz", "Усі", "Дата").isEmpty())
    }

    @Test
    fun validateCredentials() {
        assertNotNull(MonitorLogic.validateLogin("notemail", "12345678"))
        assertNotNull(MonitorLogic.validateLogin("a@b.ua", "123"))
        assertNull(MonitorLogic.validateLogin("operator@demo.ua", "Demo12345"))
    }

    @Test
    fun jsonRoundTrip() {
        val rows = MonitorLogic.samples()
        assertEquals(rows, Exchange.fromJson(Exchange.json(rows)))
    }

    @Test
    fun csvQuotedFieldsRoundTrip() {
        val rows = listOf(Measurement(name = "Вузол, \"А\"\nКиїв"))
        assertEquals(rows, Exchange.fromCsv(Exchange.csv(rows)))
    }

    @Test
    fun rejectMalformedCsv() {
        assertThrows(IllegalArgumentException::class.java) { Exchange.fromCsv("bad,header") }
        assertThrows(IllegalArgumentException::class.java) { Exchange.fromCsv("\"open") }
    }

    @Test
    fun rejectDuplicateIds() {
        val row = Measurement()
        assertThrows(IllegalArgumentException::class.java) {
            Exchange.fromJson(Exchange.json(listOf(row, row)))
        }
    }

    @Test
    fun rejectInvalidImportedMeasurement() {
        assertThrows(IllegalArgumentException::class.java) {
            Exchange.fromJson(Exchange.json(listOf(Measurement(loadMw = -1.0))))
        }
    }
}
