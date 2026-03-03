package com.example.thredsnp

import com.example.thredsnp.model.SupplierModel
import com.example.thredsnp.repository.SupplierRepoImpl
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SupplierRepoImplTest {

    private lateinit var supplierRepo: SupplierRepoImpl

    @Before
    fun setUp() {
        supplierRepo = SupplierRepoImpl()
    }

    @Test
    fun addSupplier_returnsSuccess() {
        val supplier = SupplierModel(supplierId = "S1", shopName = "Fashion Hub")
        supplierRepo.addSupplier(supplier) { success, message ->
            assertTrue(success)
            assertEquals("Supplier added successfully", message)
        }
    }

    @Test
    fun getSupplierById_returnsCorrectSupplier() {
        val supplier = SupplierModel(supplierId = "S123", shopName = "Trend setters")
        supplierRepo.addSupplier(supplier) { _, _ -> }

        supplierRepo.getSupplierById("S123") { retrieved ->
            assertNotNull(retrieved)
            assertEquals("Trend setters", retrieved?.shopName)
        }
    }

    @Test
    fun getAllSuppliers_returnsAllItems() {
        supplierRepo.addSupplier(SupplierModel(supplierId = "1"), { _, _ -> })
        supplierRepo.addSupplier(SupplierModel(supplierId = "2"), { _, _ -> })

        supplierRepo.getAllSuppliers { list ->
            assertEquals(2, list?.size)
        }
    }

    @Test
    fun deleteSupplier_removesFromList() {
        val supplier = SupplierModel(supplierId = "temp")
        supplierRepo.addSupplier(supplier) { _, _ -> }

        supplierRepo.deleteSupplier("temp") { success, _ ->
            assertTrue(success)
            supplierRepo.getSupplierById("temp") { retrieved ->
                assertNull(retrieved)
            }
        }
    }
}
