package com.example.expensetracker

import android.content.Context
import androidx.lifecycle.LiveData

class TransactionRepository(context: Context) {

    private val transactionDao = AppDatabase.getDatabase(context).transactionDao()

    fun getTransactions(type: String): LiveData<List<TransactionEntity>> {
        return transactionDao.getTransactionsByType(type)
    }

    fun getTotalAmount(type: String): LiveData<Double?> {
        return transactionDao.getTotalAmount(type)
    }

    suspend fun insert(transaction: TransactionEntity) {
        transactionDao.insert(transaction)
    }
}
