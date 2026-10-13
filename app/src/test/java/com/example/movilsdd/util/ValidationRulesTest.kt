package com.example.movilsdd.util

import com.example.movilsdd.common.DomainError
import com.example.movilsdd.common.SealedResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class ValidationRulesTest {
    @Test
    fun validUsernamePasses() {
        assertTrue(validateUsername("john.doe-99") is SealedResult.Success)
    }

    @Test
    fun usernameBoundariesPass() {
        assertTrue(validateUsername("abc") is SealedResult.Success)
        assertTrue(validateUsername("a".repeat(50)) is SealedResult.Success)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "a", "ab", "user name", "user@host", "user!", "user#tag", "Álvaro"])
    fun invalidUsernamesFail(value: String) {
        assertInstanceOf(SealedResult.Failure::class.java, validateUsername(value))
    }

    @Test
    fun tooLongUsernameFails() {
        assertInstanceOf(SealedResult.Failure::class.java, validateUsername("a".repeat(51)))
    }

    @Test
    fun emptyUsernameReportsHelpMessage() {
        val expected = SealedResult.Failure(DomainError.Validation("Usuario: usa 3–50 caracteres (letras, números, punto, guion)."))
        assertEquals(expected, validateUsername(""))
    }

    @Test
    fun validPasswordPasses() {
        assertTrue(validatePassword("TestPassword123") is SealedResult.Success)
    }

    @Test
    fun passwordBoundariesPass() {
        assertTrue(validatePassword("a".repeat(8)) is SealedResult.Success)
        assertTrue(validatePassword("a".repeat(128)) is SealedResult.Success)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "short", "1234567"])
    fun invalidPasswordsFail(value: String) {
        assertInstanceOf(SealedResult.Failure::class.java, validatePassword(value))
    }

    @Test
    fun tooLongPasswordFails() {
        assertInstanceOf(SealedResult.Failure::class.java, validatePassword("a".repeat(129)))
    }

    @Test
    fun emptyPasswordReportsHelpMessage() {
        val expected = SealedResult.Failure(DomainError.Validation("Contraseña: usa 8–128 caracteres."))
        assertEquals(expected, validatePassword(""))
    }
}
