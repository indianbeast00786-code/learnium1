package com.example.data.seed

import com.example.data.local.AppDatabase
import com.example.data.model.ItemCategory
import com.example.data.model.PaymentMethod
import com.example.data.model.SaleOrder
import com.example.data.model.SaleOrderItem
import com.example.data.model.School
import com.example.data.model.StockItem
import com.example.data.model.StockTransaction
import com.example.data.model.Student
import com.example.data.model.Supplier
import com.example.data.model.User
import com.example.data.model.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SeedData {
    const val DEFAULT_SCHOOL_ID = "learnium_intl"

    suspend fun populateIfEmpty(database: AppDatabase) = withContext(Dispatchers.IO) {
        val schoolDao = database.schoolDao()
        val existingSchool = schoolDao.getSchoolByIdDirect(DEFAULT_SCHOOL_ID)
        if (existingSchool != null) return@withContext

        // 1. School
        val school = School(
            id = DEFAULT_SCHOOL_ID,
            name = "Learnium International School",
            code = "LIS-001",
            address = "Knowledge Park IV, Greater Noida, Delhi NCR - 201310",
            phone = "+91 98765 43210",
            email = "store@learnium.edu.in",
            gstNumber = "07AAAAA0000A1Z5",
            academicSession = "2026-2027",
            currencySymbol = "₹",
            receiptNotes = "Terms: Goods once sold can only be exchanged within 7 days with valid receipt in original condition."
        )
        schoolDao.insertOrUpdate(school)

        // 2. Users
        val userDao = database.userDao()
        userDao.insertUser(
            User(
                schoolId = DEFAULT_SCHOOL_ID,
                username = "admin",
                password = "admin123",
                fullName = "Dr. Alok Verma",
                email = "principal@learnium.edu.in",
                role = UserRole.SUPER_ADMIN
            )
        )
        userDao.insertUser(
            User(
                schoolId = DEFAULT_SCHOOL_ID,
                username = "manager",
                password = "manager123",
                fullName = "Rajesh Sharma",
                email = "store.manager@learnium.edu.in",
                role = UserRole.STORE_MANAGER
            )
        )
        userDao.insertUser(
            User(
                schoolId = DEFAULT_SCHOOL_ID,
                username = "accountant",
                password = "acc123",
                fullName = "Pooja Malhotra",
                email = "accounts@learnium.edu.in",
                role = UserRole.ACCOUNTANT
            )
        )
        userDao.insertUser(
            User(
                schoolId = DEFAULT_SCHOOL_ID,
                username = "staff",
                password = "staff123",
                fullName = "Sunil Rawat",
                email = "sunil.rawat@learnium.edu.in",
                role = UserRole.STAFF
            )
        )

        // 3. Suppliers
        val supplierDao = database.supplierDao()
        val s1 = supplierDao.insertSupplier(
            Supplier(
                schoolId = DEFAULT_SCHOOL_ID,
                name = "Oxford School Uniforms & Tailors",
                contactPerson = "Vikram Mehta",
                phone = "+91 98111 22334",
                email = "oxforduniforms@gmail.com",
                address = "Sector 18, Noida Commercial Belt",
                gstNumber = "07AAACU1234F1Z8",
                notes = "Primary supplier for school shirts, trousers, skirts, blazers and ties."
            )
        )
        val s2 = supplierDao.insertSupplier(
            Supplier(
                schoolId = DEFAULT_SCHOOL_ID,
                name = "Liberty Footwear & Sports Dist.",
                contactPerson = "Anil Grover",
                phone = "+91 98222 33445",
                email = "libertyschool@footwear.com",
                address = "Chandni Chowk Market, Delhi",
                gstNumber = "07AABCL5678K1ZQ",
                notes = "Official supplier for black school shoes and PT sneakers."
            )
        )
        val s3 = supplierDao.insertSupplier(
            Supplier(
                schoolId = DEFAULT_SCHOOL_ID,
                name = "NCERT & Cambridge Book Depot",
                contactPerson = "Dinesh Gupta",
                phone = "+91 98333 44556",
                email = "ncertdepot@delhibooks.in",
                address = "Daryaganj Book Market, New Delhi",
                gstNumber = "07AACDN9012M1ZW",
                notes = "School academic textbooks, reference books and lab manuals."
            )
        )
        val s4 = supplierDao.insertSupplier(
            Supplier(
                schoolId = DEFAULT_SCHOOL_ID,
                name = "Classmate & Navneet Stationers",
                contactPerson = "Rakesh Singhania",
                phone = "+91 98444 55667",
                email = "classmate.delhi@stationery.com",
                address = "Okhla Industrial Area Phase II, New Delhi",
                gstNumber = "07AADCR3456P1ZX",
                notes = "Notebooks, long registers, drawing books and practical copies."
            )
        )
        val s5 = supplierDao.insertSupplier(
            Supplier(
                schoolId = DEFAULT_SCHOOL_ID,
                name = "Crown Accessories & Badges Ltd",
                contactPerson = "S. K. Jain",
                phone = "+91 98555 66778",
                email = "crownbadges@gmail.com",
                address = "Karol Bagh, New Delhi",
                gstNumber = "07AAECC7890R1ZC",
                notes = "Ties, belts, identity cards, badges and water bottles."
            )
        )

        // 4. Students
        val studentDao = database.studentDao()
        studentDao.insertStudent(
            Student(
                schoolId = DEFAULT_SCHOOL_ID,
                studentId = "LIS-2026-101",
                name = "Aarav Patel",
                className = "Class 8",
                section = "A",
                parentName = "Ramesh Patel",
                parentPhone = "+91 98765 11223"
            )
        )
        studentDao.insertStudent(
            Student(
                schoolId = DEFAULT_SCHOOL_ID,
                studentId = "LIS-2026-102",
                name = "Ananya Sharma",
                className = "Class 6",
                section = "B",
                parentName = "Rohit Sharma",
                parentPhone = "+91 98765 22334"
            )
        )
        studentDao.insertStudent(
            Student(
                schoolId = DEFAULT_SCHOOL_ID,
                studentId = "LIS-2026-103",
                name = "Vihaan Gupta",
                className = "Class 10",
                section = "A",
                parentName = "Alok Gupta",
                parentPhone = "+91 98765 33445"
            )
        )
        studentDao.insertStudent(
            Student(
                schoolId = DEFAULT_SCHOOL_ID,
                studentId = "LIS-2026-104",
                name = "Diya Mukherjee",
                className = "Class 4",
                section = "C",
                parentName = "S. Mukherjee",
                parentPhone = "+91 98765 44556"
            )
        )
        studentDao.insertStudent(
            Student(
                schoolId = DEFAULT_SCHOOL_ID,
                studentId = "LIS-2026-105",
                name = "Kabir Singh",
                className = "Class 9",
                section = "A",
                parentName = "Jaspal Singh",
                parentPhone = "+91 98765 55667"
            )
        )

        // 5. Stock Items
        val stockDao = database.stockItemDao()

        // Uniforms
        val u1 = stockDao.insertItem(
            StockItem(
                schoolId = DEFAULT_SCHOOL_ID,
                name = "School Shirt (White Half Sleeve)",
                category = ItemCategory.UNIFORM.name,
                subCategory = "School Shirt",
                sku = "LIS-UNF-001",
                targetClass = "Class 5-8",
                gender = "Boys",
                size = "32",
                color = "Crisp White",
                purchasePrice = 280.0,
                sellingPrice = 380.0,
                openingStock = 100,
                currentStock = 85,
                minStockLevel = 20,
                supplierId = s1,
                supplierName = "Oxford School Uniforms & Tailors",
                description = "Premium combed poly-cotton shirt with Learnium school crest on chest pocket."
            )
        )
        stockDao.insertItem(
            StockItem(
                schoolId = DEFAULT_SCHOOL_ID,
                name = "School Sports Polo T-Shirt",
                category = ItemCategory.UNIFORM.name,
                subCategory = "Sports T-Shirt",
                sku = "LIS-UNF-002",
                targetClass = "All",
                gender = "Unisex",
                size = "M",
                color = "Navy Blue",
                purchasePrice = 220.0,
                sellingPrice = 320.0,
                openingStock = 80,
                currentStock = 60,
                minStockLevel = 15,
                supplierId = s1,
                supplierName = "Oxford School Uniforms & Tailors",
                description = "Moisture-wicking active dry fabric for sports sessions."
            )
        )
        val u3 = stockDao.insertItem(
            StockItem(
                schoolId = DEFAULT_SCHOOL_ID,
                name = "School Trouser (Navy Pleated)",
                category = ItemCategory.UNIFORM.name,
                subCategory = "School Trouser",
                sku = "LIS-UNF-003",
                targetClass = "Class 6-10",
                gender = "Boys",
                size = "30",
                color = "Dark Navy",
                purchasePrice = 380.0,
                sellingPrice = 520.0,
                openingStock = 50,
                currentStock = 45,
                minStockLevel = 15,
                supplierId = s1,
                supplierName = "Oxford School Uniforms & Tailors"
            )
        )
        stockDao.insertItem(
            StockItem(
                schoolId = DEFAULT_SCHOOL_ID,
                name = "School Skirt (Navy Box Pleat)",
                category = ItemCategory.UNIFORM.name,
                subCategory = "School Skirt",
                sku = "LIS-UNF-004",
                targetClass = "Class 1-5",
                gender = "Girls",
                size = "26",
                color = "Dark Navy",
                purchasePrice = 310.0,
                sellingPrice = 420.0,
                openingStock = 40,
                currentStock = 35,
                minStockLevel = 10,
                supplierId = s1,
                supplierName = "Oxford School Uniforms & Tailors"
            )
        )
        stockDao.insertItem(
            StockItem(
                schoolId = DEFAULT_SCHOOL_ID,
                name = "School Track Pant",
                category = ItemCategory.UNIFORM.name,
                subCategory = "School Track Pant",
                sku = "LIS-UNF-005",
                targetClass = "Class 1-12",
                gender = "Unisex",
                size = "L",
                color = "Navy & Gold",
                purchasePrice = 350.0,
                sellingPrice = 480.0,
                openingStock = 30,
                currentStock = 28,
                minStockLevel = 10,
                supplierId = s1,
                supplierName = "Oxford School Uniforms & Tailors"
            )
        )
        stockDao.insertItem(
            StockItem(
                schoolId = DEFAULT_SCHOOL_ID,
                name = "School Blazer (Woolen Crested)",
                category = ItemCategory.UNIFORM.name,
                subCategory = "School Blazer",
                sku = "LIS-UNF-006",
                targetClass = "Class 9-12",
                gender = "Unisex",
                size = "36",
                color = "Deep Navy",
                purchasePrice = 1100.0,
                sellingPrice = 1550.0,
                openingStock = 12,
                currentStock = 8,
                minStockLevel = 10, // LOW STOCK
                supplierId = s1,
                supplierName = "Oxford School Uniforms & Tailors"
            )
        )
        stockDao.insertItem(
            StockItem(
                schoolId = DEFAULT_SCHOOL_ID,
                name = "School V-Neck Sweater (Maroon)",
                category = ItemCategory.UNIFORM.name,
                subCategory = "School Sweater",
                sku = "LIS-UNF-007",
                targetClass = "Class 1-8",
                gender = "Unisex",
                size = "32",
                color = "Maroon",
                purchasePrice = 450.0,
                sellingPrice = 620.0,
                openingStock = 50,
                currentStock = 40,
                minStockLevel = 15,
                supplierId = s1,
                supplierName = "Oxford School Uniforms & Tailors"
            )
        )
        stockDao.insertItem(
            StockItem(
                schoolId = DEFAULT_SCHOOL_ID,
                name = "House Dress (Phoenix Red)",
                category = ItemCategory.UNIFORM.name,
                subCategory = "School House Dress",
                sku = "LIS-UNF-008",
                targetClass = "Class 5-10",
                gender = "Unisex",
                size = "M",
                color = "Phoenix Red",
                purchasePrice = 260.0,
                sellingPrice = 370.0,
                openingStock = 60,
                currentStock = 50,
                minStockLevel = 15,
                supplierId = s1,
                supplierName = "Oxford School Uniforms & Tailors"
            )
        )
        stockDao.insertItem(
            StockItem(
                schoolId = DEFAULT_SCHOOL_ID,
                name = "Sports Breathable Shorts",
                category = ItemCategory.UNIFORM.name,
                subCategory = "Sports Shorts",
                sku = "LIS-UNF-009",
                targetClass = "Class 1-8",
                gender = "Unisex",
                size = "28",
                color = "Navy Blue",
                purchasePrice = 180.0,
                sellingPrice = 260.0,
                openingStock = 20,
                currentStock = 0, // OUT OF STOCK
                minStockLevel = 15,
                supplierId = s1,
                supplierName = "Oxford School Uniforms & Tailors"
            )
        )

        // Shoes
        stockDao.insertItem(
            StockItem(
                schoolId = DEFAULT_SCHOOL_ID,
                name = "School Black Oxford Shoes",
                category = ItemCategory.SHOES.name,
                subCategory = "School Shoes",
                sku = "LIS-SHO-001",
                targetClass = "Class 6-10",
                gender = "Boys",
                size = "7",
                brandOrPublisher = "Liberty",
                purchasePrice = 520.0,
                sellingPrice = 750.0,
                openingStock = 30,
                currentStock = 24,
                minStockLevel = 8,
                supplierId = s2,
                supplierName = "Liberty Footwear & Sports Dist.",
                description = "Genuine leather formal school shoes with cushioned insole."
            )
        )
        stockDao.insertItem(
            StockItem(
                schoolId = DEFAULT_SCHOOL_ID,
                name = "School White PT Sports Shoes",
                category = ItemCategory.SHOES.name,
                subCategory = "Sports Shoes",
                sku = "LIS-SHO-002",
                targetClass = "Class 1-8",
                gender = "Unisex",
                size = "5",
                brandOrPublisher = "Action",
                purchasePrice = 410.0,
                sellingPrice = 590.0,
                openingStock = 25,
                currentStock = 18,
                minStockLevel = 8,
                supplierId = s2,
                supplierName = "Liberty Footwear & Sports Dist."
            )
        )

        // Accessories
        val acc1 = stockDao.insertItem(
            StockItem(
                schoolId = DEFAULT_SCHOOL_ID,
                name = "Learnium Crest Silk Tie",
                category = ItemCategory.ACCESSORIES.name,
                subCategory = "School Tie",
                sku = "LIS-ACC-001",
                targetClass = "All",
                gender = "Unisex",
                size = "Standard",
                color = "Navy & Gold",
                purchasePrice = 65.0,
                sellingPrice = 120.0,
                openingStock = 150,
                currentStock = 110,
                minStockLevel = 25,
                supplierId = s5,
                supplierName = "Crown Accessories & Badges Ltd"
            )
        )
        val acc2 = stockDao.insertItem(
            StockItem(
                schoolId = DEFAULT_SCHOOL_ID,
                name = "School Leather Belt (Brass Buckle)",
                category = ItemCategory.ACCESSORIES.name,
                subCategory = "School Belt",
                sku = "LIS-ACC-002",
                targetClass = "All",
                gender = "Unisex",
                size = "30",
                color = "Navy / Black",
                purchasePrice = 75.0,
                sellingPrice = 140.0,
                openingStock = 120,
                currentStock = 95,
                minStockLevel = 20,
                supplierId = s5,
                supplierName = "Crown Accessories & Badges Ltd"
            )
        )
        stockDao.insertItem(
            StockItem(
                schoolId = DEFAULT_SCHOOL_ID,
                name = "School Cotton Socks (Pack of 2)",
                category = ItemCategory.ACCESSORIES.name,
                subCategory = "School Socks",
                sku = "LIS-ACC-003",
                targetClass = "All",
                gender = "Unisex",
                size = "M",
                color = "White with Navy stripes",
                purchasePrice = 35.0,
                sellingPrice = 70.0,
                openingStock = 200,
                currentStock = 150,
                minStockLevel = 30,
                supplierId = s5,
                supplierName = "Crown Accessories & Badges Ltd"
            )
        )
        stockDao.insertItem(
            StockItem(
                schoolId = DEFAULT_SCHOOL_ID,
                name = "Learnium Orthopedic School Bag",
                category = ItemCategory.ACCESSORIES.name,
                subCategory = "School Bag",
                sku = "LIS-ACC-005",
                targetClass = "Class 1-8",
                gender = "Unisex",
                size = "Large",
                color = "Navy Blue",
                purchasePrice = 580.0,
                sellingPrice = 850.0,
                openingStock = 30,
                currentStock = 22,
                minStockLevel = 8,
                supplierId = s5,
                supplierName = "Crown Accessories & Badges Ltd"
            )
        )

        // Books
        val b1 = stockDao.insertItem(
            StockItem(
                schoolId = DEFAULT_SCHOOL_ID,
                name = "NCERT Mathematics Class 10",
                category = ItemCategory.BOOKS.name,
                subCategory = "Mathematics Books",
                sku = "LIS-BOK-001",
                isbn = "978-8174506344",
                subject = "Mathematics",
                targetClass = "Class 10",
                brandOrPublisher = "NCERT",
                edition = "2026 Edition",
                purchasePrice = 130.0,
                sellingPrice = 160.0,
                openingStock = 80,
                currentStock = 65,
                minStockLevel = 15,
                supplierId = s3,
                supplierName = "NCERT & Cambridge Book Depot"
            )
        )
        val b2 = stockDao.insertItem(
            StockItem(
                schoolId = DEFAULT_SCHOOL_ID,
                name = "Cambridge Science Explorer Class 8",
                category = ItemCategory.BOOKS.name,
                subCategory = "Science Books",
                sku = "LIS-BOK-002",
                isbn = "978-1108453210",
                subject = "Science",
                targetClass = "Class 8",
                brandOrPublisher = "Cambridge University Press",
                edition = "3rd Edition",
                purchasePrice = 320.0,
                sellingPrice = 420.0,
                openingStock = 50,
                currentStock = 42,
                minStockLevel = 15,
                supplierId = s3,
                supplierName = "NCERT & Cambridge Book Depot"
            )
        )
        stockDao.insertItem(
            StockItem(
                schoolId = DEFAULT_SCHOOL_ID,
                name = "Sparsh Hindi Course B Class 9",
                category = ItemCategory.BOOKS.name,
                subCategory = "Hindi Books",
                sku = "LIS-BOK-005",
                isbn = "978-8174508218",
                subject = "Hindi",
                targetClass = "Class 9",
                brandOrPublisher = "NCERT",
                edition = "Latest",
                purchasePrice = 120.0,
                sellingPrice = 150.0,
                openingStock = 10,
                currentStock = 5, // LOW STOCK
                minStockLevel = 10,
                supplierId = s3,
                supplierName = "NCERT & Cambridge Book Depot"
            )
        )

        // Notebooks / Copies
        val c1 = stockDao.insertItem(
            StockItem(
                schoolId = DEFAULT_SCHOOL_ID,
                name = "4-Line English Notebook (172 Pages)",
                category = ItemCategory.COPIES.name,
                subCategory = "Class Notebook",
                sku = "LIS-CPY-001",
                targetClass = "Class 1-5",
                subject = "English",
                brandOrPublisher = "Classmate",
                pages = 172,
                purchasePrice = 32.0,
                sellingPrice = 50.0,
                openingStock = 300,
                currentStock = 240,
                minStockLevel = 50,
                supplierId = s4,
                supplierName = "Classmate & Navneet Stationers"
            )
        )
        val c2 = stockDao.insertItem(
            StockItem(
                schoolId = DEFAULT_SCHOOL_ID,
                name = "Single Line Long Register (240 Pages)",
                category = ItemCategory.COPIES.name,
                subCategory = "Register",
                sku = "LIS-CPY-003",
                targetClass = "Class 6-12",
                subject = "All Subjects",
                brandOrPublisher = "Navneet",
                pages = 240,
                purchasePrice = 55.0,
                sellingPrice = 85.0,
                openingStock = 180,
                currentStock = 130,
                minStockLevel = 40,
                supplierId = s4,
                supplierName = "Classmate & Navneet Stationers"
            )
        )

        // Custom Item
        stockDao.insertItem(
            StockItem(
                schoolId = DEFAULT_SCHOOL_ID,
                name = "Learnium Science Lab Coat (White)",
                category = ItemCategory.CUSTOM.name,
                subCategory = "Lab Equipment",
                sku = "LIS-CST-001",
                targetClass = "Class 9-12",
                gender = "Unisex",
                size = "M",
                purchasePrice = 290.0,
                sellingPrice = 420.0,
                openingStock = 35,
                currentStock = 25,
                minStockLevel = 10,
                supplierId = s1,
                supplierName = "Oxford School Uniforms & Tailors",
                description = "Cotton lab coat required for chemistry and biology practical examinations."
            )
        )

        // 6. Pre-populate sample transactions & initial sales history
        val transDao = database.stockTransactionDao()
        transDao.insertTransaction(
            StockTransaction(
                schoolId = DEFAULT_SCHOOL_ID,
                type = "PURCHASE",
                itemId = u1,
                itemName = "School Shirt (White Half Sleeve)",
                category = ItemCategory.UNIFORM.name,
                supplierId = s1,
                supplierName = "Oxford School Uniforms & Tailors",
                quantity = 100,
                unitCost = 280.0,
                totalCost = 28000.0,
                invoiceNumber = "OXF-2026-881",
                dateMillis = System.currentTimeMillis() - 86400000L * 5,
                notes = "Batch 1 delivery for academic term",
                staffName = "Rajesh Sharma"
            )
        )
        transDao.insertTransaction(
            StockTransaction(
                schoolId = DEFAULT_SCHOOL_ID,
                type = "PURCHASE",
                itemId = b1,
                itemName = "NCERT Mathematics Class 10",
                category = ItemCategory.BOOKS.name,
                supplierId = s3,
                supplierName = "NCERT & Cambridge Book Depot",
                quantity = 80,
                unitCost = 130.0,
                totalCost = 10400.0,
                invoiceNumber = "BOK-9921",
                dateMillis = System.currentTimeMillis() - 86400000L * 4,
                notes = "Received directly from distributor",
                staffName = "Rajesh Sharma"
            )
        )

        // Sample Sale Orders
        val saleDao = database.saleOrderDao()
        val sale1Id = saleDao.insertOrder(
            SaleOrder(
                schoolId = DEFAULT_SCHOOL_ID,
                receiptNumber = "LIS-REC-2026-0001",
                studentId = "LIS-2026-101",
                studentName = "Aarav Patel",
                className = "Class 8",
                section = "A",
                parentName = "Ramesh Patel",
                totalAmount = 885.0,
                discountAmount = 0.0,
                netAmount = 885.0,
                paymentMethod = PaymentMethod.UPI.name,
                dateMillis = System.currentTimeMillis() - 3600000L * 3,
                staffName = "Sunil Rawat",
                notes = "Full term starter kit issued"
            )
        )
        saleDao.insertOrderItems(
            listOf(
                SaleOrderItem(
                    saleOrderId = sale1Id,
                    itemId = u1,
                    itemName = "School Shirt (White Half Sleeve)",
                    category = ItemCategory.UNIFORM.name,
                    sku = "LIS-UNF-001",
                    size = "32",
                    quantity = 1,
                    unitPrice = 380.0,
                    totalPrice = 380.0
                ),
                SaleOrderItem(
                    saleOrderId = sale1Id,
                    itemId = acc1,
                    itemName = "Learnium Crest Silk Tie",
                    category = ItemCategory.ACCESSORIES.name,
                    sku = "LIS-ACC-001",
                    size = "Standard",
                    quantity = 1,
                    unitPrice = 120.0,
                    totalPrice = 120.0
                ),
                SaleOrderItem(
                    saleOrderId = sale1Id,
                    itemId = b2,
                    itemName = "Cambridge Science Explorer Class 8",
                    category = ItemCategory.BOOKS.name,
                    sku = "LIS-BOK-002",
                    size = "",
                    quantity = 1,
                    unitPrice = 420.0,
                    totalPrice = 420.0
                )
            )
        )

        val sale2Id = saleDao.insertOrder(
            SaleOrder(
                schoolId = DEFAULT_SCHOOL_ID,
                receiptNumber = "LIS-REC-2026-0002",
                studentId = "LIS-2026-102",
                studentName = "Ananya Sharma",
                className = "Class 6",
                section = "B",
                parentName = "Rohit Sharma",
                totalAmount = 275.0,
                discountAmount = 0.0,
                netAmount = 275.0,
                paymentMethod = PaymentMethod.CASH.name,
                dateMillis = System.currentTimeMillis() - 3600000L * 1,
                staffName = "Sunil Rawat",
                notes = "Stationery and belt purchase"
            )
        )
        saleDao.insertOrderItems(
            listOf(
                SaleOrderItem(
                    saleOrderId = sale2Id,
                    itemId = acc2,
                    itemName = "School Leather Belt (Brass Buckle)",
                    category = ItemCategory.ACCESSORIES.name,
                    sku = "LIS-ACC-002",
                    size = "30",
                    quantity = 1,
                    unitPrice = 140.0,
                    totalPrice = 140.0
                ),
                SaleOrderItem(
                    saleOrderId = sale2Id,
                    itemId = c1,
                    itemName = "4-Line English Notebook (172 Pages)",
                    category = ItemCategory.COPIES.name,
                    sku = "LIS-CPY-001",
                    size = "",
                    quantity = 1,
                    unitPrice = 50.0,
                    totalPrice = 50.0
                ),
                SaleOrderItem(
                    saleOrderId = sale2Id,
                    itemId = c2,
                    itemName = "Single Line Long Register (240 Pages)",
                    category = ItemCategory.COPIES.name,
                    sku = "LIS-CPY-003",
                    size = "",
                    quantity = 1,
                    unitPrice = 85.0,
                    totalPrice = 85.0
                )
            )
        )
    }
}
