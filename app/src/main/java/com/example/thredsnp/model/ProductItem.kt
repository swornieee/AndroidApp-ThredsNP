package com.example.thredsnp.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductItem(val id: String, val name: String, val price: String, val imageUrl: String)
