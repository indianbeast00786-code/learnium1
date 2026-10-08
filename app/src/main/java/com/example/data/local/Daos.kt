package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
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

@Dao
interface SchoolDao {
    @Query("SELECT * FROM schools")
    fun getAllSchools(): Flow<List<School>>

    @Query("SELECT * FROM schools WHERE id = :schoolId LIMIT 1")
    fun getSchoolById(schoolId: String): Flow<School?>

    @Query("SELECT * FROM schools WHERE id = :schoolId LIMIT 1")
    suspend fun getSchoolByIdDirect(schoolId: String): School?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(school: School)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE schoolId = :schoolId ORDER BY fullName ASC")
    fun getUsersBySchool(schoolId: String): Flow<List<User>>

    @Query("SELECT * FROM users WHERE schoolId = :schoolId AND username = :username LIMIT 1")
    suspend fun getUserByUsername(schoolId: String, username: String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)
}

@Dao
interface StockItemDao {
    @Query("SELECT * FROM stock_items WHERE schoolId = :schoolId ORDER BY name ASC")
    fun getAllStock(schoolId: String): Flow<List<StockItem>>

    @Query("SELECT * FROM stock_items WHERE schoolId = :schoolId AND category = :category ORDER BY name ASC")
    fun getStockByCategory(schoolId: String, category: String): Flow<List<StockItem>>

    @Query("SELECT * FROM stock_items WHERE schoolId = :schoolId AND id = :id LIMIT 1")
    suspend fun getItemById(schoolId: String, id: Long): StockItem?

    @Query("SELECT * FROM stock_items WHERE schoolId = :schoolId AND sku = :sku LIMIT 1")
    suspend fun getItemBySku(schoolId: String, sku: String): StockItem?

    @Query("SELECT * FROM stock_items WHERE schoolId = :schoolId AND currentStock <= minStockLevel AND currentStock > 0")
    fun getLowStockItems(schoolId: String): Flow<List<StockItem>>

    @Query("SELECT * FROM stock_items WHERE schoolId = :schoolId AND currentStock <= 0")
    fun getOutOfStockItems(schoolId: String): Flow<List<StockItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: StockItem): Long

    @Update
    suspend fun updateItem(item: StockItem)

    @Delete
    suspend fun deleteItem(item: StockItem)

    @Query("UPDATE stock_items SET currentStock = currentStock + :quantityDelta, lastUpdated = :now WHERE id = :itemId")
    suspend fun updateStockQuantity(itemId: Long, quantityDelta: Int, now: Long = System.currentTimeMillis())
}

@Dao
interface SupplierDao {
    @Query("SELECT * FROM suppliers WHERE schoolId = :schoolId ORDER BY name ASC")
    fun getSuppliers(schoolId: String): Flow<List<Supplier>>

    @Query("SELECT * FROM suppliers WHERE id = :id LIMIT 1")
    suspend fun getSupplierById(id: Long): Supplier?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplier(supplier: Supplier): Long

    @Update
    suspend fun updateSupplier(supplier: Supplier)

    @Delete
    suspend fun deleteSupplier(supplier: Supplier)
}

@Dao
interface StudentDao {
    @Query("SELECT * FROM students WHERE schoolId = :schoolId ORDER BY name ASC")
    fun getStudents(schoolId: String): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE schoolId = :schoolId AND studentId = :studentId LIMIT 1")
    suspend fun getByStudentId(schoolId: String, studentId: String): Student?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)
}

@Dao
interface StockTransactionDao {
    @Query("SELECT * FROM stock_transactions WHERE schoolId = :schoolId ORDER BY dateMillis DESC")
    fun getTransactions(schoolId: String): Flow<List<StockTransaction>>

    @Query("SELECT * FROM stock_transactions WHERE schoolId = :schoolId AND supplierId = :supplierId ORDER BY dateMillis DESC")
    fun getTransactionsBySupplier(schoolId: String, supplierId: Long): Flow<List<StockTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: StockTransaction): Long
}

@Dao
interface SaleOrderDao {
    @Transaction
    @Query("SELECT * FROM sale_orders WHERE schoolId = :schoolId ORDER BY dateMillis DESC")
    fun getSalesWithItems(schoolId: String): Flow<List<SaleWithItems>>

    @Transaction
    @Query("SELECT * FROM sale_orders WHERE id = :saleId LIMIT 1")
    suspend fun getSaleById(saleId: Long): SaleWithItems?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: SaleOrder): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<SaleOrderItem>)

    @Delete
    suspend fun deleteOrder(order: SaleOrder)

    @Query("DELETE FROM sale_order_items WHERE saleOrderId = :orderId")
    suspend fun deleteOrderItems(orderId: Long)
}
