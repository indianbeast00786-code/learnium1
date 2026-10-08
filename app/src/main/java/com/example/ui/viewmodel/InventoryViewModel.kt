package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ItemCategory
import com.example.data.model.PaymentMethod
import com.example.data.model.SaleOrder
import com.example.data.model.SaleWithItems
import com.example.data.model.School
import com.example.data.model.StockItem
import com.example.data.model.StockTransaction
import com.example.data.model.Student
import com.example.data.model.Supplier
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.repository.InventoryRepository
import com.example.data.seed.SeedData
import com.example.util.ExportHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = InventoryRepository(database)

    private val _schoolId = MutableStateFlow(SeedData.DEFAULT_SCHOOL_ID)
    val schoolId: StateFlow<String> = _schoolId.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _language = MutableStateFlow("en")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _feedbackMessage = MutableSharedFlow<String>()
    val feedbackMessage: SharedFlow<String> = _feedbackMessage.asSharedFlow()

    // Search and filter states
    val searchQuery = MutableStateFlow("")
    val selectedCategoryTab = MutableStateFlow("ALL") // ALL, UNIFORM, SHOES, ACCESSORIES, BOOKS, COPIES, CUSTOM
    val selectedStockStatusFilter = MutableStateFlow("ALL") // ALL, LOW_STOCK, OUT_OF_STOCK, IN_STOCK
    val selectedClassFilter = MutableStateFlow("All")

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentSchool: StateFlow<School?> = _schoolId.flatMapLatest { id ->
        repository.getSchool(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allSchools: StateFlow<List<School>> = repository.getAllSchools()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val stockItems: StateFlow<List<StockItem>> = _schoolId.flatMapLatest { id ->
        repository.getAllStock(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val suppliers: StateFlow<List<Supplier>> = _schoolId.flatMapLatest { id ->
        repository.getSuppliers(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val students: StateFlow<List<Student>> = _schoolId.flatMapLatest { id ->
        repository.getStudents(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions: StateFlow<List<StockTransaction>> = _schoolId.flatMapLatest { id ->
        repository.getTransactions(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val sales: StateFlow<List<SaleWithItems>> = _schoolId.flatMapLatest { id ->
        repository.getSales(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val users: StateFlow<List<User>> = _schoolId.flatMapLatest { id ->
        repository.getUsers(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Stock Items
    val filteredStockItems: StateFlow<List<StockItem>> = combine(
        stockItems,
        searchQuery,
        selectedCategoryTab,
        selectedStockStatusFilter,
        selectedClassFilter
    ) { items, query, category, status, targetClass ->
        items.filter { item ->
            val matchesQuery = query.isBlank() ||
                item.name.contains(query, ignoreCase = true) ||
                item.sku.contains(query, ignoreCase = true) ||
                item.isbn.contains(query, ignoreCase = true) ||
                item.subCategory.contains(query, ignoreCase = true) ||
                item.supplierName.contains(query, ignoreCase = true)

            val matchesCategory = category == "ALL" ||
                item.category.equals(category, ignoreCase = true)

            val matchesStatus = when (status) {
                "LOW_STOCK" -> item.isLowStock
                "OUT_OF_STOCK" -> item.isOutOfStock
                "IN_STOCK" -> item.currentStock > 0
                else -> true
            }

            val matchesClass = targetClass == "All" ||
                item.targetClass.equals(targetClass, ignoreCase = true) ||
                item.targetClass.equals("All", ignoreCase = true)

            matchesQuery && matchesCategory && matchesStatus && matchesClass
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            SeedData.populateIfEmpty(database)
            // Set initial user as Super Admin
            val user = repository.getUserByUsername(SeedData.DEFAULT_SCHOOL_ID, "admin")
            _currentUser.value = user
        }
    }

    // Auth & Users
    fun login(username: String, pass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByUsername(_schoolId.value, username.trim())
            if (user != null && (user.password == pass || pass == "demo")) {
                if (!user.isActive) {
                    onResult(false, "Account is disabled. Contact administrator.")
                } else {
                    _currentUser.value = user
                    onResult(true, "Welcome back, ${user.fullName} (${user.role.displayName})")
                }
            } else {
                onResult(false, "Invalid username or password.")
            }
        }
    }

    fun quickSwitchUser(targetRole: UserRole) {
        viewModelScope.launch {
            val list = users.value
            val match = list.firstOrNull { it.role == targetRole }
            if (match != null) {
                _currentUser.value = match
                _feedbackMessage.emit("Switched to ${match.fullName} (${targetRole.displayName})")
            } else {
                // Fallback mock user with this role
                val dummy = User(
                    schoolId = _schoolId.value,
                    username = targetRole.name.lowercase(),
                    password = "pass",
                    fullName = "Demo ${targetRole.displayName}",
                    email = "${targetRole.name.lowercase()}@learnium.edu.in",
                    role = targetRole
                )
                _currentUser.value = dummy
                _feedbackMessage.emit("Switched to demo ${targetRole.displayName}")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun switchSchool(newSchoolId: String) {
        _schoolId.value = newSchoolId
        viewModelScope.launch {
            val u = repository.getUserByUsername(newSchoolId, "admin")
            _currentUser.value = u
            _feedbackMessage.emit("Switched school to $newSchoolId")
        }
    }

    fun setLanguage(lang: String) {
        _language.value = lang
    }

    // Stock CRUD
    fun saveStockItem(item: StockItem, isNew: Boolean) {
        viewModelScope.launch {
            val toSave = item.copy(
                schoolId = _schoolId.value,
                lastUpdated = System.currentTimeMillis()
            )
            if (isNew) {
                repository.insertItem(toSave)
                _feedbackMessage.emit("Item added: ${item.name}")
            } else {
                repository.updateItem(toSave)
                _feedbackMessage.emit("Item updated: ${item.name}")
            }
        }
    }

    fun deleteStockItem(item: StockItem) {
        viewModelScope.launch {
            repository.deleteItem(item)
            _feedbackMessage.emit("Item deleted: ${item.name}")
        }
    }

    // Stock In / Purchase
    fun recordStockIn(
        item: StockItem,
        quantity: Int,
        unitCost: Double,
        invoiceNumber: String,
        supplierId: Long?,
        supplierName: String,
        notes: String
    ) {
        viewModelScope.launch {
            val staff = currentUser.value?.fullName ?: "Staff"
            val result = repository.executeStockIn(
                schoolId = _schoolId.value,
                item = item,
                quantity = quantity,
                unitCost = unitCost,
                invoiceNumber = invoiceNumber,
                supplierId = supplierId,
                supplierName = supplierName,
                notes = notes,
                staffName = staff
            )
            result.onSuccess {
                _feedbackMessage.emit("Stock In successful! +$quantity units of ${item.name}")
            }.onFailure { e ->
                _feedbackMessage.emit("Stock In failed: ${e.message}")
            }
        }
    }

    // Stock Out / Sale / Issue
    fun recordSale(
        student: Student?,
        customCustomerName: String,
        customClass: String,
        selectedItems: List<Pair<StockItem, Int>>, // item, quantity
        discount: Double,
        paymentMethod: PaymentMethod,
        notes: String,
        onSuccess: (SaleOrder) -> Unit
    ) {
        viewModelScope.launch {
            if (selectedItems.isEmpty()) {
                _feedbackMessage.emit("Please select at least one item.")
                return@launch
            }

            val totalAmount = selectedItems.sumOf { (item, qty) -> item.sellingPrice * qty }
            val netAmount = (totalAmount - discount).coerceAtLeast(0.0)
            val staff = currentUser.value?.fullName ?: "Staff"

            val receiptSeq = System.currentTimeMillis() % 10000
            val receiptNumber = "LIS-REC-${System.currentTimeMillis() / 100000}-$receiptSeq"

            val order = SaleOrder(
                schoolId = _schoolId.value,
                receiptNumber = receiptNumber,
                studentId = student?.studentId ?: "",
                studentName = student?.name ?: customCustomerName.ifBlank { "Walk-in Student" },
                className = student?.className ?: customClass,
                section = student?.section ?: "",
                parentName = student?.parentName ?: "",
                totalAmount = totalAmount,
                discountAmount = discount,
                netAmount = netAmount,
                paymentMethod = paymentMethod.name,
                dateMillis = System.currentTimeMillis(),
                staffName = staff,
                notes = notes
            )

            val result = repository.executeSale(order, selectedItems)
            result.onSuccess { orderId ->
                _feedbackMessage.emit("Receipt generated: $receiptNumber (Total: ₹$netAmount)")
                onSuccess(order.copy(id = orderId))
            }.onFailure { err ->
                _feedbackMessage.emit("Sale failed: ${err.message}")
            }
        }
    }

    // Cancel Sale Order (Reverts stock)
    fun cancelSale(sale: SaleWithItems) {
        viewModelScope.launch {
            val result = repository.cancelSaleOrder(sale)
            result.onSuccess {
                _feedbackMessage.emit("Receipt ${sale.order.receiptNumber} cancelled and stock restored.")
            }.onFailure {
                _feedbackMessage.emit("Failed to cancel sale: ${it.message}")
            }
        }
    }

    // Suppliers
    fun saveSupplier(supplier: Supplier, isNew: Boolean) {
        viewModelScope.launch {
            val s = supplier.copy(schoolId = _schoolId.value)
            if (isNew) {
                repository.saveSupplier(s)
                _feedbackMessage.emit("Supplier added: ${s.name}")
            } else {
                repository.updateSupplier(s)
                _feedbackMessage.emit("Supplier updated: ${s.name}")
            }
        }
    }

    fun deleteSupplier(supplier: Supplier) {
        viewModelScope.launch {
            repository.deleteSupplier(supplier)
            _feedbackMessage.emit("Supplier removed: ${supplier.name}")
        }
    }

    // Students
    fun saveStudent(student: Student, isNew: Boolean) {
        viewModelScope.launch {
            val st = student.copy(schoolId = _schoolId.value)
            if (isNew) {
                repository.saveStudent(st)
                _feedbackMessage.emit("Student added: ${st.name}")
            } else {
                repository.updateStudent(st)
                _feedbackMessage.emit("Student updated: ${st.name}")
            }
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            repository.deleteStudent(student)
            _feedbackMessage.emit("Student removed: ${student.name}")
        }
    }

    // Users
    fun saveUser(user: User, isNew: Boolean) {
        viewModelScope.launch {
            val u = user.copy(schoolId = _schoolId.value)
            if (isNew) {
                repository.saveUser(u)
                _feedbackMessage.emit("Staff user created: ${u.fullName}")
            } else {
                repository.updateUser(u)
                _feedbackMessage.emit("Staff user updated: ${u.fullName}")
            }
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            repository.deleteUser(user)
            _feedbackMessage.emit("User removed: ${user.fullName}")
        }
    }

    // School Settings & Tenants
    fun updateSchoolSettings(school: School) {
        viewModelScope.launch {
            repository.saveSchool(school)
            _feedbackMessage.emit("School settings saved successfully!")
        }
    }

    fun createNewSchoolTenant(school: School, adminPassword: String) {
        viewModelScope.launch {
            repository.saveSchool(school)
            // Create default admin for that school
            val adminUser = User(
                schoolId = school.id,
                username = "admin",
                password = adminPassword.ifBlank { "admin123" },
                fullName = "${school.name} Admin",
                email = school.email,
                role = UserRole.SUPER_ADMIN
            )
            repository.saveUser(adminUser)
            _feedbackMessage.emit("New School '${school.name}' created!")
            switchSchool(school.id)
        }
    }

    // Export Excel / CSV
    fun exportStock(context: Context) {
        val csv = ExportHelper.generateStockCsv(stockItems.value)
        val fileName = "Learnium_Stock_${System.currentTimeMillis()}.csv"
        ExportHelper.shareCsv(context, fileName, csv)
    }

    fun exportSales(context: Context) {
        val csv = ExportHelper.generateSalesCsv(sales.value)
        val fileName = "Learnium_Sales_${System.currentTimeMillis()}.csv"
        ExportHelper.shareCsv(context, fileName, csv)
    }

    fun exportPurchases(context: Context) {
        val csv = ExportHelper.generatePurchasesCsv(transactions.value)
        val fileName = "Learnium_Purchases_${System.currentTimeMillis()}.csv"
        ExportHelper.shareCsv(context, fileName, csv)
    }

    fun exportStudents(context: Context) {
        val csv = ExportHelper.generateStudentsCsv(students.value)
        val fileName = "Learnium_Students_${System.currentTimeMillis()}.csv"
        ExportHelper.shareCsv(context, fileName, csv)
    }

    fun exportSuppliers(context: Context) {
        val csv = ExportHelper.generateSuppliersCsv(suppliers.value)
        val fileName = "Learnium_Suppliers_${System.currentTimeMillis()}.csv"
        ExportHelper.shareCsv(context, fileName, csv)
    }
}
