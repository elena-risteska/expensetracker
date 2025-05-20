package com.example.expensetracker

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val category: String,
    val type: String // "expense" or "income"
)
