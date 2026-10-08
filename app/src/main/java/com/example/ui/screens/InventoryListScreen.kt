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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ItemCategory
import com.example.data.model.StockItem
import com.example.data.model.User
import com.example.ui.components.CategoryBadge
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.StockStatusBadge
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import com.example.ui.theme.SuccessGreen
import com.example.util.FormatHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryListScreen(
    items: List<StockItem>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    stockStatusFilter: String,
    onStockStatusFilterChange: (String) -> Unit,
    selectedClass: String,
    onClassChange: (String) -> Unit,
    currentUser: User?,
    currencySymbol: String = "₹",
    onAddItem: () -> Unit,
    onEditItem: (StockItem) -> Unit,
    onDeleteItem: (StockItem) -> Unit,
    onQuickStockIn: (StockItem) -> Unit,
    onExportExcel: () -> Unit
) {
    var itemToDelete by remember { mutableStateOf<StockItem?>(null) }
    var classDropdownExpanded by remember { mutableStateOf(false) }

    val categoryTabs = listOf(
        Pair("ALL", "All Stock"),
        Pair(ItemCategory.UNIFORM.name, "Uniforms"),
        Pair(ItemCategory.SHOES.name, "Shoes"),
        Pair(ItemCategory.ACCESSORIES.name, "Accessories"),
        Pair(ItemCategory.BOOKS.name, "Books"),
        Pair(ItemCategory.COPIES.name, "Notebooks / Copies"),
        Pair(ItemCategory.CUSTOM.name, "Custom Items")
    )

    val classOptions = listOf("All", "Nursery", "LKG", "UKG", "Class 1", "Class 2", "Class 3", "Class 4", "Class 5", "Class 6", "Class 7", "Class 8", "Class 9", "Class 10", "Class 11", "Class 12")

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Category Tabs Row
            item {
                Spacer(modifier = Modifier.height(10.dp))
                ScrollableTabRow(
                    selectedTabIndex = categoryTabs.indexOfFirst { it.first == selectedCategory }.coerceAtLeast(0),
                    edgePadding = 0.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = SchoolNavy
                ) {
                    categoryTabs.forEach { (catKey, catLabel) ->
                        Tab(
                            selected = selectedCategory == catKey,
                            onClick = { onCategoryChange(catKey) },
                            text = { Text(text = catLabel, fontWeight = if (selectedCategory == catKey) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }
            }

            // Search Bar & Filter Options
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            placeholder = { Text("Search by item name, SKU, ISBN, subject...") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                            trailingIcon = {
                                if (searchQuery.isNotBlank()) {
                                    IconButton(onClick = { onSearchQueryChange("") }) {
                                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("inventory_search_bar"),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Filter Chips Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LazyRow(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                item {
                                    FilterChip(
                                        selected = stockStatusFilter == "ALL",
                                        onClick = { onStockStatusFilterChange("ALL") },
                                        label = { Text("All Status") }
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = stockStatusFilter == "LOW_STOCK",
                                        onClick = { onStockStatusFilterChange("LOW_STOCK") },
                                        label = { Text("Low Stock") }
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = stockStatusFilter == "OUT_OF_STOCK",
                                        onClick = { onStockStatusFilterChange("OUT_OF_STOCK") },
                                        label = { Text("Out of Stock") }
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = stockStatusFilter == "IN_STOCK",
                                        onClick = { onStockStatusFilterChange("IN_STOCK") },
                                        label = { Text("In Stock") }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            OutlinedButton(
                                onClick = onExportExcel,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Excel", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Results summary
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Stock Items (${items.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Slate800
                    )
                    Text(
                        text = "Total Units: ${items.sumOf { it.currentStock }}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Slate600
                    )
                }
            }

            // Stock Items List
            if (items.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No inventory items found matching your criteria.", color = Slate600)
                                Spacer(modifier = Modifier.height(10.dp))
                                TextButton(onClick = onAddItem) {
                                    Text("+ Add New Item Now")
                                }
                            }
                        }
                    }
                }
            } else {
                items(items) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEditItem(item) }
                            .testTag("stock_item_${item.sku}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Top Row: Category badge, Name, Stock status badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CategoryBadge(item.category)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "SKU: ${item.sku}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Slate600
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate800
                                    )
                                }

                                StockStatusBadge(item)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Attributes Pill List: Class, Size, Color, Subject/Publisher
                            val tags = mutableListOf<String>()
                            if (item.targetClass.isNotBlank() && item.targetClass != "All") tags.add("Class: ${item.targetClass}")
                            if (item.size.isNotBlank()) tags.add("Size: ${item.size}")
                            if (item.color.isNotBlank()) tags.add(item.color)
                            if (item.subject.isNotBlank()) tags.add(item.subject)
                            if (item.brandOrPublisher.isNotBlank()) tags.add(item.brandOrPublisher)
                            if (item.pages > 0) tags.add("${item.pages} pgs")

                            if (tags.isNotEmpty()) {
                                Text(
                                    text = tags.joinToString(" • "),
                                    fontSize = 11.sp,
                                    color = Slate600
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                            }

                            HorizontalDivider(thickness = 0.5.dp, color = Slate100)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Bottom Row: Price Info & Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Sell: ${FormatHelper.formatCurrency(item.sellingPrice, currencySymbol)}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = SchoolNavy
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Cost: ${FormatHelper.formatCurrency(item.purchasePrice, currencySymbol)}",
                                            fontSize = 11.sp,
                                            color = Slate600
                                        )
                                    }
                                    Text(
                                        text = "Supplier: ${item.supplierName.ifBlank { "Direct" }} • Min Alert: ${item.minStockLevel}",
                                        fontSize = 10.sp,
                                        color = Slate600
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = { onQuickStockIn(item) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AddBusiness,
                                            contentDescription = "Stock In",
                                            tint = SuccessGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { onEditItem(item) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            tint = SchoolNavy,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    if (currentUser?.role?.canDeleteStock() != false) {
                                        IconButton(
                                            onClick = { itemToDelete = item },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = DangerRed,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp)) // padding for FAB
            }
        }

        // Add Item Floating Action Button
        FloatingActionButton(
            onClick = onAddItem,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_item_fab"),
            containerColor = SchoolNavy,
            contentColor = androidx.compose.ui.graphics.Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Stock Item")
        }
    }

    // Confirmation dialog before deleting
    itemToDelete?.let { item ->
        ConfirmDeleteDialog(
            title = "Delete '${item.name}'?",
            message = "Are you sure you want to delete this stock item? Current stock is ${item.currentStock} units. This action cannot be undone.",
            onConfirm = {
                onDeleteItem(item)
                itemToDelete = null
            },
            onDismiss = { itemToDelete = null }
        )
    }
}
