package com.example.data.repository

import androidx.room.withTransaction
import com.example.data.local.AppDatabase
import com.example.data.model.SaleOrder
import com.example.data.model.SaleOrderItem
import com.example.data.model.SaleWithItems
import com.example.data.model.School
import com.example.data.model.StockItem
import com.example.data.model.StockTransaction
import com.example.data.model.Student
import com.example.data.model.Supplier
import com.example.data.model.User
import kotlinx.coroutines.flow.Flow

class InventoryRepository(private val database: AppDatabase) {
    private val schoolDao = database.schoolDao()
    private val userDao = database.userDao()
    private val stockItemDao = database.stockItemDao()
    private val supplierDao = database.supplierDao()
    private val studentDao = database.studentDao()
    private val stockTransactionDao = database.stockTransactionDao()
    private val saleOrderDao = database.saleOrderDao()

    // School
    fun getAllSchools(): Flow<List<School>> = schoolDao.getAllSchools()
    fun getSchool(schoolId: String): Flow<School?> = schoolDao.getSchoolById(schoolId)
    suspend fun saveSchool(school: School) = schoolDao.insertOrUpdate(school)

    // User / Auth
    fun getUsers(schoolId: String): Flow<List<User>> = userDao.getUsersBySchool(schoolId)
    suspend fun getUserByUsername(schoolId: String, username: String): User? =
        userDao.getUserByUsername(schoolId, username)
    suspend fun saveUser(user: User): Long = userDao.insertUser(user)
    suspend fun updateUser(user: User) = userDao.updateUser(user)
    suspend fun deleteUser(user: User) = userDao.deleteUser(user)

    // Stock Items
    fun getAllStock(schoolId: String): Flow<List<StockItem>> = stockItemDao.getAllStock(schoolId)
    fun getStockByCategory(schoolId: String, category: String): Flow<List<StockItem>> =
        stockItemDao.getStockByCategory(schoolId, category)
    fun getLowStockItems(schoolId: String): Flow<List<StockItem>> =
        stockItemDao.getLowStockItems(schoolId)
    fun getOutOfStockItems(schoolId: String): Flow<List<StockItem>> =
        stockItemDao.getOutOfStockItems(schoolId)
    suspend fun getItemById(schoolId: String, id: Long): StockItem? =
        stockItemDao.getItemById(schoolId, id)
    suspend fun getItemBySku(schoolId: String, sku: String): StockItem? =
        stockItemDao.getItemBySku(schoolId, sku)
    suspend fun insertItem(item: StockItem): Long = stockItemDao.insertItem(item)
    suspend fun updateItem(item: StockItem) = stockItemDao.updateItem(item)
    suspend fun deleteItem(item: StockItem) = stockItemDao.deleteItem(item)

    // Suppliers
    fun getSuppliers(schoolId: String): Flow<List<Supplier>> = supplierDao.getSuppliers(schoolId)
    suspend fun getSupplierById(id: Long): Supplier? = supplierDao.getSupplierById(id)
    suspend fun saveSupplier(supplier: Supplier): Long = supplierDao.insertSupplier(supplier)
    suspend fun updateSupplier(supplier: Supplier) = supplierDao.updateSupplier(supplier)
    suspend fun deleteSupplier(supplier: Supplier) = supplierDao.deleteSupplier(supplier)

    // Students
    fun getStudents(schoolId: String): Flow<List<Student>> = studentDao.getStudents(schoolId)
    suspend fun getStudentByCode(schoolId: String, code: String): Student? =
        studentDao.getByStudentId(schoolId, code)
    suspend fun saveStudent(student: Student): Long = studentDao.insertStudent(student)
    suspend fun updateStudent(student: Student) = studentDao.updateStudent(student)
    suspend fun deleteStudent(student: Student) = studentDao.deleteStudent(student)

    // Purchases / Stock In (Atomic Transaction)
    fun getTransactions(schoolId: String): Flow<List<StockTransaction>> =
        stockTransactionDao.getTransactions(schoolId)

    suspend fun executeStockIn(
        schoolId: String,
        item: StockItem,
        quantity: Int,
        unitCost: Double,
        invoiceNumber: String,
        supplierId: Long?,
        supplierName: String,
        notes: String,
        staffName: String
    ): Result<Long> = runCatching {
        database.withTransaction {
            val totalCost = quantity * unitCost
            val transaction = StockTransaction(
                schoolId = schoolId,
                type = "PURCHASE",
                itemId = item.id,
                itemName = item.name,
                category = item.category,
                supplierId = supplierId,
                supplierName = supplierName,
                quantity = quantity,
                unitCost = unitCost,
                totalCost = totalCost,
                invoiceNumber = invoiceNumber,
                dateMillis = System.currentTimeMillis(),
                notes = notes,
                staffName = staffName
            )
            val transId = stockTransactionDao.insertTransaction(transaction)
            stockItemDao.updateStockQuantity(item.id, quantity)
            transId
        }
    }

    // Sales / Stock Out (Atomic Transaction)
    fun getSales(schoolId: String): Flow<List<SaleWithItems>> =
        saleOrderDao.getSalesWithItems(schoolId)

    suspend fun getSaleById(saleId: Long): SaleWithItems? =
        saleOrderDao.getSaleById(saleId)

    suspend fun executeSale(
        order: SaleOrder,
        items: List<Pair<StockItem, Int>> // Item to Quantity
    ): Result<Long> = runCatching {
        database.withTransaction {
            // First check stock availability
            for ((item, qty) in items) {
                val latest = stockItemDao.getItemById(order.schoolId, item.id)
                    ?: error("Item '${item.name}' not found")
                if (latest.currentStock < qty) {
                    error("Insufficient stock for '${item.name}'. Available: ${latest.currentStock}, Requested: $qty")
                }
            }

            // Create Order
            val orderId = saleOrderDao.insertOrder(order)

            // Create order items & decrease stock
            val orderItems = items.map { (item, qty) ->
                stockItemDao.updateStockQuantity(item.id, -qty)
                SaleOrderItem(
                    saleOrderId = orderId,
                    itemId = item.id,
                    itemName = item.name,
                    category = item.category,
                    sku = item.sku,
                    size = item.size,
                    quantity = qty,
                    unitPrice = item.sellingPrice,
                    totalPrice = item.sellingPrice * qty
                )
            }
            saleOrderDao.insertOrderItems(orderItems)
            orderId
        }
    }

    suspend fun cancelSaleOrder(sale: SaleWithItems): Result<Unit> = runCatching {
        database.withTransaction {
            // Revert stock
            for (item in sale.items) {
                stockItemDao.updateStockQuantity(item.itemId, item.quantity)
            }
            saleOrderDao.deleteOrderItems(sale.order.id)
            saleOrderDao.deleteOrder(sale.order)
        }
    }
}
