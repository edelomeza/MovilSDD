package com.example.movilsdd.data.local

import app.cash.turbine.test
import com.example.movilsdd.shared.InMemoryTokenStore
import com.example.movilsdd.shared.TestDataFactory
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SessionManagerImplTest {
    private lateinit var store: InMemoryTokenStore
    private lateinit var manager: SessionManagerImpl

    @BeforeEach
    fun setUp() {
        store = InMemoryTokenStore()
        manager = SessionManagerImpl(tokenStore = store)
    }

    @Test
    fun emptyStoreStartsWithoutSession() = runTest {
        assertNull(manager.getCurrentUser())
        assertFalse(manager.isSessionValid())
    }

    @Test
    fun saveSessionEmitsActiveSession() = runTest {
        manager.sessionFlow.test {
            assertNull(awaitItem())
            manager.saveSession(TestDataFactory.loginResponse(expiresAtMillis = TestDataFactory.futureExpiresAtMillis()))
            val emitted = awaitItem()
            assertEquals(TestDataFactory.VALID_USERNAME, emitted?.username)
            assertEquals(TestDataFactory.FIXTURE_TOKEN, emitted?.token)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun savedSessionIsValidBeforeExpiry() = runTest {
        manager.saveSession(TestDataFactory.loginResponse(expiresAtMillis = TestDataFactory.futureExpiresAtMillis()))
        assertTrue(manager.isSessionValid())
        assertEquals(TestDataFactory.VALID_USERNAME, manager.getCurrentUser()?.username)
    }

    @Test
    fun expiredSessionIsInvalid() = runTest {
        manager.saveSession(TestDataFactory.loginResponse(expiresAtMillis = TestDataFactory.pastExpiresAtMillis()))
        assertFalse(manager.isSessionValid())
    }

    @Test
    fun clearSessionRemovesEverything() = runTest {
        manager.saveSession(TestDataFactory.loginResponse(expiresAtMillis = TestDataFactory.futureExpiresAtMillis()))
        assertTrue(manager.isSessionValid())
        manager.clearSession()
        assertFalse(manager.isSessionValid())
        assertNull(manager.getCurrentUser())
        assertNull(store.readToken())
        assertNull(store.readUsername())
        assertEquals(0L, store.readExpiresAtMillis())
    }

    @Test
    fun preStoredCredentialsLoadOnInit() = runTest {
        store.saveCredentials(TestDataFactory.FIXTURE_TOKEN, TestDataFactory.VALID_USERNAME, TestDataFactory.futureExpiresAtMillis())
        val restored = SessionManagerImpl(tokenStore = store)
        assertEquals(TestDataFactory.VALID_USERNAME, restored.getCurrentUser()?.username)
        assertTrue(restored.isSessionValid())
    }
}
