package dev.guilherme.financeapp.viewmodel

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.guilherme.financeapp.data.Category
import dev.guilherme.financeapp.data.CategoryDao
import dev.guilherme.financeapp.data.CategoryTotal
import dev.guilherme.financeapp.data.Transaction
import dev.guilherme.financeapp.data.TransactionDao
import dev.guilherme.financeapp.data.TransactionWithCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class DateFilter {
    THIS_MONTH, LAST_MONTH, ALL_TIME
}

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) : ViewModel() {

    private val _dateFilter = MutableStateFlow(DateFilter.THIS_MONTH)
    val dateFilter: StateFlow<DateFilter> = _dateFilter

    private val transactionsByFilter: Flow<List<TransactionWithCategory>> = _dateFilter.flatMapLatest { filter ->
        val (start, end) = getDateRange(filter)
        transactionDao.getAllTransactionsWithCategoryByDate(start, end)
    }

    private val dashboardStateByFilter: StateFlow<DashboardState> = _dateFilter.flatMapLatest { filter ->
        val (start, end) = getDateRange(filter)
        combine(
            transactionDao.getTotalReceitasByDate(start, end),
            transactionDao.getTotalDespesasByDate(start, end)
        ) { totalReceitas, totalDespesas ->
            val receitas = totalReceitas ?: 0.0
            val despesas = totalDespesas ?: 0.0
            DashboardState(receitas, despesas, receitas - despesas)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardState())

    val expenseByCategoryFilter: Flow<List<CategoryTotal>> = _dateFilter.flatMapLatest { filter ->
        val (start, end) = getDateRange(filter)
        transactionDao.getExpenseTotalsByCategoryByDate(start, end)
    }

    val allTransactionsWithCategory: Flow<List<TransactionWithCategory>> = transactionsByFilter
    val dashboardState: StateFlow<DashboardState> = dashboardStateByFilter
    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()

    fun setDateFilter(filter: DateFilter) {
        _dateFilter.value = filter
    }

    private fun getDateRange(filter: DateFilter): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        return when (filter) {
            DateFilter.THIS_MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val start = calendar.timeInMillis
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                val end = calendar.timeInMillis
                start to end
            }
            DateFilter.LAST_MONTH -> {
                calendar.add(Calendar.MONTH, -1)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val start = calendar.timeInMillis
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                val end = calendar.timeInMillis
                start to end
            }
            DateFilter.ALL_TIME -> {
                0L to Long.MAX_VALUE
            }
        }
    }

    fun insert(transaction: Transaction) = viewModelScope.launch {
        transactionDao.insert(transaction)
    }

    fun delete(transaction: Transaction) = viewModelScope.launch {
        transactionDao.delete(transaction)
    }

    fun update(transaction: Transaction) = viewModelScope.launch {
        transactionDao.update(transaction)
    }

    fun getTransactionById(id: Int): Flow<Transaction?> {
        return transactionDao.getTransactionById(id)
    }

    fun insert(category: Category) = viewModelScope.launch {
        categoryDao.insert(category)
    }

    fun update(category: Category) = viewModelScope.launch {
        categoryDao.update(category)
    }

    fun delete(category: Category) = viewModelScope.launch {
        categoryDao.delete(category)
    }
}

data class DashboardState(
    val totalReceitas: Double = 0.0,
    val totalDespesas: Double = 0.0,
    val saldo: Double = 0.0
)