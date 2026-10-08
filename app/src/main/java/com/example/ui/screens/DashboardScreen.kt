package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ProductionQuantityLimits
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ItemCategory
import com.example.data.model.SaleWithItems
import com.example.data.model.School
import com.example.data.model.StockItem
import com.example.data.model.StockTransaction
import com.example.data.model.User
import com.example.ui.components.CategoryBadge
import com.example.ui.components.StatCard
import com.example.ui.components.StockStatusBadge
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SchoolGold
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningOrange
import com.example.util.FormatHelper
import java.util.Calendar

@Composable
fun DashboardScreen(
    school: School?,
    currentUser: User?,
    stockItems: List<StockItem>,
    sales: List<SaleWithItems>,
    transactions: List<StockTransaction>,
    currencySymbol: String = "₹",
    onNavigateToStock: (categoryFilter: String?, stockStatusFilter: String?) -> Unit,
    onNavigateToStockIn: () -> Unit,
    onNavigateToStockOut: () -> Unit,
    onAddNewItem: () -> Unit,
    onExportExcel: () -> Unit,
    onViewSaleReceipt: (SaleWithItems) -> Unit
) {
    // Computations
    val totalItemsCount = stockItems.size
    val totalQuantityCount = stockItems.sumOf { it.currentStock }
    val lowStockItems = stockItems.filter { it.isLowStock }
    val outOfStockItems = stockItems.filter { it.isOutOfStock }

    // Today's boundaries
    val cal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val startOfToday = cal.timeInMillis

    val todaySales = sales.filter { it.order.dateMillis >= startOfToday }
    val todayRevenue = todaySales.sumOf { it.order.netAmount }
    val totalBooks = stockItems.filter { it.category == ItemCategory.BOOKS.name }.sumOf { it.currentStock }
    val totalUniforms = stockItems.filter { it.category == ItemCategory.UNIFORM.name }.sumOf { it.currentStock }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // School ERP Welcome Header Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_school_banner"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SchoolNavy),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(SchoolGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = SchoolNavy,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = school?.name ?: "Learnium International School",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Central Stock & Inventory ERP • Session: ${school?.academicSession ?: "2026-2027"}",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Active Staff: ${currentUser?.fullName ?: "Administrator"} (${currentUser?.role?.displayName ?: "Super Admin"})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = SchoolGold
                        )
                    }
                }
            }
        }

        // Quick Action Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onNavigateToStockIn,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Stock In", fontSize = 12.sp)
                }

                Button(
                    onClick = onNavigateToStockOut,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Issue / Sell", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onAddNewItem,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Item", fontSize = 12.sp)
                }
            }
        }

        // KPI Stat Cards Grid Row 1 (Core Stock)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Total Stock Items",
                    value = "$totalItemsCount",
                    subtitle = "Unique catalogue SKUs",
                    icon = Icons.Default.Inventory2,
                    color = SchoolNavy,
                    onClick = { onNavigateToStock("ALL", "ALL") },
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Total Stock Units",
                    value = "$totalQuantityCount",
                    subtitle = "Items in store",
                    icon = Icons.Default.ProductionQuantityLimits,
                    color = Color(0xFF0284C7),
                    onClick = { onNavigateToStock("ALL", "ALL") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // KPI Stat Cards Grid Row 2 (Low / Out of Stock Alerts)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Low Stock Items",
                    value = "${lowStockItems.size}",
                    subtitle = "Below reorder level",
                    icon = Icons.Default.Warning,
                    color = WarningOrange,
                    onClick = { onNavigateToStock("ALL", "LOW_STOCK") },
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Out of Stock",
                    value = "${outOfStockItems.size}",
                    subtitle = "Needs urgent purchase",
                    icon = Icons.Default.Warning,
                    color = DangerRed,
                    onClick = { onNavigateToStock("ALL", "OUT_OF_STOCK") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // KPI Stat Cards Grid Row 3 (Today's Sales & Revenue)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Today's Issues / Sales",
                    value = "${todaySales.size}",
                    subtitle = "${todaySales.sumOf { it.items.sumOf { item -> item.quantity } }} units issued",
                    icon = Icons.Default.ReceiptLong,
                    color = Color(0xFF7C3AED),
                    onClick = onNavigateToStockOut,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Today's Revenue",
                    value = FormatHelper.formatCurrency(todayRevenue, currencySymbol),
                    subtitle = "Collected today",
                    icon = Icons.Default.Payments,
                    color = SuccessGreen,
                    onClick = onNavigateToStockOut,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // KPI Stat Cards Grid Row 4 (Books & Uniform breakdown)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Total Books Stock",
                    value = "$totalBooks",
                    subtitle = "Textbooks & reference",
                    icon = Icons.Default.MenuBook,
                    color = Color(0xFF0369A1),
                    onClick = { onNavigateToStock(ItemCategory.BOOKS.name, "ALL") },
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Total Uniforms Stock",
                    value = "$totalUniforms",
                    subtitle = "Shirts, trousers, skirts",
                    icon = Icons.Default.ShoppingBag,
                    color = Color(0xFFB45309),
                    onClick = { onNavigateToStock(ItemCategory.UNIFORM.name, "ALL") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Low Stock & Out of Stock Alerts Section
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = WarningOrange,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Stock Restock Alerts (${lowStockItems.size + outOfStockItems.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        TextButton(onClick = { onNavigateToStock("ALL", "LOW_STOCK") }) {
                            Text("View All")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val criticalItems = (outOfStockItems + lowStockItems).take(4)
                    if (criticalItems.isEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Slate100, RoundedCornerShape(10.dp))
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "All stock levels are currently healthy! No items below minimum threshold.",
                                fontSize = 12.sp,
                                color = Slate600
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            criticalItems.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.name,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = Slate800
                                        )
                                        Text(
                                            text = "${item.category} • SKU: ${item.sku} • Min Level: ${item.minStockLevel}",
                                            fontSize = 11.sp,
                                            color = Slate600
                                        )
                                    }
                                    StockStatusBadge(item)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Recent Transactions Section (Recent Sales & Stock In)
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Transactions & Issues",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        TextButton(onClick = onNavigateToStockOut) {
                            Text("View All")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val recentSales = sales.take(5)
                    if (recentSales.isEmpty()) {
                        Text(text = "No recent transactions found.", color = Slate600, fontSize = 13.sp)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            recentSales.forEach { sale ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onViewSaleReceipt(sale) }
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${sale.order.receiptNumber} • ${sale.order.studentName}",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = Slate800
                                        )
                                        Text(
                                            text = "${FormatHelper.formatDate(sale.order.dateMillis)} • ${sale.order.paymentMethod}",
                                            fontSize = 11.sp,
                                            color = Slate600
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = FormatHelper.formatCurrency(sale.order.netAmount, currencySymbol),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = SchoolNavy
                                        )
                                        Text(
                                            text = "${sale.items.sumOf { it.quantity }} items",
                                            fontSize = 11.sp,
                                            color = Slate600
                                        )
                                    }
                                }
                                HorizontalDivider(thickness = 0.5.dp, color = Slate100)
                            }
                        }
                    }
                }
            }
        }
    }
}
