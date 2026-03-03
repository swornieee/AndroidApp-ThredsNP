package com.example.thredsnp.repository

import com.example.thredsnp.model.UserModel

interface UserRepo {
    fun addUser(userModel: UserModel, callback: (Boolean, String) -> Unit)
    fun getUserById(userId: String, callback: (UserModel?) -> Unit)
    fun updateProfile(userId: String, data: Map<String, Any>, callback: (Boolean, String) -> Unit)
    fun deleteUser(userId: String, callback: (Boolean, String) -> Unit)
}