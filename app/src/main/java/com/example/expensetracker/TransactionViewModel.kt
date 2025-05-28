package com.example.expensetracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TransactionRepository(application)

    fun getTransactions(type: String): LiveData<List<TransactionEntity>> {
        return repository.getTransactions(type)
    }

    fun getTotalAmount(type: String): LiveData<Double?> {
        return repository.getTotalAmount(type)
    }

    fun insert(transaction: TransactionEntity) = viewModelScope.launch {
        repository.insert(transaction)
    }

    fun delete(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.delete(transaction)
        }
    }

}
