package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PaymentTransaction
import com.example.data.model.UserAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_accounts WHERE isLoggedIn = 1 LIMIT 1")
    fun getActiveUser(): Flow<UserAccount?>

    @Query("SELECT * FROM user_accounts WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getActiveUserSync(): UserAccount?

    @Query("SELECT * FROM user_accounts WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserAccount?

    @Query("SELECT * FROM user_accounts ORDER BY createdAt DESC")
    suspend fun getAllUsers(): List<UserAccount>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccount)

    @Update
    suspend fun updateUser(user: UserAccount)

    @Query("UPDATE user_accounts SET isLoggedIn = 0")
    suspend fun logoutAll()

    @Query("UPDATE user_accounts SET isLoggedIn = 1 WHERE username = :username")
    suspend fun loginUser(username: String)

    @Query("UPDATE user_accounts SET lastSyncedAt = :timestamp WHERE username = :username")
    suspend fun updateSyncTimestamp(username: String, timestamp: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: PaymentTransaction)

    @Query("SELECT * FROM payment_transactions WHERE username = :username ORDER BY timestamp DESC")
    fun getTransactionsForUser(username: String): Flow<List<PaymentTransaction>>
}
