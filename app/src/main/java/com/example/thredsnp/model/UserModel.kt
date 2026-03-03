package com.example.thredsnp.model

data class UserModel(
    val userId: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val profileImageUrl: String = "",
    val userType: String = "Customer" // "Customer" or "Supplier"
)