package com.example.expensetracker

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY id DESC")
    fun getTransactionsByType(type: String): LiveData<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type")
    fun getTotalAmount(type: String): LiveData<Double?>
}
