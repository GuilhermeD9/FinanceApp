package dev.guilherme.financeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.guilherme.financeapp.data.Transaction
import dev.guilherme.financeapp.data.TransactionDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardState(
    val totalReceitas: Double = 0.0,
    val totalDespesas: Double = 0.0,
    val saldo: Double = 0.0
)

class TransactionViewModel(private val dao: TransactionDao) : ViewModel() {
    val allTransactions: Flow<List<Transaction>> = dao.getAllTransactions()

    val dashboardState: StateFlow<DashboardState> =
        combine(dao.getTotalReceitas(), dao.getTotalDespesas()) { totalReceitas, totalDespesas ->
            val receitas = totalReceitas ?: 0.0
            val despesas = totalDespesas ?: 0.0
            val saldo = receitas - despesas
            DashboardState(receitas, despesas, saldo)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardState()
        )

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