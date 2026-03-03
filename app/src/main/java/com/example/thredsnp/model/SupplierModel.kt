package com.example.thredsnp.model

data class SupplierModel(
    val supplierId: String = "",
    val shopName: String = "",
    val ownerName: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val registrationNumber: String = "", // Business Reg No.
    val category: String = "", // e.g., Fashion, Sports, Shoes
    val rating: Double = 0.0,
    val logoUrl: String = "",
    val isVerified: Boolean = false
)