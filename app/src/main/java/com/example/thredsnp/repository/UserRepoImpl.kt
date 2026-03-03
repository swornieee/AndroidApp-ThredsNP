package com.example.thredsnp.repository

import com.example.thredsnp.model.UserModel

class UserRepoImpl : UserRepo {
    // For demonstration, using an in-memory list. 
    // In a real app, this would be Firebase or Room.
    private val users = mutableListOf<UserModel>()

    override fun addUser(userModel: UserModel, callback: (Boolean, String) -> Unit) {
        users.add(userModel)
        callback(true, "User registered successfully")
    }

    override fun getUserById(userId: String, callback: (UserModel?) -> Unit) {
        val user = users.find { it.userId == userId }
        callback(user)
    }

    override fun updateProfile(userId: String, data: Map<String, Any>, callback: (Boolean, String) -> Unit) {
        val index = users.indexOfFirst { it.userId == userId }
        if (index != -1) {
            val user = users[index]
            // Update fields based on map (simplified)
            val updatedUser = user.copy(
                fullName = data["fullName"] as? String ?: user.fullName,
                phone = data["phone"] as? String ?: user.phone,
                address = data["address"] as? String ?: user.address
            )
            users[index] = updatedUser
            callback(true, "Profile updated successfully")
        } else {
            callback(false, "User not found")
        }
    }

    override fun deleteUser(userId: String, callback: (Boolean, String) -> Unit) {
        val removed = users.removeIf { it.userId == userId }
        if (removed) {
            callback(true, "User deleted successfully")
        } else {
            callback(false, "User not found")
        }
    }
}