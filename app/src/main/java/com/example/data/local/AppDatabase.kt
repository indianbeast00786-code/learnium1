package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.model.SaleOrder
import com.example.data.model.SaleOrderItem
import com.example.data.model.School
import com.example.data.model.StockItem
import com.example.data.model.StockTransaction
import com.example.data.model.Student
import com.example.data.model.Supplier
import com.example.data.model.User
import com.example.data.model.UserRole

class Converters {
    @TypeConverter
    fun fromUserRole(value: UserRole): String = value.name

    @TypeConverter
    fun toUserRole(value: String): UserRole = try {
        UserRole.valueOf(value)
    } catch (_: Exception) {
        UserRole.STAFF
    }
}

@Database(
    entities = [
        School::class,
        User::class,
        StockItem::class,
        Supplier::class,
        Student::class,
        StockTransaction::class,
        SaleOrder::class,
        SaleOrderItem::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun schoolDao(): SchoolDao
    abstract fun userDao(): UserDao
    abstract fun stockItemDao(): StockItemDao
    abstract fun supplierDao(): SupplierDao
    abstract fun studentDao(): StudentDao
    abstract fun stockTransactionDao(): StockTransactionDao
    abstract fun saleOrderDao(): SaleOrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "learnium_inventory.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
