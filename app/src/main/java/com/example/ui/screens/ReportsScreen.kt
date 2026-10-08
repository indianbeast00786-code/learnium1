package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ItemCategory
import com.example.data.model.SaleWithItems
import com.example.data.model.StockItem
import com.example.data.model.StockTransaction
import com.example.ui.components.StatCard
import com.example.ui.theme.SchoolGold
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import com.example.ui.theme.SuccessGreen
import com.example.util.FormatHelper
import java.util.Calendar

@Composable
fun ReportsScreen(
    stockItems: List<StockItem>,
    sales: List<SaleWithItems>,
    transactions: List<StockTransaction>,
    currencySymbol: String = "₹",
    onExportReports: () -> Unit
) {
    var selectedPeriod by remember { mutableStateOf("ALL") } // TODAY, WEEK, MONTH, YEAR, ALL

    val now = System.currentTimeMillis()
    val periodStartMillis = remember(selectedPeriod) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        when (selectedPeriod) {
            "TODAY" -> cal.timeInMillis
            "WEEK" -> {
                cal.add(Calendar.DAY_OF_YEAR, -7)
                cal.timeInMillis
            }
            "MONTH" -> {
                cal.add(Calendar.DAY_OF_YEAR, -30)
                cal.timeInMillis
            }
            "YEAR" -> {
                cal.add(Calendar.DAY_OF_YEAR, -365)
                cal.timeInMillis
            }
            else -> 0L
        }
    }

    val filteredSales = sales.filter { it.order.dateMillis >= periodStartMillis }
    val filteredTransactions = transactions.filter { it.dateMillis >= periodStartMillis }

    // Metrics
    val totalRevenue = filteredSales.sumOf { it.order.netAmount }
    val totalDiscountGiven = filteredSales.sumOf { it.order.discountAmount }
    val totalUnitsSold = filteredSales.sumOf { it.items.sumOf { item -> item.quantity } }
    val totalPurchasesCost = filteredTransactions.sumOf { it.totalCost }

    // COGS estimation (Cost of goods sold based on item's purchase price)
    val estimatedCogs = filteredSales.sumOf { sale ->
        sale.items.sumOf { orderItem ->
            val stock = stockItems.firstOrNull { it.id == orderItem.itemId }
            val unitCost = stock?.purchasePrice ?: (orderItem.unitPrice * 0.7)
            unitCost * orderItem.quantity
        }
    }
    val estimatedGrossProfit = (totalRevenue - estimatedCogs).coerceAtLeast(0.0)
    val profitMargin = if (totalRevenue > 0) (estimatedGrossProfit / totalRevenue) * 100 else 0.0

    // Inventory Valuation
    val totalInventoryUnits = stockItems.sumOf { it.currentStock }
    val inventoryCostValuation = stockItems.sumOf { it.currentStock * it.purchasePrice }
    val inventoryRetailValuation = stockItems.sumOf { it.currentStock * it.sellingPrice }
    val potentialStoreProfit = (inventoryRetailValuation - inventoryCostValuation).coerceAtLeast(0.0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Store Reports & Analytics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Financial audit, profit calculation and inventory valuation",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                }

                OutlinedButton(onClick = onExportReports) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export All")
                }
            }
        }

        // Period filter chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedPeriod == "ALL",
                        onClick = { selectedPeriod = "ALL" },
                        label = { Text("All Time") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedPeriod == "TODAY",
                        onClick = { selectedPeriod = "TODAY" },
                        label = { Text("Today") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedPeriod == "WEEK",
                        onClick = { selectedPeriod = "WEEK" },
                        label = { Text("Last 7 Days") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedPeriod == "MONTH",
                        onClick = { selectedPeriod = "MONTH" },
                        label = { Text("Last 30 Days") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedPeriod == "YEAR",
                        onClick = { selectedPeriod = "YEAR" },
                        label = { Text("This Year") }
                    )
                }
            }
        }

        // Profit & Financial Performance Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SuccessGreen.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null, tint = SuccessGreen)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Gross Profit & Revenue Audit",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Based on ${filteredSales.size} sales receipts in selected period",
                                fontSize = 11.sp,
                                color = Slate600
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Total Sales Revenue", fontSize = 11.sp, color = Slate600)
                            Text(
                                text = FormatHelper.formatCurrency(totalRevenue, currencySymbol),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = SchoolNavy
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Cost of Goods Sold", fontSize = 11.sp, color = Slate600)
                            Text(
                                text = FormatHelper.formatCurrency(estimatedCogs, currencySymbol),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Slate800
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Gross Profit Margin", fontSize = 11.sp, color = Slate600)
                            Text(
                                text = "${String.format("%.1f", profitMargin)}%",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = SuccessGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = Slate100)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Gross Profit Value:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text(
                            text = FormatHelper.formatCurrency(estimatedGrossProfit, currencySymbol),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = SuccessGreen
                        )
                    }
                }
            }
        }

        // Inventory Valuation Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Warehouse Stock Valuation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Total capital invested in current inventory across all categories",
                        fontSize = 11.sp,
                        color = Slate600
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Total On-Hand Units", fontSize = 11.sp, color = Slate600)
                            Text(text = "$totalInventoryUnits units", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "At Purchase Cost", fontSize = 11.sp, color = Slate600)
                            Text(
                                text = FormatHelper.formatCurrency(inventoryCostValuation, currencySymbol),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = SchoolNavy
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "At Retail Selling Price", fontSize = 11.sp, color = Slate600)
                            Text(
                                text = FormatHelper.formatCurrency(inventoryRetailValuation, currencySymbol),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = SuccessGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Potential store margin upon complete sale: ${FormatHelper.formatCurrency(potentialStoreProfit, currencySymbol)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SchoolGold
                    )
                }
            }
        }

        // Category-wise Breakdown Cards
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Stock & Sales by Category",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    ItemCategory.entries.forEach { cat ->
                        val catStockItems = stockItems.filter { it.category == cat.name }
                        val catUnits = catStockItems.sumOf { it.currentStock }
                        val catValue = catStockItems.sumOf { it.currentStock * it.sellingPrice }
                        val progress = if (totalInventoryUnits > 0) catUnits.toFloat() / totalInventoryUnits else 0f

                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = cat.displayName,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = Slate800
                                )
                                Text(
                                    text = "$catUnits units (${FormatHelper.formatCurrency(catValue, currencySymbol)})",
                                    fontSize = 12.sp,
                                    color = Slate600
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = SchoolNavy,
                                trackColor = Slate100
                            )
                        }
                    }
                }
            }
        }
    }
}
