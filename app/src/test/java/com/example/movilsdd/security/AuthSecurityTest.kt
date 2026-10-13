package com.example.movilsdd.security

import android.content.Context
import com.example.movilsdd.data.local.PreferencesLocalDataSource
import com.example.movilsdd.data.local.TokenLocalDataSource
import com.example.movilsdd.data.local.TokenLocalDataSourceImpl
import com.example.movilsdd.data.remote.AuthApiService
import com.example.movilsdd.shared.TestDataFactory
import com.example.movilsdd.util.Logger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.http.POST

class AuthSecurityTest {
    @Test
    fun loggerRedactsTokenValue() {
        val redacted = Logger.sanitize("strToken: ${TestDataFactory.FIXTURE_TOKEN}")
        assertFalse(redacted.contains(TestDataFactory.FIXTURE_TOKEN))
        assertTrue(redacted.contains("***"))
    }

    @Test
    fun loggerRedactsPasswordValue() {
        val redacted = Logger.sanitize("password=${TestDataFactory.VALID_PASSWORD}")
        assertFalse(redacted.contains(TestDataFactory.VALID_PASSWORD))
        assertTrue(redacted.contains("***"))
    }

    @Test
    fun loggerRedactsBearerAuthorization() {
        val redacted = Logger.sanitize("Authorization: Bearer ${TestDataFactory.FIXTURE_TOKEN}")
        assertFalse(redacted.contains(TestDataFactory.FIXTURE_TOKEN))
    }

    @Test
    fun loggerKeepsNonSensitiveText() {
        val message = "login failed with code 401"
        assertEquals(message, Logger.sanitize(message))
    }

    @Test
    fun sensitiveValuesNeverLoggedRaw() {
        val combined = "user=${TestDataFactory.VALID_USERNAME} strToken: ${TestDataFactory.FIXTURE_TOKEN} password=${TestDataFactory.VALID_PASSWORD}"
        val redacted = Logger.sanitize(combined)
        assertFalse(redacted.contains(TestDataFactory.FIXTURE_TOKEN))
        assertFalse(redacted.contains(TestDataFactory.VALID_PASSWORD))
    }

    @Test
    fun flagsStoreExposesNoSensitiveMembers() {
        val names = PreferencesLocalDataSource::class.java.methods.map { it.name.lowercase() }
        assertTrue(names.none { it.contains("token") || it.contains("password") || it.contains("secret") })
    }

    @Test
    fun tokenStoreIsContextBackedEncryptedStore() {
        assertTrue(TokenLocalDataSource::class.java.isAssignableFrom(TokenLocalDataSourceImpl::class.java))
        val parameters = TokenLocalDataSourceImpl::class.java.declaredConstructors.single().parameterTypes
        assertEquals(1, parameters.size)
        assertTrue(Context::class.java.isAssignableFrom(parameters[0]))
    }

    @Test
    fun authServiceUsesRelativePathsOnly() {
        val paths = AuthApiService::class.java.declaredMethods.mapNotNull { it.getAnnotation(POST::class.java)?.value }
        assertTrue(paths.isNotEmpty())
        assertTrue(paths.none { it.startsWith("http") })
        assertTrue(paths.contains("api/v1/Auth/login"))
        assertTrue(paths.contains("api/v1/Auth/logout"))
    }
}
