package com.example.thredsnp.repository

import com.example.thredsnp.model.SupplierModel

interface SupplierRepo {
    fun addSupplier(supplierModel: SupplierModel, callback: (Boolean, String) -> Unit)
    fun getSupplierById(supplierId: String, callback: (SupplierModel?) -> Unit)
    fun updateSupplierProfile(supplierId: String, data: Map<String, Any>, callback: (Boolean, String) -> Unit)
    fun deleteSupplier(supplierId: String, callback: (Boolean, String) -> Unit)
    fun getAllSuppliers(callback: (List<SupplierModel>?) -> Unit)
}