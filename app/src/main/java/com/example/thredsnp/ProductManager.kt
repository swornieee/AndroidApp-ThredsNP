package com.example.thredsnp

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import com.example.thredsnp.model.ProductItem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream

object ProductManager {
    private const val PREFS_NAME = "thredsnp_prefs"
    private const val KEY_PRODUCTS = "products_list"

    val products = mutableStateListOf<ProductItem>()

    fun init(context: Context) {
        if (products.isNotEmpty()) return
        
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonString = prefs.getString(KEY_PRODUCTS, null)
        
        if (jsonString != null) {
            try {
                val decoded = Json.decodeFromString<List<ProductItem>>(jsonString)
                products.clear()
                products.addAll(decoded)
            } catch (e: Exception) {
                loadDefaults()
            }
        } else {
            loadDefaults()
        }
    }

    private fun loadDefaults() {
        products.clear()
        products.addAll(listOf(
            ProductItem("P1", "Summer T-Shirt", "NRP 1,500", ""),
            ProductItem("P2", "Wireless Headphones", "NRP 5,500", ""),
            ProductItem("P3", "Sneakers Pro", "NRP 8,200", ""),
            ProductItem("P4", "Classic Watch", "NRP 12,000", ""),
            ProductItem("P5", "Backpack", "NRP 3,200", ""),
            ProductItem("P6", "Denim Jacket", "NRP 4,800", "")
        ))
    }

    fun addProduct(context: Context, product: ProductItem, imageUri: Uri?) {
        var finalProduct = product
        if (imageUri != null) {
            val internalPath = saveImageToInternalStorage(context, imageUri)
            if (internalPath != null) {
                finalProduct = product.copy(imageUrl = internalPath)
            }
        }
        products.add(finalProduct)
        saveProducts(context)
    }

    fun removeProduct(context: Context, product: ProductItem) {
        // Remove image if it's stored in internal storage
        if (product.imageUrl.startsWith("file:///data/user/")) {
            try {
                val file = File(Uri.parse(product.imageUrl).path ?: "")
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        products.remove(product)
        saveProducts(context)
    }

    private fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = "product_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(file).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveProducts(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonString = Json.encodeToString(products.toList())
        prefs.edit().putString(KEY_PRODUCTS, jsonString).apply()
    }
}
