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

    private var transactionToUpdate: Transaction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        transactionDao = AppDatabase.getDatabase(this).transactionDao()

        val descriptionEditText: EditText = findViewById(R.id.edit_text_description)
        val valueEditText: EditText = findViewById(R.id.edit_text_value)
        val addButton: Button = findViewById(R.id.button_add)
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_transactions)

        adapter = TransactionAdapter (
            onItemClick = { transaction ->
                enterEditMode(transaction, descriptionEditText, valueEditText, addButton)
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

        addButton.setOnClickListener{
            val description = descriptionEditText.text.toString()
            val valueString = valueEditText.text.toString()

            if (description.isNotEmpty() && valueString.isNotEmpty()) {
                val value = valueString.toDouble()

                if (transactionToUpdate != null) {
                    val updatedTransaction = transactionToUpdate!!.copy(
                        description = description,
                        value = value
                    )
                    lifecycleScope.launch {
                        transactionDao.update(updatedTransaction)
                    }
                    Toast.makeText(this, "Transação atualizada!", Toast.LENGTH_SHORT).show()
                    exitEditMode(descriptionEditText, valueEditText, addButton)
                } else {
                    val transaction = Transaction(description = description, value = value)
                    lifecycleScope.launch {
                        transactionDao.insert(transaction)
                    }
                    descriptionEditText.text.clear()
                    valueEditText.text.clear()
                    Toast.makeText(this, "Transação salva!", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enterEditMode(
        transaction: Transaction,
        descEt: EditText,
        valueEt: EditText,
        button: Button
    ) {
        transactionToUpdate = transaction
        descEt.setText(transaction.description)
        valueEt.setText(transaction.value.toString())
        button.text = "Atualizar Transação"
    }

    private fun exitEditMode(descEt: EditText, valueEt: EditText, button: Button) {
        transactionToUpdate = null
        descEt.text.clear()
        valueEt.text.clear()
        button.text = "Adicionar Transação"
    }
}