package com.example.thredsnp

import androidx.compose.runtime.mutableStateListOf
import com.example.thredsnp.view.ProductItem

object ProductManager {
    val products = mutableStateListOf(
        ProductItem("P1", "Summer T-Shirt", "NRP 1,500", ""),
        ProductItem("P2", "Wireless Headphones", "NRP 5,500", ""),
        ProductItem("P3", "Sneakers Pro", "NRP 8,200", ""),
        ProductItem("P4", "Classic Watch", "NRP 12,000", ""),
        ProductItem("P5", "Backpack", "NRP 3,200", ""),
        ProductItem("P6", "Denim Jacket", "NRP 4,800", "")
    )
}