package com.example.thredsnp

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import com.example.thredsnp.model.ProductItem
import com.example.thredsnp.view.Order
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream

object ProductManager {
    private const val PREFS_NAME = "thredsnp_prefs"
    private const val KEY_PRODUCTS = "products_list"
    private const val KEY_WISHLIST = "wishlist_list"
    private val database = FirebaseDatabase.getInstance().reference

    val products = mutableStateListOf<ProductItem>()
    val wishlist = mutableStateListOf<ProductItem>()
    val orders = mutableStateListOf<Order>()

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        if (products.isEmpty()) {
            val productsJson = prefs.getString(KEY_PRODUCTS, null)
            if (productsJson != null) {
                try {
                    val decoded = Json.decodeFromString<List<ProductItem>>(productsJson)
                    products.addAll(decoded)
                } catch (e: Exception) {
                    loadDefaults()
                }
            } else {
                loadDefaults()
            }
        }

        if (wishlist.isEmpty()) {
            val wishlistJson = prefs.getString(KEY_WISHLIST, null)
            if (wishlistJson != null) {
                try {
                    val decoded = Json.decodeFromString<List<ProductItem>>(wishlistJson)
                    wishlist.addAll(decoded)
                } catch (e: Exception) { }
            }
        }
        
        listenForOrders()
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
        if (product.imageUrl.startsWith("file:///data/user/")) {
            try {
                val file = File(Uri.parse(product.imageUrl).path ?: "")
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) { }
        }
        products.remove(product)
        wishlist.remove(product)
        saveProducts(context)
        saveWishlist(context)
    }

    fun toggleWishlist(context: Context, product: ProductItem) {
        if (wishlist.contains(product)) {
            wishlist.remove(product)
        } else {
            wishlist.add(product)
        }
        saveWishlist(context)
    }

    fun placeOrder(context: Context, order: Order) {
        val orderId = order.id.replace("#", "")
        database.child("orders").child(orderId).setValue(order)
    }

    private fun listenForOrders() {
        database.child("orders").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newOrders = mutableListOf<Order>()
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(Order::class.java)
                    if (order != null) {
                        newOrders.add(0, order)
                    }
                }
                orders.clear()
                orders.addAll(newOrders)
            }
            override fun onCancelled(error: DatabaseError) { }
        })
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

    private fun saveWishlist(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonString = Json.encodeToString(wishlist.toList())
        prefs.edit().putString(KEY_WISHLIST, jsonString).apply()
    }
}
