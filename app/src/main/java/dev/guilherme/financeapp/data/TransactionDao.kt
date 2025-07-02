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
    SELECT transactions.*,
            categories.name as categoryName 
    FROM transactions 
    INNER JOIN categories ON transactions.categoryId = categories.id 
    ORDER BY transactions.date DESC""")
    fun getAllTransactionsWithCategory(): Flow<List<TransactionWithCategory>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getTransactionById(id: Int): Flow<Transaction?>

    @Query("SELECT SUM(value) FROM transactions WHERE type = 'RECEITA'")
    fun getTotalReceitas() : Flow<Double?>

    @Query("SELECT SUM(value) FROM transactions WHERE type = 'DESPESA'")
    fun getTotalDespesas() : Flow<Double?>
}