package com.example

import com.example.crypto.QMaxCypherEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun qMaxCypher_computesDeterministicMatrixAndAvalancheEffect() {
        val salt = "A1B2C3D4E5F60718"
        val plain = "NGS|BRCA1:c.5266dupC|Q39"
        val baseline = QMaxCypherEngine.computeCypherState(
            plainInput = plain,
            saltHex = salt,
            toggledBitIndices = emptySet()
        )
        assertEquals(128, baseline.binaryMatrix128.size)

        val flipped = QMaxCypherEngine.computeCypherState(
            plainInput = plain,
            saltHex = salt,
            toggledBitIndices = setOf(0, 63)
        )
        assertNotEquals(baseline.sha256Hex, flipped.sha256Hex)
        assertTrue(flipped.flippedBitCount > 0)
    }

    @Test
    fun metasimulator_generatesValidW4SchemeprintAndT1Token() {
        val report = QMaxCypherEngine.runMetasimCalibration(
            variantGene = "BRCA1",
            hgvs = "c.5266dupC",
            pValue = 1.2e-9,
            phredQuality = 39,
            tensionLoad = 0.35f,
            reinforcementRate = 0.90f
        )
        assertTrue(report.schemeId.startsWith("W4-MAP-"))
        assertTrue(report.singleSubmissionToken.startsWith("T1-PODANI-"))
        assertEquals(4, report.divideReasoningSteps.size)
    }
}
