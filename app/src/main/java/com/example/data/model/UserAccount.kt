package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "user_accounts")
data class UserAccount(
    @PrimaryKey
    val username: String,
    val passwordHash: String, // Kept locally for credentials retention & sync
    val fullName: String = "",
    val schoolName: String = "JUNIOR SECONDARY SCHOOL",
    val tscNumber: String = "",
    val phone: String = "",
    val freeDownloadsRemaining: Int = 3, // New users get up to 3 free downloads
    val paidDownloadsRemaining: Int = 0, // Pay KES 10 per download afterwards
    val totalDownloadsUsed: Int = 0,
    val isLoggedIn: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val lastSyncedAt: Long = System.currentTimeMillis()
) {
    val totalAvailableDownloads: Int
        get() = freeDownloadsRemaining + paidDownloadsRemaining

    val isEligibleForFreeDownload: Boolean
        get() = freeDownloadsRemaining > 0

    fun formattedSyncTime(): String {
        return try {
            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            sdf.format(Date(lastSyncedAt))
        } catch (e: Exception) {
            "Just now"
        }
    }
}

@Entity(tableName = "payment_transactions")
data class PaymentTransaction(
    @PrimaryKey
    val transactionId: String, // e.g. M-PESA Code TK89XYZ123
    val username: String,
    val amountKes: Int,
    val downloadsAdded: Int,
    val recipientPhone: String = "0748053644",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "CONFIRMED"
) {
    fun formattedTime(): String {
        return try {
            val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
            sdf.format(Date(timestamp))
        } catch (e: Exception) {
            ""
        }
    }
}
