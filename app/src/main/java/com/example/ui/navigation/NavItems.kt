package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestination(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val inBottomBar: Boolean = false
) {
    DASHBOARD("dashboard", "Dashboard", Icons.Default.Dashboard, true),
    STOCK("stock", "Inventory", Icons.Default.Inventory, true),
    STOCK_IN("stock_in", "Stock In", Icons.Default.ArrowDownward, true),
    STOCK_OUT("stock_out", "Stock Out", Icons.Default.AddShoppingCart, true),
    STUDENTS("students", "Students", Icons.Default.People, false),
    SUPPLIERS("suppliers", "Suppliers", Icons.Default.Business, false),
    REPORTS("reports", "Reports", Icons.Default.Assessment, true),
    USERS("users", "Users & Roles", Icons.Default.AdminPanelSettings, false),
    SETTINGS("settings", "Settings", Icons.Default.Settings, false)
}
