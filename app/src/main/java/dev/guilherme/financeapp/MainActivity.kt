package dev.guilherme.financeapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var transactionDao: TransactionDao
    private lateinit var adapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        transactionDao = AppDatabase.getDatabase(this).transactionDao()

        val descriptionEditText: EditText = findViewById(R.id.edit_text_description)
        val valueEditText: EditText = findViewById(R.id.edit_text_value)
        val addButton: Button = findViewById(R.id.button_add)
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_transactions)

        var transactionToUpdate: Transaction? = null

        adapter = TransactionAdapter (
            onItemClick = { transaction ->
                transactionToUpdate = transaction
                val dialog = EditTransactionDialogFragment.newInstance(
                    transaction.description,
                    transaction.value
                )
                dialog.show(supportFragmentManager, EditTransactionDialogFragment.TAG)
            },
            onDeleteClick = { transaction ->
                lifecycleScope.launch {
                    transactionDao.delete(transaction)
                    Toast.makeText(this@MainActivity, "Transação deletada", Toast.LENGTH_SHORT).show()
                }
            }
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            transactionDao.getAllTransactions().collect {
                transactionsFromDb -> adapter.submitList(transactionsFromDb)
            }
        }

        supportFragmentManager.setFragmentResultListener(EditTransactionDialogFragment.REQUEST_KEY, this) { _, bundle ->
            val newDescription = bundle.getString(EditTransactionDialogFragment.RESULT_DESCRIPTION)!!
            val newValue = bundle.getDouble(EditTransactionDialogFragment.RESULT_VALUE)

            if (transactionToUpdate != null) {
                val updatedTransaction = transactionToUpdate!!.copy(
                    description = newDescription,
                    value = newValue
                )
                lifecycleScope.launch {
                    transactionDao.update(updatedTransaction)
                }
                Toast.makeText(this, "Transação atualizada!", Toast.LENGTH_SHORT).show()
                transactionToUpdate = null
            }

        }

        addButton.setOnClickListener{
            val description = descriptionEditText.text.toString()
            val valueString = valueEditText.text.toString()

            if (description.isNotEmpty() && valueString.isNotEmpty()) {
                val value = valueString.toDouble()
                val transaction = Transaction(description = description, value = value)

                lifecycleScope.launch {
                    transactionDao.insert(transaction)
                }

                descriptionEditText.text.clear()
                valueEditText.text.clear()
                Toast.makeText(this, "Transação salva!", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}