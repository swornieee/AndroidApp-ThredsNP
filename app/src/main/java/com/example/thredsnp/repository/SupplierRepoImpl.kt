package com.example.thredsnp.repository

import com.example.thredsnp.model.SupplierModel

class SupplierRepoImpl : SupplierRepo {
    // For demonstration, using an in-memory list. 
    // In a real app, this would be Firebase or Room.
    private val suppliers = mutableListOf<SupplierModel>()

    override fun addSupplier(supplierModel: SupplierModel, callback: (Boolean, String) -> Unit) {
        suppliers.add(supplierModel)
        callback(true, "Supplier added successfully")
    }

    override fun getSupplierById(supplierId: String, callback: (SupplierModel?) -> Unit) {
        val supplier = suppliers.find { it.supplierId == supplierId }
        callback(supplier)
    }

    override fun updateSupplierProfile(supplierId: String, data: Map<String, Any>, callback: (Boolean, String) -> Unit) {
        val index = suppliers.indexOfFirst { it.supplierId == supplierId }
        if (index != -1) {
            val supplier = suppliers[index]
            val updatedSupplier = supplier.copy(
                shopName = data["shopName"] as? String ?: supplier.shopName,
                phone = data["phone"] as? String ?: supplier.phone,
                address = data["address"] as? String ?: supplier.address,
                category = data["category"] as? String ?: supplier.category
            )
            suppliers[index] = updatedSupplier
            callback(true, "Supplier profile updated")
        } else {
            callback(false, "Supplier not found")
        }
    }

    override fun deleteSupplier(supplierId: String, callback: (Boolean, String) -> Unit) {
        val removed = suppliers.removeIf { it.supplierId == supplierId }
        if (removed) {
            callback(true, "Supplier removed successfully")
        } else {
            callback(false, "Supplier not found")
        }
    }

    override fun getAllSuppliers(callback: (List<SupplierModel>?) -> Unit) {
        callback(suppliers.toList())
    }
}