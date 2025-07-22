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
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) : ViewModel() {

    private val _dateFilter = MutableStateFlow(DateFilter.THIS_MONTH)
    private val _typeFilter = MutableStateFlow("ALL")
    private val _searchQuery = MutableStateFlow("")

    val dateFilter: StateFlow<DateFilter> = _dateFilter

    val dashboardState: StateFlow<DashboardState> = _dateFilter.flatMapLatest { filter ->
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

    val expenseByCategory: Flow<List<CategoryTotal>> = _dateFilter.flatMapLatest { filter ->
        val (start, end) = getDateRange(filter)
        transactionDao.getExpenseTotalsByCategoryByDate(start, end)
    }

    val allTransactionsWithCategory: Flow<List<TransactionWithCategory>> =
        combine(_dateFilter, _typeFilter, _searchQuery) { dateFilter, typeFilter, searchQuery ->
            Triple(dateFilter, typeFilter, searchQuery)
        }.flatMapLatest { (dateFilter, typeFilter, searchQuery) ->
            val (start, end) = getDateRange(dateFilter)
            transactionDao.getAllTransactionsWithCategoryByDate(start, end, typeFilter, searchQuery)
        }

    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()

    fun setDateFilter(filter: DateFilter) {
        _dateFilter.value = filter
    }

    fun setTypeFilter(filterType: String) {
        _typeFilter.value = filterType
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun getDateRange(filter: DateFilter): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        return when (filter) {
            DateFilter.THIS_MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                setCalendarToStartOfDay(calendar)
                val start = calendar.timeInMillis
                calendar.add(Calendar.MONTH, 1)
                val end = calendar.timeInMillis - 1
                start to end
            }
            DateFilter.LAST_MONTH -> {
                calendar.add(Calendar.MONTH, -1)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                setCalendarToStartOfDay(calendar)
                val start = calendar.timeInMillis
                calendar.add(Calendar.MONTH, 1)
                val end = calendar.timeInMillis - 1
                start to end
            }
            DateFilter.ALL_TIME -> {
                0L to Long.MAX_VALUE
            }
        }
    }

    private fun setCalendarToStartOfDay(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }

    private fun setCalendarToEndOfDay(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
    }

    fun insert(transaction: Transaction) = viewModelScope.launch { transactionDao.insert(transaction) }
    fun delete(transaction: Transaction) = viewModelScope.launch { transactionDao.delete(transaction) }
    fun update(transaction: Transaction) = viewModelScope.launch { transactionDao.update(transaction) }
    fun getTransactionById(id: Int): Flow<Transaction?> { return transactionDao.getTransactionById(id) }
    fun insert(category: Category) = viewModelScope.launch { categoryDao.insert(category) }
    fun update(category: Category) = viewModelScope.launch { categoryDao.update(category) }
    fun delete(category: Category) = viewModelScope.launch { categoryDao.delete(category) }
}

data class DashboardState(
    val totalReceitas: Double = 0.0,
    val totalDespesas: Double = 0.0,
    val saldo: Double = 0.0
)