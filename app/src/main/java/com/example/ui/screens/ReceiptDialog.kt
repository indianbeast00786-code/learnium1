package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.SaleOrder
import com.example.data.model.SaleOrderItem
import com.example.data.model.School
import com.example.ui.theme.SchoolGold
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import com.example.util.FormatHelper

@Composable
fun ReceiptDialog(
    order: SaleOrder,
    items: List<SaleOrderItem>,
    school: School?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val schoolName = school?.name ?: "Learnium International School"
    val schoolAddress = school?.address ?: "Knowledge Park IV, Greater Noida, Delhi NCR"
    val schoolPhone = school?.phone ?: "+91 98765 43210"
    val currency = school?.currencySymbol ?: "₹"

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
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top close action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Official Bill / Receipt",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Printable Receipt Canvas Container
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Slate200, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        // School Header
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(SchoolNavy),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = SchoolGold,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = schoolName.uppercase(),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp,
                                color = SchoolNavy,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = schoolAddress,
                                fontSize = 11.sp,
                                color = Slate600,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Tel: $schoolPhone | GST: ${school?.gstNumber ?: "07AAAAA0000A1Z5"}",
                                fontSize = 11.sp,
                                color = Slate600,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(thickness = 1.dp, color = Slate200)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Receipt & Student Details Meta
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "RECEIPT NO:", fontSize = 11.sp, color = Slate600, fontWeight = FontWeight.Bold)
                                Text(text = order.receiptNumber, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Slate800)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "STUDENT NAME:", fontSize = 11.sp, color = Slate600, fontWeight = FontWeight.Bold)
                                Text(text = order.studentName.ifBlank { "Walk-in Buyer" }, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Slate800)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "DATE & TIME:", fontSize = 11.sp, color = Slate600, fontWeight = FontWeight.Bold)
                                Text(text = FormatHelper.formatDate(order.dateMillis), fontSize = 12.sp, color = Slate800)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "CLASS & SECTION:", fontSize = 11.sp, color = Slate600, fontWeight = FontWeight.Bold)
                                Text(
                                    text = if (order.className.isNotBlank()) "${order.className} ${order.section}".trim() else "-",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Slate800
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Items Table Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Slate100, RoundedCornerShape(6.dp))
                                .padding(vertical = 8.dp, horizontal = 10.dp)
                        ) {
                            Text(text = "ITEM", modifier = Modifier.weight(2.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate800)
                            Text(text = "QTY", modifier = Modifier.weight(0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = Slate800)
                            Text(text = "RATE", modifier = Modifier.weight(1.2f), fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, color = Slate800)
                            Text(text = "AMOUNT", modifier = Modifier.weight(1.3f), fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, color = Slate800)
                        }

                        // Items List
                        items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 7.dp, horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(2.5f)) {
                                    Text(text = item.itemName, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Slate800)
                                    if (item.size.isNotBlank() || item.sku.isNotBlank()) {
                                        Text(
                                            text = listOf(item.sku, if (item.size.isNotBlank()) "Size: ${item.size}" else "").filter { it.isNotBlank() }.joinToString(" • "),
                                            fontSize = 10.sp,
                                            color = Slate600
                                        )
                                    }
                                }
                                Text(text = "${item.quantity}", modifier = Modifier.weight(0.8f), fontSize = 12.sp, textAlign = TextAlign.Center, color = Slate800)
                                Text(text = FormatHelper.formatCurrency(item.unitPrice, currency), modifier = Modifier.weight(1.2f), fontSize = 12.sp, textAlign = TextAlign.End, color = Slate800)
                                Text(text = FormatHelper.formatCurrency(item.totalPrice, currency), modifier = Modifier.weight(1.3f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.End, color = Slate800)
                            }
                            HorizontalDivider(thickness = 0.5.dp, color = Slate100)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Totals Summary
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(0.6f),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Subtotal:", fontSize = 12.sp, color = Slate600)
                                Text(text = FormatHelper.formatCurrency(order.totalAmount, currency), fontSize = 12.sp, color = Slate800)
                            }
                            if (order.discountAmount > 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(0.6f),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Discount:", fontSize = 12.sp, color = Color(0xFF16A34A))
                                    Text(text = "-${FormatHelper.formatCurrency(order.discountAmount, currency)}", fontSize = 12.sp, color = Color(0xFF16A34A))
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            HorizontalDivider(thickness = 1.dp, color = Slate200)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(0.6f),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "NET PAID:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SchoolNavy)
                                Text(
                                    text = FormatHelper.formatCurrency(order.netAmount, currency),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SchoolNavy
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(thickness = 1.dp, color = Slate200)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Footer Information
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Paid via: ${order.paymentMethod}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Slate800
                            )
                            Text(
                                text = "Issued by: ${order.staffName}",
                                fontSize = 11.sp,
                                color = Slate600
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = school?.receiptNotes ?: "Thank you! Goods once sold can be exchanged within 7 days.",
                            fontSize = 10.sp,
                            color = Slate600,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { shareReceipt(context, order, items, school) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Receipt")
                    }

                    OutlinedButton(
                        onClick = { printReceipt(context, order, items, school) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Print / PDF")
                    }
                }
            }
        }
    }
}

