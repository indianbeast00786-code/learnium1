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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SchoolGold
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import com.example.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    users: List<User>,
    currentUser: User?,
    onSaveUser: (User, Boolean) -> Unit,
    onDeleteUser: (User) -> Unit,
    onQuickSwitchUser: (UserRole) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingUser by remember { mutableStateOf<User?>(null) }
    var userToDelete by remember { mutableStateOf<User?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Text(
                    text = "Staff Users & Role Permissions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Manage staff accounts, assign roles and control access levels",
                    fontSize = 12.sp,
                    color = Slate600
                )
            }

            // Quick Role Switcher for Testing & Demonstration
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, tint = SchoolNavy)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Quick Role Switcher (Test Permissions)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(UserRole.entries.toTypedArray()) { role ->
                                val isCurrent = currentUser?.role == role
                                FilterChip(
                                    selected = isCurrent,
                                    onClick = { onQuickSwitchUser(role) },
                                    label = { Text(role.displayName, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }
            }

            // Permissions Matrix Explainer Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate100)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "Role Access Levels:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate800)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "• Super Admin / Admin: Full control over inventory, sales, purchases, suppliers, users and school settings.", fontSize = 11.sp, color = Slate600)
                        Text(text = "• Store Manager: Manage inventory stock, inward stock-in purchases, suppliers and stock-out.", fontSize = 11.sp, color = Slate600)
                        Text(text = "• Accountant: Sales audits, financial reports, purchase orders and bill printing.", fontSize = 11.sp, color = Slate600)
                        Text(text = "• Staff: Issue items to students, check stock availability, generate and print bills.", fontSize = 11.sp, color = Slate600)
                    }
                }
            }

            // Users List
            items(users) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (user.role) {
                                            UserRole.SUPER_ADMIN -> SchoolNavy
                                            UserRole.ADMIN -> Color(0xFF1D4ED8)
                                            UserRole.STORE_MANAGER -> SchoolGold
                                            UserRole.ACCOUNTANT -> SuccessGreen
                                            UserRole.STAFF -> Slate600
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (user.role.level >= 4) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = user.fullName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Slate800
                                )
                                Text(
                                    text = "Username: @${user.username} • ${user.email}",
                                    fontSize = 11.sp,
                                    color = Slate600
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(SchoolNavy.copy(alpha = 0.1f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = user.role.displayName,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SchoolNavy
                                    )
                                }
                            }
                        }

                        if (currentUser?.role?.canManageUsers() != false) {
                            Row {
                                IconButton(onClick = {
                                    editingUser = user
                                    showDialog = true
                                }) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = SchoolNavy, modifier = Modifier.size(18.dp))
                                }
                                if (users.size > 1 && user.id != currentUser?.id) {
                                    IconButton(onClick = { userToDelete = user }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Add User FAB
        if (currentUser?.role?.canManageUsers() != false) {
            FloatingActionButton(
                onClick = {
                    editingUser = null
                    showDialog = true
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
                    .testTag("add_user_fab"),
                containerColor = SchoolNavy,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add User")
            }
        }
    }

    // Add / Edit User Dialog
    if (showDialog) {
        var username by remember { mutableStateOf(editingUser?.username ?: "") }
        var fullName by remember { mutableStateOf(editingUser?.fullName ?: "") }
        var email by remember { mutableStateOf(editingUser?.email ?: "") }
        var password by remember { mutableStateOf(editingUser?.password ?: "pass123") }
        var selectedRole by remember { mutableStateOf(editingUser?.role ?: UserRole.STAFF) }
        var roleDropdownExpanded by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth(0.95f)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (editingUser == null) "Create Staff Account" else "Edit Staff User",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SchoolNavy
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Role selector
                    ExposedDropdownMenuBox(
                        expanded = roleDropdownExpanded,
                        onExpandedChange = { roleDropdownExpanded = !roleDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedRole.displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Assigned Role") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleDropdownExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = roleDropdownExpanded,
                            onDismissRequest = { roleDropdownExpanded = false }
                        ) {
                            UserRole.entries.forEach { r ->
                                DropdownMenuItem(
                                    text = { Text(r.displayName) },
                                    onClick = {
                                        selectedRole = r
                                        roleDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        OutlinedButton(onClick = { showDialog = false }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (username.isNotBlank() && fullName.isNotBlank()) {
                                    val u = User(
                                        id = editingUser?.id ?: 0L,
                                        schoolId = editingUser?.schoolId ?: "",
                                        username = username.trim().lowercase(),
                                        fullName = fullName.trim(),
                                        email = email.trim(),
                                        password = password.trim(),
                                        role = selectedRole
                                    )
                                    onSaveUser(u, editingUser == null)
                                    showDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy)
                        ) {
                            Text("Save Account")
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation
    userToDelete?.let { user ->
        ConfirmDeleteDialog(
            title = "Delete User Account?",
            message = "Are you sure you want to delete ${user.fullName} (@${user.username})?",
            onConfirm = {
                onDeleteUser(user)
                userToDelete = null
            },
            onDismiss = { userToDelete = null }
        )
    }
}
