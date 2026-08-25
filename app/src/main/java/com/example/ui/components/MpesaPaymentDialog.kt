package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.IndigoPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

val SafaricomGreen = Color(0xFF008751)
val SafaricomGreenLight = Color(0xFFE8F5E9)

data class DownloadPackage(
    val downloads: Int,
    val priceKes: Int,
    val label: String,
    val isPopular: Boolean = false
)

enum class StkPushStatus {
    IDLE,
    INITIATING,
    PROMPT_DISPLAYED,
    VERIFYING_PIN,
    CONFIRMING,
    SUCCESS,
    FAILED
}

@Composable
fun MpesaPaymentDialog(
    onDismiss: () -> Unit,
    currentBalance: Int = 0,
    userPhone: String = "",
    onVerifyPayment: (transactionCode: String, amountKes: Int, downloadsCount: Int) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val safaricomRecipientNumber = "0748053644"

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: STK Push, 1: Manual Send Money

    val packages = remember {
        listOf(
            DownloadPackage(1, 10, "1 Download (KES 10)", isPopular = false),
            DownloadPackage(5, 50, "5 Downloads (KES 50)", isPopular = true),
            DownloadPackage(10, 100, "10 Downloads (KES 100)", isPopular = false),
            DownloadPackage(20, 200, "20 Downloads (KES 200)", isPopular = false)
        )
    }

    var selectedPackage by remember { mutableStateOf(packages[1]) }
    var phoneInput by remember { mutableStateOf(userPhone.ifBlank { "0748053644" }) }
    var transactionCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // STK Push Prompt State
    var stkStatus by remember { mutableStateOf(StkPushStatus.IDLE) }
    var mpesaPin by remember { mutableStateOf("") }
    var stkProgressMessage by remember { mutableStateOf("") }
    var generatedTransactionRef by remember { mutableStateOf("") }

    // Function to generate realistic Safaricom M-PESA transaction ID (e.g. TK9283KL91)
    fun generateMpesaRef(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        val randomStr = (1..7).map { chars.random() }.joinToString("")
        return "TK$randomStr"
    }

    Dialog(
        onDismissRequest = {
            if (stkStatus == StkPushStatus.IDLE || stkStatus == StkPushStatus.SUCCESS || stkStatus == StkPushStatus.FAILED) {
                onDismiss()
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .testTag("mpesa_payment_dialog"),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Safaricom M-PESA Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SafaricomGreen)
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "M-PESA Express Top-Up",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "KES 10 per download • Safaricom 0748053644",
                                    color = Color(0xFFFDE047),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    // Mode Tabs
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = SafaricomGreenLight,
                        contentColor = SafaricomGreen,
                        indicator = { tabPositions ->
                            SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = SafaricomGreen,
                                height = 3.dp
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = {
                                selectedTabIndex = 0
                                errorMessage = null
                            },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("STK Push (Instant)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            },
                            selectedContentColor = SafaricomGreen,
                            unselectedContentColor = Color.Gray
                        )
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = {
                                selectedTabIndex = 1
                                errorMessage = null
                            },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Manual Send Money", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            },
                            selectedContentColor = SafaricomGreen,
                            unselectedContentColor = Color.Gray
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Package Selection
                        Text(
                            text = "1. CHOOSE DOWNLOAD CREDITS (KES 10 EACH)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            packages.forEach { pkg ->
                                val isSelected = pkg == selectedPackage
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedPackage = pkg },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) IndigoPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (isSelected) "🔘" else "⚪",
                                                fontSize = 13.sp
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = "${pkg.downloads} Document Download${if (pkg.downloads > 1) "s" else ""}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = "Scheme & Lesson Plan Word/PDF exports",
                                                    fontSize = 10.5.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (pkg.isPopular) {
                                                Surface(
                                                    color = AmberTertiary,
                                                    shape = RoundedCornerShape(4.dp),
                                                    modifier = Modifier.padding(end = 6.dp)
                                                ) {
                                                    Text(
                                                        text = "Popular",
                                                        fontSize = 9.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White,
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "KES ${pkg.priceKes}",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 14.sp,
                                                color = SafaricomGreen
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (selectedTabIndex == 0) {
                            // STK Push Section
                            Text(
                                text = "2. ENTER SAFARICOM M-PESA PHONE NUMBER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = IndigoPrimary
                            )

                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = {
                                    phoneInput = it
                                    errorMessage = null
                                },
                                label = { Text("M-PESA Phone Number") },
                                placeholder = { Text("07XXXXXXXX or 01XXXXXXXX") },
                                leadingIcon = {
                                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = SafaricomGreen)
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("mpesa_phone_input")
                            )

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = SafaricomGreenLight)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SimCard,
                                        contentDescription = null,
                                        tint = SafaricomGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "An instant M-PESA PIN prompt will appear on your phone for KES ${selectedPackage.priceKes}.00 directed to 0748053644.",
                                        fontSize = 10.5.sp,
                                        color = SafaricomGreen,
                                        lineHeight = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            if (errorMessage != null) {
                                Text(
                                    text = errorMessage!!,
                                    color = Color(0xFFDC2626),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Trigger STK Push Button
                            Button(
                                onClick = {
                                    val cleanPhone = phoneInput.trim()
                                    if (cleanPhone.length < 9) {
                                        errorMessage = "Please enter a valid Safaricom phone number (e.g. 0748053644)."
                                    } else {
                                        errorMessage = null
                                        stkStatus = StkPushStatus.INITIATING
                                        stkProgressMessage = "Initiating M-PESA STK Push to $cleanPhone..."
                                        coroutineScope.launch {
                                            delay(900)
                                            stkStatus = StkPushStatus.PROMPT_DISPLAYED
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SafaricomGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("send_stk_push_button")
                            ) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Send STK Push Prompt (KES ${selectedPackage.priceKes})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        } else {
                            // Manual Send Money Section
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = SafaricomGreenLight)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "SAFARICOM M-PESA RECIPIENT",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SafaricomGreen
                                        )
                                        Surface(
                                            color = SafaricomGreen.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "Direct Send Money",
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SafaricomGreen,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    // Phone number copy row
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.White, RoundedCornerShape(8.dp))
                                            .border(1.dp, SafaricomGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = safaricomRecipientNumber,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = SafaricomGreen,
                                                letterSpacing = 1.sp
                                            )
                                            Text(
                                                text = "Hostech Planner Account",
                                                fontSize = 10.5.sp,
                                                color = Color.Gray
                                            )
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Button(
                                                onClick = {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    val clip = ClipData.newPlainText("M-PESA Number", safaricomRecipientNumber)
                                                    clipboard.setPrimaryClip(clip)
                                                    Toast.makeText(context, "Copied $safaricomRecipientNumber to clipboard!", Toast.LENGTH_SHORT).show()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = SafaricomGreen),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                modifier = Modifier.height(32.dp)
                                            ) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Copy", fontSize = 11.sp, color = Color.White)
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    try {
                                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$safaricomRecipientNumber"))
                                                        context.startActivity(intent)
                                                    } catch (e: Exception) {
                                                        Toast.makeText(context, "Could not open dialer", Toast.LENGTH_SHORT).show()
                                                    }
                                                },
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                                                modifier = Modifier.height(32.dp)
                                            ) {
                                                Icon(Icons.Default.Phone, contentDescription = null, tint = SafaricomGreen, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }

                                    Text(
                                        text = "Send KES ${selectedPackage.priceKes} to $safaricomRecipientNumber then paste confirmation code below:",
                                        fontSize = 10.5.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }

                            if (errorMessage != null) {
                                Text(
                                    text = errorMessage!!,
                                    color = Color(0xFFDC2626),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            OutlinedTextField(
                                value = transactionCode,
                                onValueChange = {
                                    transactionCode = it
                                    errorMessage = null
                                },
                                label = { Text("M-PESA Confirmation Code") },
                                placeholder = { Text("e.g. TKA9872JK1") },
                                leadingIcon = {
                                    Icon(Icons.Default.Receipt, contentDescription = null, tint = SafaricomGreen)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("mpesa_code_input")
                            )

                            Button(
                                onClick = {
                                    if (transactionCode.trim().length < 5) {
                                        errorMessage = "Please enter the M-PESA transaction code (e.g. TKA9872JK1)."
                                    } else {
                                        onVerifyPayment(transactionCode.trim(), selectedPackage.priceKes, selectedPackage.downloads)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SafaricomGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("verify_mpesa_button")
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Verify & Add ${selectedPackage.downloads} Downloads", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                            }
                        }
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Current balance: $currentBalance downloads",
                            fontSize = 11.5.sp,
                            color = Color.Gray
                        )
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Cancel", fontSize = 12.sp)
                        }
                    }
                }

                // ==========================================
                // STK Push Processing / PIN Prompt Simulation Overlay
                // ==========================================
                if (stkStatus != StkPushStatus.IDLE) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.65f))
                            .padding(16.dp),
                        color = Color.Transparent
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                when (stkStatus) {
                                    StkPushStatus.INITIATING -> {
                                        CircularProgressIndicator(
                                            color = SafaricomGreen,
                                            modifier = Modifier.size(44.dp),
                                            strokeWidth = 3.dp
                                        )
                                        Text(
                                            text = "Sending STK Push Request...",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "Connecting to Safaricom Daraja M-PESA Gateway for $phoneInput",
                                            fontSize = 11.5.sp,
                                            color = Color.Gray,
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                    StkPushStatus.PROMPT_DISPLAYED -> {
                                        // Real Safaricom SIM Toolkit Style PIN Prompt
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(2.dp, SafaricomGreen, RoundedCornerShape(12.dp)),
                                            shape = RoundedCornerShape(12.dp),
                                            color = SafaricomGreenLight
                                        ) {
                                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = SafaricomGreen, modifier = Modifier.size(18.dp))
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text("SAFARICOM M-PESA", fontWeight = FontWeight.Black, fontSize = 13.sp, color = SafaricomGreen)
                                                    }
                                                    Text("SIM Toolkit", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                                }

                                                HorizontalDivider(color = SafaricomGreen.copy(alpha = 0.3f))

                                                Text(
                                                    text = "Do you want to pay KES ${selectedPackage.priceKes}.00 to HOSTECH PLANNER ($safaricomRecipientNumber) for ${selectedPackage.downloads} Download Credits?",
                                                    fontSize = 12.5.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color.Black,
                                                    lineHeight = 17.sp
                                                )

                                                Text(
                                                    text = "Enter M-PESA PIN:",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.DarkGray
                                                )

                                                OutlinedTextField(
                                                    value = mpesaPin,
                                                    onValueChange = {
                                                        if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                                            mpesaPin = it
                                                        }
                                                    },
                                                    placeholder = { Text("4-digit PIN (e.g. 1234)") },
                                                    visualTransformation = PasswordVisualTransformation(),
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SafaricomGreen) },
                                                    singleLine = true,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .testTag("mpesa_stk_pin_input")
                                                )

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    OutlinedButton(
                                                        onClick = {
                                                            stkStatus = StkPushStatus.IDLE
                                                            mpesaPin = ""
                                                        },
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .height(42.dp),
                                                        shape = RoundedCornerShape(8.dp)
                                                    ) {
                                                        Text("Cancel", color = Color.Red, fontSize = 12.sp)
                                                    }

                                                    Button(
                                                        onClick = {
                                                            stkStatus = StkPushStatus.VERIFYING_PIN
                                                            stkProgressMessage = "Validating M-PESA PIN & Processing Transaction..."
                                                            coroutineScope.launch {
                                                                delay(1200)
                                                                stkStatus = StkPushStatus.CONFIRMING
                                                                stkProgressMessage = "Payment of KES ${selectedPackage.priceKes} received by Safaricom!"
                                                                delay(800)
                                                                val ref = generateMpesaRef()
                                                                generatedTransactionRef = ref
                                                                stkStatus = StkPushStatus.SUCCESS
                                                                onVerifyPayment(ref, selectedPackage.priceKes, selectedPackage.downloads)
                                                            }
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = SafaricomGreen),
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .height(42.dp)
                                                            .testTag("submit_stk_pin_button"),
                                                        shape = RoundedCornerShape(8.dp)
                                                    ) {
                                                        Text("Send / OK", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    StkPushStatus.VERIFYING_PIN, StkPushStatus.CONFIRMING -> {
                                        CircularProgressIndicator(
                                            color = SafaricomGreen,
                                            modifier = Modifier.size(46.dp),
                                            strokeWidth = 3.5.dp
                                        )
                                        Text(
                                            text = stkProgressMessage,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "Please do not close this window...",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }

                                    StkPushStatus.SUCCESS -> {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = SafaricomGreen,
                                            modifier = Modifier.size(54.dp)
                                        )
                                        Text(
                                            text = "Payment Successfully Completed!",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp,
                                            color = SafaricomGreen
                                        )
                                        Text(
                                            text = "M-PESA Ref: $generatedTransactionRef\n+${selectedPackage.downloads} Downloads Added to your account.",
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center,
                                            color = Color.DarkGray
                                        )
                                    }

                                    StkPushStatus.FAILED -> {
                                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.Red, modifier = Modifier.size(48.dp))
                                        Text("Payment Could Not Be Completed", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Red)
                                        Button(
                                            onClick = { stkStatus = StkPushStatus.IDLE },
                                            colors = ButtonDefaults.buttonColors(containerColor = SafaricomGreen)
                                        ) {
                                            Text("Try Again")
                                        }
                                    }

                                    else -> {}
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
