package dev.guilherme.financeapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val transactions = mutableListOf<Transaction>()
    private lateinit var adapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val descriptionEditText: EditText = findViewById(R.id.edit_text_description)
        val valueEditText: EditText = findViewById(R.id.edit_text_value)
        val addButton: Button = findViewById(R.id.button_add)
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_transactions)

        adapter = TransactionAdapter(transactions)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        addButton.setOnClickListener{
            val description = descriptionEditText.text.toString()
            val valueString = valueEditText.text.toString()

            if (description.isNotEmpty() && valueString.isNotEmpty()) {
                val value = valueString.toDouble()
                val transaction = Transaction(description, value)

                transactions.add(transaction)

                adapter.notifyItemInserted(transactions.size - 1)

                descriptionEditText.text.clear()
                valueEditText.text.clear()
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}