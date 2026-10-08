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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.School
import com.example.data.model.UserRole
import com.example.ui.theme.SchoolGold
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800

@Composable
fun LoginScreen(
    school: School?,
    onLogin: (String, String, (Boolean, String) -> Unit) -> Unit,
    onQuickRoleSelect: (UserRole) -> Unit
) {
    var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("admin123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val schoolName = school?.name ?: "Learnium International School"

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // School Crest Icon
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(SchoolNavy),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = SchoolGold,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = schoolName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = SchoolNavy,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Central Stock & Inventory ERP Portal",
                style = MaterialTheme.typography.bodyMedium,
                color = Slate600,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Login Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Staff Sign In",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate800
                    )
                    Text(
                        text = "Enter your institutional credentials",
                        fontSize = 12.sp,
                        color = Slate600
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            errorMessage = null
                        },
                        label = { Text("Username") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_username_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        label = { Text("Password") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input"),
                        singleLine = true
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            isLoading = true
                            onLogin(username, password) { success, msg ->
                                isLoading = false
                                if (!success) {
                                    errorMessage = msg
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("login_submit_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Sign In to Portal", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Demo / Fast Evaluation Role Login Buttons
                    Text(
                        text = "1-Click Demo Logins (Evaluation):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate600
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = false,
                            onClick = {
                                username = "admin"
                                password = "admin123"
                                onQuickRoleSelect(UserRole.SUPER_ADMIN)
                            },
                            label = { Text("Super Admin", fontSize = 10.sp) }
                        )

                        FilterChip(
                            selected = false,
                            onClick = {
                                username = "manager"
                                password = "manager123"
                                onQuickRoleSelect(UserRole.STORE_MANAGER)
                            },
                            label = { Text("Manager", fontSize = 10.sp) }
                        )

                        FilterChip(
                            selected = false,
                            onClick = {
                                username = "staff"
                                password = "staff123"
                                onQuickRoleSelect(UserRole.STAFF)
                            },
                            label = { Text("Staff", fontSize = 10.sp) }
                        )
                    }
                }
            }
        }
    }
}
