package dev.guilherme.financeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.guilherme.financeapp.data.Transaction
import dev.guilherme.financeapp.data.TransactionDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TransactionViewModel(private val dao: TransactionDao) : ViewModel() {
    val allTransactions: Flow<List<Transaction>> = dao.getAllTransactions()

    fun insert(transaction: Transaction) = viewModelScope.launch {
        dao.insert(transaction)
    }

    fun delete(transaction: Transaction) = viewModelScope.launch {
        dao.delete(transaction)
    }

    fun update(transaction: Transaction) = viewModelScope.launch {
        dao.update(transaction)
    }

    fun getTransactionById(id: Int): Flow<Transaction?> {
        return dao.getTransactionById(id)
    }
}

class TransactionViewModelFactory(private val dao: TransactionDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknow ViewModel Class")
    }
}