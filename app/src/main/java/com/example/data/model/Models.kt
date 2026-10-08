package com.example.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

enum class UserRole(val displayName: String, val level: Int) {
    SUPER_ADMIN("Super Admin", 5),
    ADMIN("Admin", 4),
    STORE_MANAGER("Store Manager", 3),
    ACCOUNTANT("Accountant", 2),
    STAFF("Staff", 1);

    fun canManageUsers(): Boolean = level >= 4
    fun canManageSettings(): Boolean = level >= 4
    fun canStockIn(): Boolean = level >= 3
    fun canStockOut(): Boolean = level >= 1
    fun canEditStock(): Boolean = level >= 3
    fun canDeleteStock(): Boolean = level >= 4
    fun canViewReports(): Boolean = level >= 2
}

enum class ItemCategory(val displayName: String, val codePrefix: String) {
    UNIFORM("Uniform / Dress", "UNF"),
    SHOES("Shoes", "SHO"),
    ACCESSORIES("Accessories", "ACC"),
    BOOKS("Books", "BOK"),
    COPIES("Notebooks / Copies", "CPY"),
    CUSTOM("Custom Items", "CST");

    companion object {
        fun fromString(value: String): ItemCategory {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: CUSTOM
        }
    }
}

enum class PaymentMethod(val displayName: String) {
    CASH("Cash"),
    UPI("UPI"),
    CARD("Card"),
    BANK_TRANSFER("Bank Transfer"),
    OTHER("Other")
}

@Entity(tableName = "schools")
data class School(
    @PrimaryKey val id: String,
    val name: String,
    val code: String,
    val address: String,
    val phone: String,
    val email: String,
    val gstNumber: String,
    val academicSession: String,
    val currencySymbol: String = "₹",
    val receiptNotes: String = "Thank you for your purchase. Learnium International School."
)

@Entity(
    tableName = "users",
    indices = [Index(value = ["schoolId", "username"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val schoolId: String,
    val username: String,
    val password: String, // Clean internal credential
    val fullName: String,
    val email: String,
    val role: UserRole,
    val isActive: Boolean = true
)

@Entity(
    tableName = "stock_items",
    indices = [
        Index(value = ["schoolId", "sku"], unique = true),
        Index(value = ["schoolId", "category"])
    ]
)
data class StockItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val schoolId: String,
    val name: String,
    val category: String, // UNIFORM, SHOES, ACCESSORIES, BOOKS, COPIES, CUSTOM
    val subCategory: String = "",
    val sku: String,
    val barcode: String = "",
    val targetClass: String = "All", // Nursery, LKG, UKG, Class 1..12, All
    val gender: String = "Unisex", // Boys, Girls, Unisex
    val size: String = "",
    val color: String = "",
    val brandOrPublisher: String = "",
    val isbn: String = "",
    val subject: String = "",
    val edition: String = "",
    val pages: Int = 0,
    val purchasePrice: Double,
    val sellingPrice: Double,
    val openingStock: Int = 0,
    val currentStock: Int = 0,
    val minStockLevel: Int = 10,
    val supplierId: Long? = null,
    val supplierName: String = "",
    val academicSession: String = "2026-2027",
    val description: String = "",
    val isActive: Boolean = true,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    val isOutOfStock: Boolean get() = currentStock <= 0
    val isLowStock: Boolean get() = currentStock > 0 && currentStock <= minStockLevel
    val isOverstock: Boolean get() = currentStock >= (minStockLevel * 4).coerceAtLeast(40)
}

@Entity(tableName = "suppliers")
data class Supplier(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val schoolId: String,
    val name: String,
    val contactPerson: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val gstNumber: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val schoolId: String,
    val studentId: String,
    val name: String,
    val className: String,
    val section: String = "A",
    val parentName: String = "",
    val parentPhone: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "stock_transactions")
data class StockTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val schoolId: String,
    val type: String, // PURCHASE, ADJUSTMENT_IN, RETURN_IN
    val itemId: Long,
    val itemName: String,
    val category: String = "",
    val supplierId: Long? = null,
    val supplierName: String = "",
    val quantity: Int,
    val unitCost: Double,
    val totalCost: Double,
    val invoiceNumber: String = "",
    val dateMillis: Long = System.currentTimeMillis(),
    val notes: String = "",
    val staffName: String = "Admin"
)

@Entity(tableName = "sale_orders")
data class SaleOrder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val schoolId: String,
    val receiptNumber: String,
    val studentId: String = "",
    val studentName: String = "",
    val className: String = "",
    val section: String = "",
    val parentName: String = "",
    val totalAmount: Double,
    val discountAmount: Double = 0.0,
    val netAmount: Double,
    val paymentMethod: String = PaymentMethod.CASH.name,
    val dateMillis: Long = System.currentTimeMillis(),
    val staffName: String = "Staff",
    val notes: String = ""
)

@Entity(
    tableName = "sale_order_items",
    indices = [Index(value = ["saleOrderId"])]
)
data class SaleOrderItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val saleOrderId: Long,
    val itemId: Long,
    val itemName: String,
    val category: String,
    val sku: String,
    val size: String = "",
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double
)

data class SaleWithItems(
    @Embedded val order: SaleOrder,
    @Relation(
        parentColumn = "id",
        entityColumn = "saleOrderId"
    )
    val items: List<SaleOrderItem>
)
