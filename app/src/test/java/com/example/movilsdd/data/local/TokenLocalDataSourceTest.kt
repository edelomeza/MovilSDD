package com.example.movilsdd.data.local

import com.example.movilsdd.shared.InMemoryTokenStore
import com.example.movilsdd.shared.TestDataFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TokenLocalDataSourceTest {
    private lateinit var store: InMemoryTokenStore

    @BeforeEach
    fun setUp() {
        store = InMemoryTokenStore()
    }

    @Test
    fun saveAndReadRoundtrip() {
        val expiresAtMillis = TestDataFactory.futureExpiresAtMillis()
        store.saveCredentials(TestDataFactory.FIXTURE_TOKEN, TestDataFactory.VALID_USERNAME, expiresAtMillis)
        assertEquals(TestDataFactory.FIXTURE_TOKEN, store.readToken())
        assertEquals(TestDataFactory.VALID_USERNAME, store.readUsername())
        assertEquals(expiresAtMillis, store.readExpiresAtMillis())
    }

    @Test
    fun clearRemovesEverything() {
        store.saveCredentials(TestDataFactory.FIXTURE_TOKEN, TestDataFactory.VALID_USERNAME, TestDataFactory.futureExpiresAtMillis())
        store.clear()
        assertNull(store.readToken())
        assertNull(store.readUsername())
        assertEquals(0L, store.readExpiresAtMillis())
    }

    @Test
    fun overwriteReplacesPreviousCredentials() {
        store.saveCredentials("first", "first.user", TestDataFactory.futureExpiresAtMillis())
        val expiresAtMillis = TestDataFactory.futureExpiresAtMillis()
        store.saveCredentials(TestDataFactory.FIXTURE_TOKEN, TestDataFactory.VALID_USERNAME, expiresAtMillis)
        assertEquals(TestDataFactory.FIXTURE_TOKEN, store.readToken())
        assertEquals(TestDataFactory.VALID_USERNAME, store.readUsername())
        assertEquals(expiresAtMillis, store.readExpiresAtMillis())
    }

    @Test
    fun emptyStoreReturnsDefaults() {
        assertNull(store.readToken())
        assertNull(store.readUsername())
        assertEquals(0L, store.readExpiresAtMillis())
    }
}
