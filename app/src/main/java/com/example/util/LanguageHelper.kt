package com.example.util

data class SupportedLanguage(
    val code: String,
    val name: String,
    val nativeName: String
)

object LanguageHelper {
    val languages = listOf(
        SupportedLanguage("en", "English", "English"),
        SupportedLanguage("hi", "Hindi", "हिन्दी"),
        SupportedLanguage("bn", "Bengali", "বাংলা"),
        SupportedLanguage("gu", "Gujarati", "ગુજરાતી"),
        SupportedLanguage("kn", "Kannada", "ಕನ್ನಡ"),
        SupportedLanguage("ml", "Malayalam", "മലയാളം"),
        SupportedLanguage("mr", "Marathi", "मराठी"),
        SupportedLanguage("or", "Odia", "ଓଡ଼ିଆ"),
        SupportedLanguage("pa", "Punjabi", "ਪੰਜਾਬੀ"),
        SupportedLanguage("ta", "Tamil", "தமிழ்"),
        SupportedLanguage("te", "Telugu", "తెలుగు"),
        SupportedLanguage("ur", "Urdu", "اردو"),
        SupportedLanguage("as", "Assamese", "অসমীয়া")
    )

    private val translations = mapOf(
        "en" to mapOf(
            "app_title" to "Learnium School Inventory",
            "dashboard" to "Dashboard",
            "stock" to "Stock / Inventory",
            "stock_in" to "Stock In / Purchase",
            "stock_out" to "Stock Out / Sales",
            "students" to "Students",
            "suppliers" to "Suppliers",
            "reports" to "Reports & Analytics",
            "users" to "Users & Roles",
            "settings" to "School Settings",
            "logout" to "Sign Out",
            "search_hint" to "Search items, SKU, student, ISBN...",
            "filter" to "Filter",
            "export_excel" to "Export to Excel / CSV",
            "add_item" to "Add Stock Item",
            "total_items" to "Total Stock Items",
            "total_quantity" to "Total Units in Stock",
            "low_stock" to "Low Stock Items",
            "out_of_stock" to "Out of Stock",
            "today_sales" to "Today's Issues / Sales",
            "today_revenue" to "Today's Revenue",
            "total_books" to "Total Books",
            "total_uniforms" to "Total Uniforms",
            "recent_transactions" to "Recent Transactions",
            "low_stock_alerts" to "Low Stock Alerts",
            "purchase_price" to "Purchase Price",
            "selling_price" to "Selling Price",
            "current_stock" to "Current Stock",
            "min_level" to "Min Level",
            "status" to "Status",
            "action" to "Action",
            "receipt" to "Bill / Receipt",
            "print" to "Print Receipt",
            "share" to "Share Receipt",
            "download" to "Download"
        ),
        "hi" to mapOf(
            "app_title" to "लर्नियम स्कूल इन्वेंट्री",
            "dashboard" to "डैशबोर्ड",
            "stock" to "स्टॉक / इन्वेंट्री",
            "stock_in" to "स्टॉक इन / खरीद",
            "stock_out" to "स्टॉक आउट / बिक्री",
            "students" to "छात्र रिकॉर्ड",
            "suppliers" to "आपूर्तिकर्ता",
            "reports" to "रिपोर्ट और विश्लेषण",
            "users" to "उपयोगकर्ता और भूमिकाएँ",
            "settings" to "स्कूल सेटिंग्स",
            "logout" to "लॉग आउट",
            "search_hint" to "आइटम, एसकेयू, छात्र, आईएसबीएन खोजें...",
            "filter" to "फ़िल्टर",
            "export_excel" to "एक्सेल / सीएसवी में निर्यात करें",
            "add_item" to "नया आइटम जोड़ें",
            "total_items" to "कुल स्टॉक आइटम",
            "total_quantity" to "स्टॉक में कुल इकाइयां",
            "low_stock" to "कम स्टॉक आइटम",
            "out_of_stock" to "स्टॉक समाप्त",
            "today_sales" to "आज की बिक्री / निर्गमन",
            "today_revenue" to "आज का राजस्व",
            "total_books" to "कुल पुस्तकें",
            "total_uniforms" to "कुल वर्दी आइटम",
            "recent_transactions" to "हाल के लेन-देन",
            "low_stock_alerts" to "कम स्टॉक चेतावनियां",
            "purchase_price" to "खरीद मूल्य",
            "selling_price" to "बिक्री मूल्य",
            "current_stock" to "वर्तमान स्टॉक",
            "min_level" to "न्यूनतम स्तर",
            "status" to "स्थिति",
            "action" to "कार्रवाई",
            "receipt" to "बिल / रसीद",
            "print" to "रसीद प्रिंट करें",
            "share" to "रसीद साझा करें",
            "download" to "डाउनलोड करें"
        )
    )

    fun getString(langCode: String, key: String): String {
        return translations[langCode]?.get(key)
            ?: translations["en"]?.get(key)
            ?: key
    }
}