private fun formatReceiptPlainText(
    order: SaleOrder,
    items: List<SaleOrderItem>,
    school: School?
): String {
    val schoolName = school?.name ?: "Learnium International School"
    val currency = school?.currencySymbol ?: "₹"
    val sb = StringBuilder()
    sb.append("========================================\n")
    sb.append("       $schoolName\n")
    sb.append("         OFFICIAL STORE RECEIPT\n")
    sb.append("========================================\n")
    sb.append("Receipt No: ${order.receiptNumber}\n")
    sb.append("Date: ${FormatHelper.formatDate(order.dateMillis)}\n")
    sb.append("Student: ${order.studentName} (${order.className} ${order.section})\n")
    sb.append("Student ID: ${order.studentId.ifBlank { "N/A" }}\n")
    sb.append("----------------------------------------\n")
    sb.append("ITEM                  QTY   PRICE   TOTAL\n")
    sb.append("----------------------------------------\n")
    for (i in items) {
        val name = if (i.itemName.length > 20) i.itemName.take(18) + ".." else i.itemName.padEnd(20)
        sb.append("$name  ${i.quantity}   ${FormatHelper.formatCurrency(i.unitPrice, currency)}   ${FormatHelper.formatCurrency(i.totalPrice, currency)}\n")
    }
    sb.append("----------------------------------------\n")
    sb.append("Subtotal: ${FormatHelper.formatCurrency(order.totalAmount, currency)}\n")
    if (order.discountAmount > 0) {
        sb.append("Discount: -${FormatHelper.formatCurrency(order.discountAmount, currency)}\n")
    }
    sb.append("NET TOTAL: ${FormatHelper.formatCurrency(order.netAmount, currency)}\n")
    sb.append("Payment: ${order.paymentMethod}\n")
    sb.append("Issued By: ${order.staffName}\n")
    sb.append("========================================\n")
    sb.append(school?.receiptNotes ?: "Thank you! Have a great academic session.\n")
    sb.append("========================================\n")
    return sb.toString()
}

private fun shareReceipt(
    context: Context,
    order: SaleOrder,
    items: List<SaleOrderItem>,
    school: School?
) {
    val receiptText = formatReceiptPlainText(order, items, school)
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_SUBJECT, "Receipt ${order.receiptNumber} - ${order.studentName}")
        putExtra(Intent.EXTRA_TEXT, receiptText)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share Receipt via WhatsApp, Email, etc.")
    context.startActivity(shareIntent)
}

private fun printReceipt(
    context: Context,
    order: SaleOrder,
    items: List<SaleOrderItem>,
    school: School?
) {
    // Standard Android share/send text to any printer service or PDF reader
    val receiptText = formatReceiptPlainText(order, items, school)
    val printIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TITLE, "Receipt-${order.receiptNumber}.txt")
        putExtra(Intent.EXTRA_TEXT, receiptText)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(printIntent, "Print or Save as PDF"))
}
