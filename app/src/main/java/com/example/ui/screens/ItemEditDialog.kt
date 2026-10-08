package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ItemCategory
import com.example.data.model.StockItem
import com.example.data.model.Supplier
import com.example.ui.theme.SchoolNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemEditDialog(
    initialItem: StockItem?,
    suppliers: List<Supplier>,
    onDismiss: () -> Unit,
    onSave: (StockItem, Boolean) -> Unit
) {
    val isNew = initialItem == null

    var category by remember { mutableStateOf(initialItem?.category ?: ItemCategory.UNIFORM.name) }
    var name by remember { mutableStateOf(initialItem?.name ?: "") }
    var subCategory by remember { mutableStateOf(initialItem?.subCategory ?: "") }
    var sku by remember { mutableStateOf(initialItem?.sku ?: "") }
    var barcode by remember { mutableStateOf(initialItem?.barcode ?: "") }
    var targetClass by remember { mutableStateOf(initialItem?.targetClass ?: "All") }
    var gender by remember { mutableStateOf(initialItem?.gender ?: "Unisex") }
    var size by remember { mutableStateOf(initialItem?.size ?: "") }
    var color by remember { mutableStateOf(initialItem?.color ?: "") }
    var brandOrPublisher by remember { mutableStateOf(initialItem?.brandOrPublisher ?: "") }
    var isbn by remember { mutableStateOf(initialItem?.isbn ?: "") }
    var subject by remember { mutableStateOf(initialItem?.subject ?: "") }
    var edition by remember { mutableStateOf(initialItem?.edition ?: "") }
    var pagesText by remember { mutableStateOf(if (initialItem != null && initialItem.pages > 0) initialItem.pages.toString() else "") }
    var purchasePriceText by remember { mutableStateOf(if (initialItem != null) initialItem.purchasePrice.toString() else "") }
    var sellingPriceText by remember { mutableStateOf(if (initialItem != null) initialItem.sellingPrice.toString() else "") }
    var openingStockText by remember { mutableStateOf(if (initialItem != null) initialItem.openingStock.toString() else "0") }
    var currentStockText by remember { mutableStateOf(if (initialItem != null) initialItem.currentStock.toString() else "0") }
    var minStockText by remember { mutableStateOf(if (initialItem != null) initialItem.minStockLevel.toString() else "10") }
    var selectedSupplierId by remember { mutableStateOf(initialItem?.supplierId) }
    var selectedSupplierName by remember { mutableStateOf(initialItem?.supplierName ?: "") }
    var description by remember { mutableStateOf(initialItem?.description ?: "") }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var supplierDropdownExpanded by remember { mutableStateOf(false) }
    var classDropdownExpanded by remember { mutableStateOf(false) }
    var genderDropdownExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val classOptions = listOf("All", "Nursery", "LKG", "UKG", "Class 1", "Class 2", "Class 3", "Class 4", "Class 5", "Class 6", "Class 7", "Class 8", "Class 9", "Class 10", "Class 11", "Class 12")
    val genderOptions = listOf("Unisex", "Boys", "Girls")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(0.95f),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isNew) "Add New Stock Item" else "Edit Stock Item",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Category Selector
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = ItemCategory.fromString(category).displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Stock Category *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        ItemCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.displayName) },
                                onClick = {
                                    category = cat.name
                                    categoryDropdownExpanded = false
                                    // Auto-generate SKU prefix if blank
                                    if (sku.isBlank()) {
                                        val randCode = (100..999).random()
                                        sku = "LIS-${cat.codePrefix}-$randCode"
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Item Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name *") },
                    placeholder = { Text("e.g. School Shirt Half Sleeve, Oxford Shoes...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("item_name_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Sub Category
                OutlinedTextField(
                    value = subCategory,
                    onValueChange = { subCategory = it },
                    label = { Text("Sub-Category / Type") },
                    placeholder = { Text("e.g. School Shirt, Text Book, Register...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // SKU & Barcode
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = sku,
                        onValueChange = { sku = it.uppercase() },
                        label = { Text("SKU / Item Code *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = barcode,
                        onValueChange = { barcode = it },
                        label = { Text("Barcode / Tag") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Target Class & Gender
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = classDropdownExpanded,
                        onExpandedChange = { classDropdownExpanded = !classDropdownExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = targetClass,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Class / Grade") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classDropdownExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = classDropdownExpanded,
                            onDismissRequest = { classDropdownExpanded = false }
                        ) {
                            classOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt) },
                                    onClick = {
                                        targetClass = opt
                                        classDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = genderDropdownExpanded,
                        onExpandedChange = { genderDropdownExpanded = !genderDropdownExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = gender,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Gender") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderDropdownExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = genderDropdownExpanded,
                            onDismissRequest = { genderDropdownExpanded = false }
                        ) {
                            genderOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt) },
                                    onClick = {
                                        gender = opt
                                        genderDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Size & Color
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = size,
                        onValueChange = { size = it },
                        label = { Text("Size") },
                        placeholder = { Text("28, 30, M, L, 6...") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = color,
                        onValueChange = { color = it },
                        label = { Text("Color") },
                        placeholder = { Text("Navy, White...") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // Category-specific fields: Books vs Notebooks vs Shoes
                val curCat = ItemCategory.fromString(category)
                if (curCat == ItemCategory.BOOKS) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = isbn,
                            onValueChange = { isbn = it },
                            label = { Text("ISBN Number") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = subject,
                            onValueChange = { subject = it },
                            label = { Text("Subject") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = brandOrPublisher,
                            onValueChange = { brandOrPublisher = it },
                            label = { Text("Publisher") },
                            placeholder = { Text("NCERT, Cambridge...") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = edition,
                            onValueChange = { edition = it },
                            label = { Text("Edition") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                } else if (curCat == ItemCategory.COPIES) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = brandOrPublisher,
                            onValueChange = { brandOrPublisher = it },
                            label = { Text("Brand") },
                            placeholder = { Text("Classmate, Navneet...") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = pagesText,
                            onValueChange = { pagesText = it },
                            label = { Text("Pages") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                } else if (curCat == ItemCategory.SHOES) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = brandOrPublisher,
                        onValueChange = { brandOrPublisher = it },
                        label = { Text("Shoe Brand") },
                        placeholder = { Text("Liberty, Bata, Action...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Prices: Purchase Price & Selling Price
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = purchasePriceText,
                        onValueChange = { purchasePriceText = it },
                        label = { Text("Purchase Price (₹) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = sellingPriceText,
                        onValueChange = { sellingPriceText = it },
                        label = { Text("Selling Price (₹) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Stock Levels: Current Stock & Minimum Stock Alert Level
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = currentStockText,
                        onValueChange = { currentStockText = it },
                        label = { Text("Current Stock *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = minStockText,
                        onValueChange = { minStockText = it },
                        label = { Text("Min Alert Level *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Supplier Dropdown
                ExposedDropdownMenuBox(
                    expanded = supplierDropdownExpanded,
                    onExpandedChange = { supplierDropdownExpanded = !supplierDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedSupplierName.ifBlank { "Select Supplier (Optional)" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Supplier") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = supplierDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = supplierDropdownExpanded,
                        onDismissRequest = { supplierDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("None") },
                            onClick = {
                                selectedSupplierId = null
                                selectedSupplierName = ""
                                supplierDropdownExpanded = false
                            }
                        )
                        suppliers.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s.name) },
                                onClick = {
                                    selectedSupplierId = s.id
                                    selectedSupplierName = s.name
                                    supplierDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                errorMessage = "Item name is required."
                                return@Button
                            }
                            if (sku.isBlank()) {
                                errorMessage = "SKU is required."
                                return@Button
                            }
                            val pPrice = purchasePriceText.toDoubleOrNull() ?: 0.0
                            val sPrice = sellingPriceText.toDoubleOrNull() ?: 0.0
                            val cStock = currentStockText.toIntOrNull() ?: 0
                            val mStock = minStockText.toIntOrNull() ?: 10
                            val pages = pagesText.toIntOrNull() ?: 0
                            val oStock = openingStockText.toIntOrNull() ?: cStock

                            val savedItem = StockItem(
                                id = initialItem?.id ?: 0L,
                                schoolId = initialItem?.schoolId ?: "",
                                name = name.trim(),
                                category = category,
                                subCategory = subCategory.trim(),
                                sku = sku.trim(),
                                barcode = barcode.trim(),
                                targetClass = targetClass,
                                gender = gender,
                                size = size.trim(),
                                color = color.trim(),
                                brandOrPublisher = brandOrPublisher.trim(),
                                isbn = isbn.trim(),
                                subject = subject.trim(),
                                edition = edition.trim(),
                                pages = pages,
                                purchasePrice = pPrice,
                                sellingPrice = sPrice,
                                openingStock = oStock,
                                currentStock = cStock,
                                minStockLevel = mStock,
                                supplierId = selectedSupplierId,
                                supplierName = selectedSupplierName,
                                description = description.trim()
                            )
                            onSave(savedItem, isNew)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy),
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .testTag("save_item_button")
                    ) {
                        Text(if (isNew) "Add Item" else "Update Item")
                    }
                }
            }
        }
    }
}
