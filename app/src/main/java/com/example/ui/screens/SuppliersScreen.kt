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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.StockTransaction
import com.example.data.model.Supplier
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import com.example.ui.theme.SuccessGreen
import com.example.util.FormatHelper

@Composable
fun SuppliersScreen(
    suppliers: List<Supplier>,
    transactions: List<StockTransaction>,
    currencySymbol: String = "₹",
    onSaveSupplier: (Supplier, Boolean) -> Unit,
    onDeleteSupplier: (Supplier) -> Unit,
    onExportSuppliers: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var editingSupplier by remember { mutableStateOf<Supplier?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var supplierToDelete by remember { mutableStateOf<Supplier?>(null) }
    var selectedSupplierForHistory by remember { mutableStateOf<Supplier?>(null) }

    val filtered = suppliers.filter {
        searchQuery.isBlank() ||
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.contactPerson.contains(searchQuery, ignoreCase = true) ||
            it.gstNumber.contains(searchQuery, ignoreCase = true) ||
            it.phone.contains(searchQuery, ignoreCase = true)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header & Search
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Suppliers & Vendors (${suppliers.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedButton(onClick = onExportSuppliers) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export CSV")
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search supplier name, contact, phone, GST...") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Supplier List
            if (filtered.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Box(modifier = Modifier.padding(32.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No suppliers found.", color = Slate600)
                        }
                    }
                }
            } else {
                items(filtered) { supplier ->
                    val suppTransactions = transactions.filter { it.supplierId == supplier.id }
                    val totalPurchased = suppTransactions.sumOf { it.totalCost }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSupplierForHistory = supplier },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(SchoolNavy.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(imageVector = Icons.Default.Business, contentDescription = null, tint = SchoolNavy)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = supplier.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Slate800
                                        )
                                        Text(
                                            text = "Contact: ${supplier.contactPerson.ifBlank { "N/A" }} • Tel: ${supplier.phone.ifBlank { "N/A" }}",
                                            fontSize = 12.sp,
                                            color = Slate600
                                        )
                                    }
                                }

                                Row {
                                    IconButton(onClick = {
                                        editingSupplier = supplier
                                        showDialog = true
                                    }) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = SchoolNavy, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { supplierToDelete = supplier }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(thickness = 0.5.dp, color = Slate100)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "GST: ${supplier.gstNumber.ifBlank { "Not provided" }}",
                                    fontSize = 11.sp,
                                    color = Slate600
                                )
                                Text(
                                    text = "Total Purchases: ${FormatHelper.formatCurrency(totalPurchased, currencySymbol)} (${suppTransactions.size} orders)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Add Supplier FAB
        FloatingActionButton(
            onClick = {
                editingSupplier = null
                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_supplier_fab"),
            containerColor = SchoolNavy,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Supplier")
        }
    }

    // Add / Edit Supplier Dialog
    if (showDialog) {
        var name by remember { mutableStateOf(editingSupplier?.name ?: "") }
        var contactPerson by remember { mutableStateOf(editingSupplier?.contactPerson ?: "") }
        var phone by remember { mutableStateOf(editingSupplier?.phone ?: "") }
        var email by remember { mutableStateOf(editingSupplier?.email ?: "") }
        var address by remember { mutableStateOf(editingSupplier?.address ?: "") }
        var gstNumber by remember { mutableStateOf(editingSupplier?.gstNumber ?: "") }
        var notes by remember { mutableStateOf(editingSupplier?.notes ?: "") }

        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth(0.95f)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (editingSupplier == null) "Add New Supplier" else "Edit Supplier Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SchoolNavy
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Supplier / Company Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = contactPerson,
                            onValueChange = { contactPerson = it },
                            label = { Text("Contact Person") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = gstNumber,
                            onValueChange = { gstNumber = it },
                            label = { Text("GST Number") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address / City") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Supply Notes / Specialization") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        OutlinedButton(onClick = { showDialog = false }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (name.isNotBlank()) {
                                    val s = Supplier(
                                        id = editingSupplier?.id ?: 0L,
                                        schoolId = editingSupplier?.schoolId ?: "",
                                        name = name.trim(),
                                        contactPerson = contactPerson.trim(),
                                        phone = phone.trim(),
                                        email = email.trim(),
                                        address = address.trim(),
                                        gstNumber = gstNumber.trim(),
                                        notes = notes.trim()
                                    )
                                    onSaveSupplier(s, editingSupplier == null)
                                    showDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy)
                        ) {
                            Text("Save Supplier")
                        }
                    }
                }
            }
        }
    }

    // Supplier Purchases History Modal
    selectedSupplierForHistory?.let { supplier ->
        val suppTrans = transactions.filter { it.supplierId == supplier.id }

        Dialog(onDismissRequest = { selectedSupplierForHistory = null }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(vertical = 20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Purchase History: ${supplier.name}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SchoolNavy
                            )
                            Text(
                                text = "Total Inward Supply: ${FormatHelper.formatCurrency(suppTrans.sumOf { it.totalCost }, currencySymbol)}",
                                fontSize = 12.sp,
                                color = Slate600
                            )
                        }
                        IconButton(onClick = { selectedSupplierForHistory = null }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (suppTrans.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("No stock-in purchase transactions recorded for this supplier.", color = Slate600, fontSize = 13.sp)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.height(300.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(suppTrans) { t ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Slate100),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = t.itemName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(text = FormatHelper.formatCurrency(t.totalCost, currencySymbol), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                        Text(text = "${FormatHelper.formatDate(t.dateMillis)} • Qty: ${t.quantity} • Rate: ${FormatHelper.formatCurrency(t.unitCost, currencySymbol)}", fontSize = 11.sp, color = Slate600)
                                        if (t.invoiceNumber.isNotBlank()) {
                                            Text(text = "Invoice: ${t.invoiceNumber} • Recipient: ${t.staffName}", fontSize = 10.sp, color = Slate600)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation
    supplierToDelete?.let { supplier ->
        ConfirmDeleteDialog(
            title = "Delete Supplier?",
            message = "Are you sure you want to remove supplier '${supplier.name}'? Existing stock items will retain supplier name.",
            onConfirm = {
                onDeleteSupplier(supplier)
                supplierToDelete = null
            },
            onDismiss = { supplierToDelete = null }
        )
    }
}
