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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
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
import com.example.data.model.SaleWithItems
import com.example.data.model.Student
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SchoolGold
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import com.example.util.FormatHelper

@Composable
fun StudentsScreen(
    students: List<Student>,
    sales: List<SaleWithItems>,
    currencySymbol: String = "₹",
    onSaveStudent: (Student, Boolean) -> Unit,
    onDeleteStudent: (Student) -> Unit,
    onExportStudents: () -> Unit,
    onViewReceipt: (SaleWithItems) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var editingStudent by remember { mutableStateOf<Student?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var studentToDelete by remember { mutableStateOf<Student?>(null) }
    var selectedStudentForHistory by remember { mutableStateOf<Student?>(null) }

    val filtered = students.filter {
        searchQuery.isBlank() ||
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.studentId.contains(searchQuery, ignoreCase = true) ||
            it.className.contains(searchQuery, ignoreCase = true) ||
            it.parentName.contains(searchQuery, ignoreCase = true)
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
                        text = "Students & Issue Records (${students.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedButton(onClick = onExportStudents) {
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
                    placeholder = { Text("Search by student name, ID, class...") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Student List
            if (filtered.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Box(modifier = Modifier.padding(32.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No students found.", color = Slate600)
                        }
                    }
                }
            } else {
                items(filtered) { student ->
                    val studentSales = sales.filter {
                        it.order.studentId == student.studentId ||
                            it.order.studentName.equals(student.name, ignoreCase = true)
                    }
                    val totalSpent = studentSales.sumOf { it.order.netAmount }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedStudentForHistory = student },
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
                                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = SchoolNavy)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = student.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Slate800
                                        )
                                        Text(
                                            text = "ID: ${student.studentId} • ${student.className} Section ${student.section}",
                                            fontSize = 12.sp,
                                            color = Slate600
                                        )
                                    }
                                }

                                Row {
                                    IconButton(onClick = {
                                        editingStudent = student
                                        showDialog = true
                                    }) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = SchoolNavy, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { studentToDelete = student }) {
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
                                    text = "Parent: ${student.parentName.ifBlank { "N/A" }} (${student.parentPhone.ifBlank { "No phone" }})",
                                    fontSize = 11.sp,
                                    color = Slate600
                                )
                                Text(
                                    text = "Issued: ${studentSales.size} bills (${FormatHelper.formatCurrency(totalSpent, currencySymbol)})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SchoolNavy
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

        // Add Student FAB
        FloatingActionButton(
            onClick = {
                editingStudent = null
                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_student_fab"),
            containerColor = SchoolNavy,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Student")
        }
    }

    // Add / Edit Student Dialog
    if (showDialog) {
        var name by remember { mutableStateOf(editingStudent?.name ?: "") }
        var studentId by remember { mutableStateOf(editingStudent?.studentId ?: "LIS-2026-${(100..999).random()}") }
        var className by remember { mutableStateOf(editingStudent?.className ?: "Class 1") }
        var section by remember { mutableStateOf(editingStudent?.section ?: "A") }
        var parentName by remember { mutableStateOf(editingStudent?.parentName ?: "") }
        var parentPhone by remember { mutableStateOf(editingStudent?.parentPhone ?: "") }

        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth(0.95f)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (editingStudent == null) "Register New Student" else "Edit Student Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SchoolNavy
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = studentId,
                        onValueChange = { studentId = it },
                        label = { Text("Student Admission ID *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = className,
                            onValueChange = { className = it },
                            label = { Text("Class") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = section,
                            onValueChange = { section = it },
                            label = { Text("Section") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = parentName,
                        onValueChange = { parentName = it },
                        label = { Text("Parent / Guardian Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = parentPhone,
                        onValueChange = { parentPhone = it },
                        label = { Text("Parent Phone") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        OutlinedButton(onClick = { showDialog = false }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (name.isNotBlank()) {
                                    val s = Student(
                                        id = editingStudent?.id ?: 0L,
                                        schoolId = editingStudent?.schoolId ?: "",
                                        studentId = studentId.trim(),
                                        name = name.trim(),
                                        className = className.trim(),
                                        section = section.trim(),
                                        parentName = parentName.trim(),
                                        parentPhone = parentPhone.trim()
                                    )
                                    onSaveStudent(s, editingStudent == null)
                                    showDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy)
                        ) {
                            Text("Save Student")
                        }
                    }
                }
            }
        }
    }

    // Student Sales History Modal
    selectedStudentForHistory?.let { student ->
        val studentSales = sales.filter {
            it.order.studentId == student.studentId ||
                it.order.studentName.equals(student.name, ignoreCase = true)
        }

        Dialog(onDismissRequest = { selectedStudentForHistory = null }) {
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
                                text = "Purchase Records: ${student.name}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SchoolNavy
                            )
                            Text(
                                text = "ID: ${student.studentId} • ${student.className} ${student.section}",
                                fontSize = 12.sp,
                                color = Slate600
                            )
                        }
                        IconButton(onClick = { selectedStudentForHistory = null }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (studentSales.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("No stock items have been issued to this student yet.", color = Slate600, fontSize = 13.sp)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.height(300.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(studentSales) { sale ->
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
                                            Text(text = sale.order.receiptNumber, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SchoolNavy)
                                            Text(text = FormatHelper.formatCurrency(sale.order.netAmount, currencySymbol), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                        Text(text = FormatHelper.formatDate(sale.order.dateMillis), fontSize = 10.sp, color = Slate600)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = sale.items.joinToString(", ") { "${it.itemName} (x${it.quantity})" },
                                            fontSize = 11.sp,
                                            color = Slate800
                                        )
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                            TextButton(onClick = {
                                                selectedStudentForHistory = null
                                                onViewReceipt(sale)
                                            }) {
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
        }
    }

    // Delete confirmation
    studentToDelete?.let { student ->
        ConfirmDeleteDialog(
            title = "Delete Student Record?",
            message = "Are you sure you want to delete ${student.name} (${student.studentId})?",
            onConfirm = {
                onDeleteStudent(student)
                studentToDelete = null
            },
            onDismiss = { studentToDelete = null }
        )
    }
}
