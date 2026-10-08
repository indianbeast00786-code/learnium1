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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.School
import com.example.ui.theme.SchoolGold
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import com.example.util.LanguageHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentSchool: School?,
    allSchools: List<School>,
    currentLanguage: String,
    onSaveSchoolSettings: (School) -> Unit,
    onSwitchSchool: (String) -> Unit,
    onCreateNewSchoolTenant: (School, String) -> Unit,
    onSelectLanguage: (String) -> Unit
) {
    var name by remember(currentSchool) { mutableStateOf(currentSchool?.name ?: "Learnium International School") }
    var code by remember(currentSchool) { mutableStateOf(currentSchool?.code ?: "LIS-001") }
    var address by remember(currentSchool) { mutableStateOf(currentSchool?.address ?: "") }
    var phone by remember(currentSchool) { mutableStateOf(currentSchool?.phone ?: "") }
    var email by remember(currentSchool) { mutableStateOf(currentSchool?.email ?: "") }
    var gstNumber by remember(currentSchool) { mutableStateOf(currentSchool?.gstNumber ?: "") }
    var academicSession by remember(currentSchool) { mutableStateOf(currentSchool?.academicSession ?: "2026-2027") }
    var currencySymbol by remember(currentSchool) { mutableStateOf(currentSchool?.currencySymbol ?: "₹") }
    var receiptNotes by remember(currentSchool) { mutableStateOf(currentSchool?.receiptNotes ?: "") }

    var showNewSchoolDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Multi-School Branch / Tenant Switcher
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
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SchoolNavy.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Business, contentDescription = null, tint = SchoolNavy)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "Multi-School Tenant System", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "Isolate stock, sales & users per campus", fontSize = 11.sp, color = Slate600)
                            }
                        }

                        OutlinedButton(onClick = { showNewSchoolDialog = true }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New School", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Active School Campus:", fontSize = 12.sp, color = Slate600)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allSchools.forEach { s ->
                            val isSelected = s.id == currentSchool?.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSwitchSchool(s.id) },
                                label = { Text(s.name, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }
        }

        // Language Selection Section (13 Indian languages support)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SchoolGold.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = SchoolGold)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "System Language / भाषा", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "Select primary ERP display language", fontSize = 11.sp, color = Slate600)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LanguageHelper.languages.take(6).forEach { lang ->
                            FilterChip(
                                selected = currentLanguage == lang.code,
                                onClick = { onSelectLanguage(lang.code) },
                                label = { Text("${lang.nativeName} (${lang.name})", fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LanguageHelper.languages.drop(6).forEach { lang ->
                            FilterChip(
                                selected = currentLanguage == lang.code,
                                onClick = { onSelectLanguage(lang.code) },
                                label = { Text(lang.nativeName, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }

        // School Information Form Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SchoolNavy.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.School, contentDescription = null, tint = SchoolNavy)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "School Profile & Store Metadata", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "Appears on all printed receipts and audit reports", fontSize = 11.sp, color = Slate600)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("School Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = code,
                            onValueChange = { code = it },
                            label = { Text("School Code") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = academicSession,
                            onValueChange = { academicSession = it },
                            label = { Text("Academic Session") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("School Campus Address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = gstNumber,
                            onValueChange = { gstNumber = it },
                            label = { Text("GST Number") },
                            modifier = Modifier.weight(1.5f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = currencySymbol,
                            onValueChange = { currencySymbol = it },
                            label = { Text("Currency") },
                            modifier = Modifier.weight(0.8f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = receiptNotes,
                        onValueChange = { receiptNotes = it },
                        label = { Text("Receipt Policy & Exchange Terms") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val updated = currentSchool?.copy(
                                name = name.trim(),
                                code = code.trim(),
                                address = address.trim(),
                                phone = phone.trim(),
                                email = email.trim(),
                                gstNumber = gstNumber.trim(),
                                academicSession = academicSession.trim(),
                                currencySymbol = currencySymbol.trim(),
                                receiptNotes = receiptNotes.trim()
                            ) ?: School(
                                id = "learnium_intl",
                                name = name.trim(),
                                code = code.trim(),
                                address = address.trim(),
                                phone = phone.trim(),
                                email = email.trim(),
                                gstNumber = gstNumber.trim(),
                                academicSession = academicSession.trim(),
                                currencySymbol = currencySymbol.trim(),
                                receiptNotes = receiptNotes.trim()
                            )
                            onSaveSchoolSettings(updated)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_school_settings_button")
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save School Profile")
                    }
                }
            }
        }
    }

    // Create New School Tenant Dialog
    if (showNewSchoolDialog) {
        var newSchoolName by remember { mutableStateOf("") }
        var newSchoolCode by remember { mutableStateOf("") }
        var newSchoolAddress by remember { mutableStateOf("") }
        var newSchoolEmail by remember { mutableStateOf("") }
        var adminPassword by remember { mutableStateOf("admin123") }

        Dialog(onDismissRequest = { showNewSchoolDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth(0.95f)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Provision New School Tenant",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SchoolNavy
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newSchoolName,
                        onValueChange = { newSchoolName = it },
                        label = { Text("School Name *") },
                        placeholder = { Text("e.g. Learnium South Campus") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newSchoolCode,
                        onValueChange = { newSchoolCode = it },
                        label = { Text("School Code") },
                        placeholder = { Text("e.g. LIS-002") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newSchoolAddress,
                        onValueChange = { newSchoolAddress = it },
                        label = { Text("Campus Address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newSchoolEmail,
                        onValueChange = { newSchoolEmail = it },
                        label = { Text("Admin Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = adminPassword,
                        onValueChange = { adminPassword = it },
                        label = { Text("Initial Admin Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        OutlinedButton(onClick = { showNewSchoolDialog = false }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newSchoolName.isNotBlank()) {
                                    val tenantId = "school_" + System.currentTimeMillis()
                                    val sc = School(
                                        id = tenantId,
                                        name = newSchoolName.trim(),
                                        code = newSchoolCode.trim().ifBlank { "SCH-${(100..999).random()}" },
                                        address = newSchoolAddress.trim(),
                                        phone = "+91 98000 00000",
                                        email = newSchoolEmail.trim(),
                                        gstNumber = "",
                                        academicSession = "2026-2027",
                                        currencySymbol = "₹"
                                    )
                                    onCreateNewSchoolTenant(sc, adminPassword)
                                    showNewSchoolDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy)
                        ) {
                            Text("Create Tenant")
                        }
                    }
                }
            }
        }
    }
}
