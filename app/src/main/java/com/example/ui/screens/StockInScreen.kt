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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StockItem
import com.example.data.model.StockTransaction
import com.example.data.model.Supplier
import com.example.ui.components.CategoryBadge
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import com.example.ui.theme.SuccessGreen
import com.example.util.FormatHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockInScreen(
    stockItems: List<StockItem>,
    suppliers: List<Supplier>,
    transactions: List<StockTransaction>,
    currencySymbol: String = "₹",
    onSubmitStockIn: (StockItem, Int, Double, String, Long?, String, String) -> Unit,
    onExportPurchases: () -> Unit
) {
    val context = LocalContext.current

    var selectedItem by remember { mutableStateOf<StockItem?>(null) }
    var selectedSupplier by remember { mutableStateOf<Supplier?>(null) }
    var quantityText by remember { mutableStateOf("") }
    var unitCostText by remember { mutableStateOf("") }
    var invoiceNumber by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var itemDropdownExpanded by remember { mutableStateOf(false) }
    var supplierDropdownExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Form Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(SuccessGreen.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = SuccessGreen
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "New Stock In / Inward Purchase",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Receive shipment and automatically increase stock levels",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Select Item Dropdown
                    ExposedDropdownMenuBox(
                        expanded = itemDropdownExpanded,
                        onExpandedChange = { itemDropdownExpanded = !itemDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedItem?.let { "${it.name} (${it.sku}) - Current: ${it.currentStock}" } ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Stock Item *") },
                            placeholder = { Text("Tap to select item") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = itemDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("stock_in_item_selector")
                        )
                        ExposedDropdownMenu(
                            expanded = itemDropdownExpanded,
                            onDismissRequest = { itemDropdownExpanded = false }
                        ) {
                            stockItems.forEach { item ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(text = item.name, fontWeight = FontWeight.Medium)
                                            Text(
                                                text = "${item.category} • SKU: ${item.sku} • Stock: ${item.currentStock}",
                                                fontSize = 11.sp,
                                                color = Slate600
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedItem = item
                                        unitCostText = item.purchasePrice.toString()
                                        // Auto select item's supplier if matched
                                        val supp = suppliers.firstOrNull { it.id == item.supplierId }
                                        if (supp != null) selectedSupplier = supp
                                        itemDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Select Supplier Dropdown
                    ExposedDropdownMenuBox(
                        expanded = supplierDropdownExpanded,
                        onExpandedChange = { supplierDropdownExpanded = !supplierDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedSupplier?.name ?: selectedItem?.supplierName ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Supplier") },
                            placeholder = { Text("Select supplier") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = supplierDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = supplierDropdownExpanded,
                            onDismissRequest = { supplierDropdownExpanded = false }
                        ) {
                            suppliers.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s.name) },
                                    onClick = {
                                        selectedSupplier = s
                                        supplierDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quantity & Unit Cost
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = quantityText,
                            onValueChange = { quantityText = it },
                            label = { Text("Quantity In *") },
                            placeholder = { Text("e.g. 50") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("stock_in_quantity_input"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = unitCostText,
                            onValueChange = { unitCostText = it },
                            label = { Text("Unit Cost ($currencySymbol) *") },
                            placeholder = { Text("e.g. 280") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Invoice Number & Notes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = invoiceNumber,
                            onValueChange = { invoiceNumber = it },
                            label = { Text("Invoice / Bill No.") },
                            placeholder = { Text("e.g. INV-2026-99") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes / Batch") },
                            placeholder = { Text("e.g. Term 1 order") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    // Calculation Total Preview
                    val qty = quantityText.toIntOrNull() ?: 0
                    val cost = unitCostText.toDoubleOrNull() ?: 0.0
                    val totalCost = qty * cost

                    if (qty > 0 && cost > 0.0) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(14.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Inward Cost:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = FormatHelper.formatCurrency(totalCost, currencySymbol),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SchoolNavy
                                )
                            }
                        }
                    }

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            val item = selectedItem
                            if (item == null) {
                                errorMessage = "Please select an item."
                                return@Button
                            }
                            if (qty <= 0) {
                                errorMessage = "Quantity must be greater than 0."
                                return@Button
                            }
                            if (cost <= 0.0) {
                                errorMessage = "Unit cost must be greater than 0."
                                return@Button
                            }

                            errorMessage = null
                            val suppName = selectedSupplier?.name ?: item.supplierName
                            val suppId = selectedSupplier?.id ?: item.supplierId

                            onSubmitStockIn(
                                item,
                                qty,
                                cost,
                                invoiceNumber.trim(),
                                suppId,
                                suppName,
                                notes.trim()
                            )

                            // Clear inputs
                            quantityText = ""
                            invoiceNumber = ""
                            notes = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submit_stock_in_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy)
                    ) {
                        Icon(imageVector = Icons.Default.AddBusiness, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add to Stock (Record Purchase)")
                    }
                }
            }
        }

        // Transactions History Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = SchoolNavy,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Recent Stock In Records (${transactions.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(onClick = onExportPurchases) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export CSV")
                }
            }
        }

        // Transactions History List
        if (transactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.padding(32.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No stock-in records found.", color = Slate600)
                    }
                }
            }
        } else {
            items(transactions) { t ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = t.itemName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = Slate800
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Supplier: ${t.supplierName.ifBlank { "Direct" }} • ${FormatHelper.formatDate(t.dateMillis)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate600
                            )
                            if (t.invoiceNumber.isNotBlank()) {
                                Text(
                                    text = "Invoice: ${t.invoiceNumber} • Recorded by: ${t.staffName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate600
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "+${t.quantity} Units",
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen,
                                fontSize = 14.sp
                            )
                            Text(
                                text = FormatHelper.formatCurrency(t.totalCost, currencySymbol),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = Slate800
                            )
                        }
                    }
                }
            }
        }
    }
}
