package com.example.movilsdd.security

import com.example.movilsdd.util.Logger
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class SanitizationTest {
    @Test
    fun loggerRedactsSecrets() {
        val out = Logger.sanitize("token: abc123 and password= hunter2")
        assertFalse(out.contains("abc123"))
        assertFalse(out.contains("hunter2"))
    }

    @Test
    fun loggerKeepsSafeText() {
        val out = Logger.sanitize("page=2 size=20")
        assertFalse(out.contains("***"))
    }
}
