package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.ItemCategory
import com.example.data.model.StockItem
import com.example.data.model.UserRole
import com.example.util.ExportHelper
import com.example.util.FormatHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun readStringFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Learnium Inventory", appName)
    }

    @Test
    fun testStockAlertCalculations() {
        val normalItem = StockItem(
            schoolId = "learnium_intl",
            name = "School Shirt",
            category = ItemCategory.UNIFORM.name,
            sku = "LIS-UNF-101",
            purchasePrice = 250.0,
            sellingPrice = 350.0,
            currentStock = 50,
            minStockLevel = 15
        )
        assertFalse(normalItem.isLowStock)
        assertFalse(normalItem.isOutOfStock)

        val lowItem = normalItem.copy(currentStock = 10, minStockLevel = 15)
        assertTrue(lowItem.isLowStock)
        assertFalse(lowItem.isOutOfStock)

        val outOfStockItem = normalItem.copy(currentStock = 0)
        assertTrue(outOfStockItem.isOutOfStock)
        assertFalse(outOfStockItem.isLowStock)
    }

    @Test
    fun testUserPermissions() {
        assertTrue(UserRole.SUPER_ADMIN.canManageUsers())
        assertTrue(UserRole.ADMIN.canManageUsers())
        assertFalse(UserRole.STORE_MANAGER.canManageUsers())
        assertFalse(UserRole.STAFF.canManageUsers())

        assertTrue(UserRole.STORE_MANAGER.canStockIn())
        assertTrue(UserRole.STAFF.canStockOut())
    }

    @Test
    fun testCsvExportGeneration() {
        val items = listOf(
            StockItem(
                id = 1,
                schoolId = "learnium_intl",
                name = "NCERT Math 10",
                category = ItemCategory.BOOKS.name,
                sku = "LIS-BOK-001",
                purchasePrice = 130.0,
                sellingPrice = 160.0,
                currentStock = 20,
                minStockLevel = 10
            )
        )
        val csv = ExportHelper.generateStockCsv(items)
        assertTrue(csv.contains("NCERT Math 10"))
        assertTrue(csv.contains("LIS-BOK-001"))
        assertTrue(csv.contains("In Stock"))
    }

    @Test
    fun testFormatCurrency() {
        val formatted = FormatHelper.formatCurrency(1250.0, "₹")
        assertTrue(formatted.contains("₹"))
        assertTrue(formatted.contains("1,250") || formatted.contains("1250"))
    }
}
