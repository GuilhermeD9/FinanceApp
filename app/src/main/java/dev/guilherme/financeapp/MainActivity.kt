package dev.guilherme.financeapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val descriptionEditText: EditText = findViewById(R.id.edit_text_description)
        val valueEditText: EditText = findViewById(R.id.edit_text_value)
        val addButton: Button = findViewById(R.id.button_add)

        addButton.setOnClickListener{
            val description = descriptionEditText.text.toString()
            val valueString = valueEditText.text.toString()

            if (description.isNotEmpty() && valueString.isNotEmpty()) {
                val value = valueString.toDouble()

                // Mensagem temporária
                val message = "Transação: $description - Valor: R$$value"
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()

                descriptionEditText.text.clear()
                valueEditText.text.clear()

            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            }

        }
    }
}