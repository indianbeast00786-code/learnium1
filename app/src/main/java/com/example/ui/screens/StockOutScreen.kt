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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PaymentMethod
import com.example.data.model.SaleOrder
import com.example.data.model.SaleWithItems
import com.example.data.model.StockItem
import com.example.data.model.Student
import com.example.ui.components.CategoryBadge
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
fun StockOutScreen(
    stockItems: List<StockItem>,
    students: List<Student>,
    sales: List<SaleWithItems>,
    currencySymbol: String = "₹",
    onCompleteSale: (Student?, String, String, List<Pair<StockItem, Int>>, Double, PaymentMethod, String, (SaleOrder) -> Unit) -> Unit,
    onViewReceipt: (SaleWithItems) -> Unit,
    onCancelSale: (SaleWithItems) -> Unit,
    onExportSales: () -> Unit
) {
    var selectedStudent by remember { mutableStateOf<Student?>(null) }
    var customStudentName by remember { mutableStateOf("") }
    var customClass by remember { mutableStateOf("") }
    var discountText by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    var saleNotes by remember { mutableStateOf("") }

    // Multi-item cart
    val cartItems = remember { mutableStateListOf<Pair<StockItem, Int>>() }

    var studentDropdownExpanded by remember { mutableStateOf(false) }
    var itemSelectorExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Issue / Sale Form Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(SchoolNavy.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddShoppingCart,
                                contentDescription = null,
                                tint = SchoolNavy
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Issue Items / Student Sale",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Issue uniform, books, shoes, copies to students and generate receipt",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Student Selector Dropdown
                    ExposedDropdownMenuBox(
                        expanded = studentDropdownExpanded,
                        onExpandedChange = { studentDropdownExpanded = !studentDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedStudent?.let { "${it.name} (${it.studentId}) - ${it.className} ${it.section}" }
                                ?: customStudentName.ifBlank { "" },
                            onValueChange = {
                                customStudentName = it
                                selectedStudent = null
                            },
                            label = { Text("Student (Select Registered or Enter Name)") },
                            placeholder = { Text("Select student or type name") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = studentDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("student_selector_input")
                        )
                        ExposedDropdownMenu(
                            expanded = studentDropdownExpanded,
                            onDismissRequest = { studentDropdownExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Walk-in / Custom Student (Type manually)") },
                                onClick = {
                                    selectedStudent = null
                                    studentDropdownExpanded = false
                                }
                            )
                            students.forEach { s ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(text = s.name, fontWeight = FontWeight.SemiBold)
                                            Text(text = "ID: ${s.studentId} • Class: ${s.className} ${s.section}", fontSize = 11.sp, color = Slate600)
                                        }
                                    },
                                    onClick = {
                                        selectedStudent = s
                                        customStudentName = s.name
                                        customClass = s.className
                                        studentDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (selectedStudent == null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = customClass,
                            onValueChange = { customClass = it },
                            label = { Text("Class / Grade") },
                            placeholder = { Text("e.g. Class 8 A") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = Slate200)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Add Items Section
                    Text(
                        text = "Selected Items to Issue / Sell",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Cart Items list
                    if (cartItems.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Slate100, RoundedCornerShape(10.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No items added to cart yet.", color = Slate600, fontSize = 13.sp)
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            cartItems.forEachIndexed { index, (item, qty) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1.5f)) {
                                        Text(text = item.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        Text(
                                            text = "${item.category} • Rate: ${FormatHelper.formatCurrency(item.sellingPrice, currencySymbol)}",
                                            fontSize = 11.sp,
                                            color = Slate600
                                        )
                                    }

                                    // Quantity Stepper
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                if (qty > 1) {
                                                    cartItems[index] = Pair(item, qty - 1)
                                                } else {
                                                    cartItems.removeAt(index)
                                                }
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                                        }

                                        Text(text = "$qty", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                                        IconButton(
                                            onClick = {
                                                if (qty < item.currentStock) {
                                                    cartItems[index] = Pair(item, qty + 1)
                                                } else {
                                                    errorMessage = "Cannot exceed available stock (${item.currentStock}) for ${item.name}"
                                                }
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = FormatHelper.formatCurrency(item.sellingPrice * qty, currencySymbol),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = SchoolNavy
                                    )

                                    IconButton(
                                        onClick = { cartItems.removeAt(index) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = DangerRed, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Item Picker Dropdown
                    ExposedDropdownMenuBox(
                        expanded = itemSelectorExpanded,
                        onExpandedChange = { itemSelectorExpanded = !itemSelectorExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { itemSelectorExpanded = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_item_to_cart_button")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Select Stock Item to Add")
                        }

                        ExposedDropdownMenu(
                            expanded = itemSelectorExpanded,
                            onDismissRequest = { itemSelectorExpanded = false }
                        ) {
                            stockItems.forEach { item ->
                                val inCart = cartItems.any { it.first.id == item.id }
                                DropdownMenuItem(
                                    enabled = item.currentStock > 0 && !inCart,
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(text = item.name, fontWeight = FontWeight.Medium)
                                                Text(
                                                    text = "${item.category} • SKU: ${item.sku} • Stock: ${item.currentStock}",
                                                    fontSize = 11.sp,
                                                    color = Slate600
                                                )
                                            }
                                            Text(
                                                text = FormatHelper.formatCurrency(item.sellingPrice, currencySymbol),
                                                fontWeight = FontWeight.Bold,
                                                color = SchoolNavy
                                            )
                                        }
                                    },
                                    onClick = {
                                        cartItems.add(Pair(item, 1))
                                        itemSelectorExpanded = false
                                        errorMessage = null
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Payment Method Chips
                    Text(
                        text = "Payment Method",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate800
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PaymentMethod.entries.forEach { pm ->
                            FilterChip(
                                selected = paymentMethod == pm,
                                onClick = { paymentMethod = pm },
                                label = { Text(pm.displayName, fontSize = 12.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Discount & Notes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = discountText,
                            onValueChange = { discountText = it },
                            label = { Text("Discount ($currencySymbol)") },
                            placeholder = { Text("0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = saleNotes,
                            onValueChange = { saleNotes = it },
                            label = { Text("Receipt Notes") },
                            placeholder = { Text("Optional notes") },
                            modifier = Modifier.weight(1.5f),
                            singleLine = true
                        )
                    }

                    // Calculation Summary Card
                    val subtotal = cartItems.sumOf { (item, qty) -> item.sellingPrice * qty }
                    val discount = discountText.toDoubleOrNull() ?: 0.0
                    val grandTotal = (subtotal - discount).coerceAtLeast(0.0)

                    if (cartItems.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Subtotal (${cartItems.sumOf { it.second }} items):", fontSize = 13.sp)
                                    Text(text = FormatHelper.formatCurrency(subtotal, currencySymbol), fontSize = 13.sp)
                                }
                                if (discount > 0) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "Discount:", fontSize = 13.sp, color = SuccessGreen)
                                        Text(text = "-${FormatHelper.formatCurrency(discount, currencySymbol)}", fontSize = 13.sp, color = SuccessGreen)
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                HorizontalDivider(thickness = 1.dp, color = Slate200)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "TOTAL PAYABLE:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SchoolNavy)
                                    Text(
                                        text = FormatHelper.formatCurrency(grandTotal, currencySymbol),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp,
                                        color = SchoolNavy
                                    )
                                }
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
                            if (cartItems.isEmpty()) {
                                errorMessage = "Please add at least one item to cart."
                                return@Button
                            }
                            val sName = selectedStudent?.name ?: customStudentName.ifBlank { "Walk-in Student" }
                            val sClass = selectedStudent?.className ?: customClass

                            errorMessage = null
                            onCompleteSale(
                                selectedStudent,
                                sName,
                                sClass,
                                cartItems.toList(),
                                discount,
                                paymentMethod,
                                saleNotes.trim()
                            ) { newOrder ->
                                // Reset form
                                cartItems.clear()
                                discountText = ""
                                saleNotes = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("complete_sale_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy)
                    ) {
                        Icon(imageVector = Icons.Default.Receipt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate Bill & Complete Issue")
                    }
                }
            }
        }

        // Sales & Student Issue History
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sales & Issue History (${sales.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedButton(onClick = onExportSales) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export Sales")
                }
            }
        }

        if (sales.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.padding(32.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No sales or issues recorded yet.", color = Slate600)
                    }
                }
            }
        } else {
            items(sales) { s ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onViewReceipt(s) },
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = s.order.receiptNumber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = SchoolNavy
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Slate100)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = s.order.paymentMethod, fontSize = 10.sp, color = Slate600)
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Student: ${s.order.studentName} (${s.order.className}) • ${FormatHelper.formatDate(s.order.dateMillis)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate600
                            )
                            Text(
                                text = "${s.items.size} item types (${s.items.sumOf { it.quantity }} units) • Staff: ${s.order.staffName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate600
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = FormatHelper.formatCurrency(s.order.netAmount, currencySymbol),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Slate800
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row {
                                TextButton(onClick = { onViewReceipt(s) }) {
                                    Text("View Bill", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
