package com.example.thredsnp

import com.example.thredsnp.model.UserModel
import com.example.thredsnp.repository.UserRepoImpl
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UserRepoImplTest {

    private lateinit var userRepo: UserRepoImpl

    @Before
    fun setUp() {
        userRepo = UserRepoImpl()
    }

    @Test
    fun addUser_returnsSuccess() {
        val user = UserModel(userId = "1", fullName = "Test User", email = "test@example.com")
        
        userRepo.addUser(user) { success, message ->
            assertTrue(success)
            assertEquals("User registered successfully", message)
        }
    }

    @Test
    fun getUserById_returnsCorrectUser() {
        val user = UserModel(userId = "123", fullName = "John Doe")
        userRepo.addUser(user) { _, _ -> }

        userRepo.getUserById("123") { retrievedUser ->
            assertNotNull(retrievedUser)
            assertEquals("John Doe", retrievedUser?.fullName)
        }
    }

    @Test
    fun updateProfile_updatesCorrectFields() {
        val user = UserModel(userId = "1", fullName = "Old Name", phone = "12345")
        userRepo.addUser(user) { _, _ -> }

        val updates = mapOf("fullName" to "New Name", "phone" to "67890")
        
        userRepo.updateProfile("1", updates) { success, _ ->
            assertTrue(success)
            userRepo.getUserById("1") { updatedUser ->
                assertEquals("New Name", updatedUser?.fullName)
                assertEquals("67890", updatedUser?.phone)
            }
        }
    }

    @Test
    fun updateProfile_failsForInvalidId() {
        val updates = mapOf("fullName" to "New Name")
        userRepo.updateProfile("999", updates) { success, message ->
            assertFalse(success)
            assertEquals("User not found", message)
        }
    }

    @Test
    fun deleteUser_removesUserFromList() {
        val user = UserModel(userId = "delete_me")
        userRepo.addUser(user) { _, _ -> }

        userRepo.deleteUser("delete_me") { success, _ ->
            assertTrue(success)
            userRepo.getUserById("delete_me") { retrievedUser ->
                assertNull(retrievedUser)
            }
        }
    }

    @Test
    fun deleteUser_failsForInvalidId() {
        userRepo.deleteUser("non_existent") { success, message ->
            assertFalse(success)
            assertEquals("User not found", message)
        }
    }

    @Test
    fun getUserById_returnsNullForInvalidId() {
        userRepo.getUserById("non_existent") { user ->
            assertNull(user)
        }
    }
}
