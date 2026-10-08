package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.SaleWithItems
import com.example.data.model.StockItem
import com.example.data.model.StockTransaction
import com.example.data.model.Student
import com.example.data.model.Supplier
import java.io.File
import java.io.FileOutputStream

object ExportHelper {

    private fun escapeCsv(value: Any?): String {
        if (value == null) return "\"\""
        val str = value.toString()
        return "\"" + str.replace("\"", "\"\"") + "\""
    }

    fun generateStockCsv(items: List<StockItem>): String {
        val sb = StringBuilder()
        sb.append("Item ID,Name,Category,Sub Category,SKU,Class,Gender,Size,Purchase Price,Selling Price,Opening Stock,Current Stock,Min Stock,Status,Supplier\n")
        for (item in items) {
            val status = when {
                item.isOutOfStock -> "Out of Stock"
                item.isLowStock -> "Low Stock"
                else -> "In Stock"
            }
            sb.append(escapeCsv(item.id)).append(",")
            sb.append(escapeCsv(item.name)).append(",")
            sb.append(escapeCsv(item.category)).append(",")
            sb.append(escapeCsv(item.subCategory)).append(",")
            sb.append(escapeCsv(item.sku)).append(",")
            sb.append(escapeCsv(item.targetClass)).append(",")
            sb.append(escapeCsv(item.gender)).append(",")
            sb.append(escapeCsv(item.size)).append(",")
            sb.append(escapeCsv(item.purchasePrice)).append(",")
            sb.append(escapeCsv(item.sellingPrice)).append(",")
            sb.append(escapeCsv(item.openingStock)).append(",")
            sb.append(escapeCsv(item.currentStock)).append(",")
            sb.append(escapeCsv(item.minStockLevel)).append(",")
            sb.append(escapeCsv(status)).append(",")
            sb.append(escapeCsv(item.supplierName)).append("\n")
        }
        return sb.toString()
    }

    fun generateSalesCsv(sales: List<SaleWithItems>): String {
        val sb = StringBuilder()
        sb.append("Receipt Number,Date,Student ID,Student Name,Class,Section,Items Count,Total Amount,Discount,Net Amount,Payment Method,Staff\n")
        for (sale in sales) {
            sb.append(escapeCsv(sale.order.receiptNumber)).append(",")
            sb.append(escapeCsv(FormatHelper.formatDate(sale.order.dateMillis))).append(",")
            sb.append(escapeCsv(sale.order.studentId)).append(",")
            sb.append(escapeCsv(sale.order.studentName)).append(",")
            sb.append(escapeCsv(sale.order.className)).append(",")
            sb.append(escapeCsv(sale.order.section)).append(",")
            sb.append(escapeCsv(sale.items.sumOf { it.quantity })).append(",")
            sb.append(escapeCsv(sale.order.totalAmount)).append(",")
            sb.append(escapeCsv(sale.order.discountAmount)).append(",")
            sb.append(escapeCsv(sale.order.netAmount)).append(",")
            sb.append(escapeCsv(sale.order.paymentMethod)).append(",")
            sb.append(escapeCsv(sale.order.staffName)).append("\n")
        }
        return sb.toString()
    }

    fun generatePurchasesCsv(transactions: List<StockTransaction>): String {
        val sb = StringBuilder()
        sb.append("Transaction ID,Type,Date,Item Name,Category,Supplier,Quantity,Unit Cost,Total Cost,Invoice Number,Staff\n")
        for (t in transactions) {
            sb.append(escapeCsv(t.id)).append(",")
            sb.append(escapeCsv(t.type)).append(",")
            sb.append(escapeCsv(FormatHelper.formatDate(t.dateMillis))).append(",")
            sb.append(escapeCsv(t.itemName)).append(",")
            sb.append(escapeCsv(t.category)).append(",")
            sb.append(escapeCsv(t.supplierName)).append(",")
            sb.append(escapeCsv(t.quantity)).append(",")
            sb.append(escapeCsv(t.unitCost)).append(",")
            sb.append(escapeCsv(t.totalCost)).append(",")
            sb.append(escapeCsv(t.invoiceNumber)).append(",")
            sb.append(escapeCsv(t.staffName)).append("\n")
        }
        return sb.toString()
    }

    fun generateStudentsCsv(students: List<Student>): String {
        val sb = StringBuilder()
        sb.append("Student ID,Full Name,Class,Section,Parent Name,Parent Phone\n")
        for (s in students) {
            sb.append(escapeCsv(s.studentId)).append(",")
            sb.append(escapeCsv(s.name)).append(",")
            sb.append(escapeCsv(s.className)).append(",")
            sb.append(escapeCsv(s.section)).append(",")
            sb.append(escapeCsv(s.parentName)).append(",")
            sb.append(escapeCsv(s.parentPhone)).append("\n")
        }
        return sb.toString()
    }

    fun generateSuppliersCsv(suppliers: List<Supplier>): String {
        val sb = StringBuilder()
        sb.append("Supplier Name,Contact Person,Phone,Email,Address,GST Number,Notes\n")
        for (s in suppliers) {
            sb.append(escapeCsv(s.name)).append(",")
            sb.append(escapeCsv(s.contactPerson)).append(",")
            sb.append(escapeCsv(s.phone)).append(",")
            sb.append(escapeCsv(s.email)).append(",")
            sb.append(escapeCsv(s.address)).append(",")
            sb.append(escapeCsv(s.gstNumber)).append(",")
            sb.append(escapeCsv(s.notes)).append("\n")
        }
        return sb.toString()
    }

    fun shareCsv(context: Context, fileName: String, csvContent: String) {
        try {
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()
            val file = File(exportDir, fileName)
            FileOutputStream(file).use { it.write(csvContent.toByteArray(Charsets.UTF_8)) }

            val uri: Uri = try {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            } catch (_: Exception) {
                Uri.fromFile(file)
            }

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_SUBJECT, fileName)
                putExtra(Intent.EXTRA_TEXT, "Exported $fileName from Learnium School Inventory System")
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Export / Open $fileName"))
        } catch (_: Exception) {
            // Fallback: share plain text
            val textIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, fileName)
                putExtra(Intent.EXTRA_TEXT, csvContent)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(textIntent, "Export $fileName"))
        }
    }
}
