package dev.guilherme.financeapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert
    suspend fun insert(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Update
    suspend fun update(transaction: Transaction)

    @Query("""
    SELECT 
        transactions.*, 
        categories.name as categoryName 
    FROM transactions 
    INNER JOIN categories ON transactions.categoryId = categories.id 
    WHERE transactions.date BETWEEN :startDate AND :endDate 
    ORDER BY transactions.date DESC""")
    fun getAllTransactionsWithCategoryByDate(startDate: Long, endDate: Long): Flow<List<TransactionWithCategory>>

    @Query("""
        SELECT c.name AS categoryName, SUM(t.value) AS total
        FROM transactions AS t
        INNER JOIN categories AS c ON t.categoryId = c.id
        WHERE t.type = 'DESPESA' AND t.date BETWEEN :startDate AND :endDate
        GROUP BY c.name
        HAVING SUM(t.value) > 0""")
    fun getExpenseTotalsByCategoryByDate(startDate: Long, endDate: Long): Flow<List<CategoryTotal>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getTransactionById(id: Int): Flow<Transaction?>

    @Query("SELECT SUM(value) FROM transactions WHERE type = 'RECEITA' AND date BETWEEN :startDate AND :endDate")
    fun getTotalReceitasByDate(startDate: Long, endDate: Long) : Flow<Double?>

    @Query("SELECT SUM(value) FROM transactions WHERE type = 'DESPESA' AND date BETWEEN :startDate AND :endDate")
    fun getTotalDespesasByDate(startDate: Long, endDate: Long) : Flow<Double?>
}