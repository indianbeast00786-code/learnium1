package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SaleOrder
import com.example.data.model.SaleWithItems
import com.example.data.model.StockItem
import com.example.ui.navigation.AppDestination
import com.example.ui.theme.SchoolGold
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import com.example.ui.viewmodel.InventoryViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: InventoryViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentSchool by viewModel.currentSchool.collectAsStateWithLifecycle()
    val allSchools by viewModel.allSchools.collectAsStateWithLifecycle()
    val currentLang by viewModel.language.collectAsStateWithLifecycle()

    val stockItems by viewModel.stockItems.collectAsStateWithLifecycle()
    val filteredStockItems by viewModel.filteredStockItems.collectAsStateWithLifecycle()
    val suppliers by viewModel.suppliers.collectAsStateWithLifecycle()
    val students by viewModel.students.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val sales by viewModel.sales.collectAsStateWithLifecycle()
    val users by viewModel.users.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategoryTab.collectAsStateWithLifecycle()
    val stockStatusFilter by viewModel.selectedStockStatusFilter.collectAsStateWithLifecycle()
    val selectedClass by viewModel.selectedClassFilter.collectAsStateWithLifecycle()

    var currentDestination by remember { mutableStateOf(AppDestination.DASHBOARD) }

    // Dialog state holders
    var showItemEditDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<StockItem?>(null) }
    var activeReceiptSale by remember { mutableStateOf<SaleWithItems?>(null) }

    // Listen for feedback messages
    LaunchedEffect(Unit) {
        viewModel.feedbackMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // If no user is logged in, show Login Screen
    if (currentUser == null) {
        LoginScreen(
            school = currentSchool,
            onLogin = { user, pass, cb -> viewModel.login(user, pass, cb) },
            onQuickRoleSelect = { role -> viewModel.quickSwitchUser(role) }
        )
        return
    }

    val currency = currentSchool?.currencySymbol ?: "₹"

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth > 650.dp

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(modifier = Modifier.width(300.dp)) {
                    // Drawer Header
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SchoolNavy)
                            .padding(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(SchoolGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = SchoolNavy,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = currentSchool?.name ?: "Learnium International School",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Campus Code: ${currentSchool?.code ?: "LIS-001"}",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Logged in as: ${currentUser?.fullName} (${currentUser?.role?.displayName})",
                            color = SchoolGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // All Nav Items
                    AppDestination.entries.forEach { dest ->
                        NavigationDrawerItem(
                            icon = { Icon(imageVector = dest.icon, contentDescription = dest.title) },
                            label = { Text(dest.title, fontWeight = FontWeight.Medium) },
                            selected = currentDestination == dest,
                            onClick = {
                                currentDestination = dest
                                coroutineScope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    HorizontalDivider(thickness = 0.5.dp, color = Slate200)

                    // Sign Out in Drawer
                    NavigationDrawerItem(
                        icon = { Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sign Out", tint = MaterialTheme.colorScheme.error) },
                        label = { Text("Sign Out", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            viewModel.logout()
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Wide Screen Navigation Rail
                if (isWideScreen) {
                    NavigationRail(
                        modifier = Modifier.fillMaxHeight(),
                        containerColor = MaterialTheme.colorScheme.surface,
                        header = {
                            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                                Icon(imageVector = Icons.Default.Menu, contentDescription = "Open Menu")
                            }
                        }
                    ) {
                        AppDestination.entries.forEach { dest ->
                            NavigationRailItem(
                                icon = { Icon(imageVector = dest.icon, contentDescription = dest.title) },
                                label = { Text(dest.title, fontSize = 10.sp) },
                                selected = currentDestination == dest,
                                onClick = { currentDestination = dest }
                            )
                        }
                    }
                }

                // Main Content Scaffold
                Scaffold(
                    modifier = Modifier.weight(1f),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        TopAppBar(
                            title = {
                                Column {
                                    Text(
                                        text = currentDestination.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = currentSchool?.name ?: "Learnium International School",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            navigationIcon = {
                                if (!isWideScreen) {
                                    IconButton(
                                        onClick = { coroutineScope.launch { drawerState.open() } },
                                        modifier = Modifier.testTag("menu_drawer_button")
                                    ) {
                                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                                    }
                                }
                            },
                            actions = {
                                // User role pill
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SchoolNavy.copy(alpha = 0.1f))
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = currentUser?.role?.displayName ?: "",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SchoolNavy
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.logout() },
                                    modifier = Modifier.testTag("top_logout_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                        contentDescription = "Sign Out",
                                        tint = Slate600
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    bottomBar = {
                        if (!isWideScreen) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 3.dp
                            ) {
                                val bottomDestinations = AppDestination.entries.filter { it.inBottomBar }
                                bottomDestinations.forEach { dest ->
                                    NavigationBarItem(
                                        icon = { Icon(imageVector = dest.icon, contentDescription = dest.title) },
                                        label = { Text(dest.title, fontSize = 10.sp) },
                                        selected = currentDestination == dest,
                                        onClick = { currentDestination = dest },
                                        modifier = Modifier.testTag("nav_${dest.route}")
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentDestination) {
                            AppDestination.DASHBOARD -> DashboardScreen(
                                school = currentSchool,
                                currentUser = currentUser,
                                stockItems = stockItems,
                                sales = sales,
                                transactions = transactions,
                                currencySymbol = currency,
                                onNavigateToStock = { cat, status ->
                                    if (cat != null) viewModel.selectedCategoryTab.value = cat
                                    if (status != null) viewModel.selectedStockStatusFilter.value = status
                                    currentDestination = AppDestination.STOCK
                                },
                                onNavigateToStockIn = { currentDestination = AppDestination.STOCK_IN },
                                onNavigateToStockOut = { currentDestination = AppDestination.STOCK_OUT },
                                onAddNewItem = {
                                    itemToEdit = null
                                    showItemEditDialog = true
                                },
                                onExportExcel = { viewModel.exportStock(context) },
                                onViewSaleReceipt = { activeReceiptSale = it }
                            )

                            AppDestination.STOCK -> InventoryListScreen(
                                items = filteredStockItems,
                                searchQuery = searchQuery,
                                onSearchQueryChange = { viewModel.searchQuery.value = it },
                                selectedCategory = selectedCategory,
                                onCategoryChange = { viewModel.selectedCategoryTab.value = it },
                                stockStatusFilter = stockStatusFilter,
                                onStockStatusFilterChange = { viewModel.selectedStockStatusFilter.value = it },
                                selectedClass = selectedClass,
                                onClassChange = { viewModel.selectedClassFilter.value = it },
                                currentUser = currentUser,
                                currencySymbol = currency,
                                onAddItem = {
                                    itemToEdit = null
                                    showItemEditDialog = true
                                },
                                onEditItem = {
                                    itemToEdit = it
                                    showItemEditDialog = true
                                },
                                onDeleteItem = { viewModel.deleteStockItem(it) },
                                onQuickStockIn = {
                                    currentDestination = AppDestination.STOCK_IN
                                },
                                onExportExcel = { viewModel.exportStock(context) }
                            )

                            AppDestination.STOCK_IN -> StockInScreen(
                                stockItems = stockItems,
                                suppliers = suppliers,
                                transactions = transactions,
                                currencySymbol = currency,
                                onSubmitStockIn = { item, qty, cost, inv, suppId, suppName, notes ->
                                    viewModel.recordStockIn(item, qty, cost, inv, suppId, suppName, notes)
                                },
                                onExportPurchases = { viewModel.exportPurchases(context) }
                            )

                            AppDestination.STOCK_OUT -> StockOutScreen(
                                stockItems = stockItems,
                                students = students,
                                sales = sales,
                                currencySymbol = currency,
                                onCompleteSale = { student, sName, sClass, cart, disc, pm, notes, onOrderCreated ->
                                    viewModel.recordSale(student, sName, sClass, cart, disc, pm, notes) { newOrder ->
                                        onOrderCreated(newOrder)
                                        // Open receipt modal
                                        activeReceiptSale = SaleWithItems(
                                            order = newOrder,
                                            items = cart.map { (it, qty) ->
                                                com.example.data.model.SaleOrderItem(
                                                    saleOrderId = newOrder.id,
                                                    itemId = it.id,
                                                    itemName = it.name,
                                                    category = it.category,
                                                    sku = it.sku,
                                                    size = it.size,
                                                    quantity = qty,
                                                    unitPrice = it.sellingPrice,
                                                    totalPrice = it.sellingPrice * qty
                                                )
                                            }
                                        )
                                    }
                                },
                                onViewReceipt = { activeReceiptSale = it },
                                onCancelSale = { viewModel.cancelSale(it) },
                                onExportSales = { viewModel.exportSales(context) }
                            )

                            AppDestination.STUDENTS -> StudentsScreen(
                                students = students,
                                sales = sales,
                                currencySymbol = currency,
                                onSaveStudent = { s, isNew -> viewModel.saveStudent(s, isNew) },
                                onDeleteStudent = { viewModel.deleteStudent(it) },
                                onExportStudents = { viewModel.exportStudents(context) },
                                onViewReceipt = { activeReceiptSale = it }
                            )

                            AppDestination.SUPPLIERS -> SuppliersScreen(
                                suppliers = suppliers,
                                transactions = transactions,
                                currencySymbol = currency,
                                onSaveSupplier = { s, isNew -> viewModel.saveSupplier(s, isNew) },
                                onDeleteSupplier = { viewModel.deleteSupplier(it) },
                                onExportSuppliers = { viewModel.exportSuppliers(context) }
                            )

                            AppDestination.REPORTS -> ReportsScreen(
                                stockItems = stockItems,
                                sales = sales,
                                transactions = transactions,
                                currencySymbol = currency,
                                onExportReports = {
                                    viewModel.exportStock(context)
                                }
                            )

                            AppDestination.USERS -> UsersScreen(
                                users = users,
                                currentUser = currentUser,
                                onSaveUser = { u, isNew -> viewModel.saveUser(u, isNew) },
                                onDeleteUser = { viewModel.deleteUser(it) },
                                onQuickSwitchUser = { viewModel.quickSwitchUser(it) }
                            )

                            AppDestination.SETTINGS -> SettingsScreen(
                                currentSchool = currentSchool,
                                allSchools = allSchools,
                                currentLanguage = currentLang,
                                onSaveSchoolSettings = { viewModel.updateSchoolSettings(it) },
                                onSwitchSchool = { viewModel.switchSchool(it) },
                                onCreateNewSchoolTenant = { sc, pass -> viewModel.createNewSchoolTenant(sc, pass) },
                                onSelectLanguage = { viewModel.setLanguage(it) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Item Add / Edit Modal Dialog
    if (showItemEditDialog) {
        ItemEditDialog(
            initialItem = itemToEdit,
            suppliers = suppliers,
            onDismiss = {
                showItemEditDialog = false
                itemToEdit = null
            },
            onSave = { savedItem, isNew ->
                viewModel.saveStockItem(savedItem, isNew)
                showItemEditDialog = false
                itemToEdit = null
            }
        )
    }

    // Bill / Receipt Modal Dialog
    activeReceiptSale?.let { sale ->
        ReceiptDialog(
            order = sale.order,
            items = sale.items,
            school = currentSchool,
            onDismiss = { activeReceiptSale = null }
        )
    }
}
