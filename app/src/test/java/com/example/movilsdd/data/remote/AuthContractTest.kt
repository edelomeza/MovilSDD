package com.example.movilsdd.data.remote

import com.example.movilsdd.data.remote.dto.LoginRequestDto
import com.example.movilsdd.data.remote.dto.LoginResponseDto
import com.example.movilsdd.shared.TestDataFactory
import com.example.movilsdd.ui.navigation.HomeRoute
import com.example.movilsdd.ui.navigation.LoginRoute
import java.io.File
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AuthContractTest {
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = true
        encodeDefaults = true
    }

    @Test
    fun loginRequestUsesWireFieldNames() {
        val encoded = json.encodeToString(LoginRequestDto.serializer(), TestDataFactory.loginRequestDto())
        val fields = json.parseToJsonElement(encoded).jsonObject
        assertTrue(fields.containsKey("strNombreUsuario"))
        assertTrue(fields.containsKey("strPWD"))
        assertFalse(fields.containsKey("username"))
        assertFalse(fields.containsKey("password"))
        assertContractFixture("loginRequest.json", encoded)
    }

    @Test
    fun loginResponseUsesWireFieldNames() {
        val encoded = json.encodeToString(LoginResponseDto.serializer(), TestDataFactory.loginResponseDto())
        val fields = json.parseToJsonElement(encoded).jsonObject
        assertTrue(fields.containsKey("strToken"))
        assertTrue(fields.containsKey("dteExpira"))
        assertFalse(fields.containsKey("token"))
        assertFalse(fields.containsKey("expiresAt"))
        assertContractFixture("loginResponse.json", encoded)
    }

    @Test
    fun loginDtosRoundtrip() {
        val request = TestDataFactory.loginRequestDto()
        val decodedRequest = json.decodeFromString(LoginRequestDto.serializer(), json.encodeToString(LoginRequestDto.serializer(), request))
        assertEquals(request, decodedRequest)
        val response = TestDataFactory.loginResponseDto()
        val decodedResponse = json.decodeFromString(LoginResponseDto.serializer(), json.encodeToString(LoginResponseDto.serializer(), response))
        assertEquals(response, decodedResponse)
    }

    @Test
    fun routesExposeExpectedFields() {
        val login = json.encodeToString(LoginRoute.serializer(), LoginRoute())
        assertTrue(json.parseToJsonElement(login).jsonObject.containsKey("error"))
        assertContractFixture("loginRoute.json", login)
        val home = json.encodeToString(HomeRoute.serializer(), HomeRoute(TestDataFactory.VALID_USERNAME))
        val username = json.parseToJsonElement(home).jsonObject.getValue("username").jsonPrimitive.content
        assertEquals(TestDataFactory.VALID_USERNAME, username)
        assertContractFixture("homeRoute.json", home)
    }

    private fun assertContractFixture(name: String, encoded: String) {
        val file = File(FixtureDir, name)
        if (IsCaptureEnabled) {
            file.parentFile?.mkdirs()
            file.writeText(encoded + "\n")
        }
        assertTrue(file.exists(), "Missing contract fixture $name; regenerate with CAPTURE=1")
        assertEquals(encoded, file.readText().trim())
    }

    private companion object {
        val FixtureDir = File("src/test/resources/fixtures/auth")
        val IsCaptureEnabled = System.getenv("CAPTURE") == "1"
    }
}
