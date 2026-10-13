package com.example.movilsdd.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SealedResultTest {
    @Test
    fun successCarriesData() {
        val result: SealedResult<String> = SealedResult.Success("ok")
        assertTrue(result is SealedResult.Success)
        assertEquals("ok", (result as SealedResult.Success).data)
    }

    @Test
    fun failureCarriesTypedError() {
        val result: SealedResult<String> = SealedResult.Failure(DomainError.Validation("bad"))
        assertTrue(result is SealedResult.Failure)
        assertEquals(DomainError.Validation("bad"), (result as SealedResult.Failure).error)
    }
}
