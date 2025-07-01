package dev.guilherme.financeapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.guilherme.financeapp.R
import dev.guilherme.financeapp.data.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionAdapter(
    private val onItemClick: (Transaction) -> Unit,
    private val onDeleteClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    class TransactionViewHolder(
        itemView: View,
        private val onItemClick: (Transaction) -> Unit,
        private val onDeleteClick: (Transaction) -> Unit
        ) : RecyclerView.ViewHolder(itemView) {

        private val descriptionTextView: TextView = itemView.findViewById(R.id.text_view_item_description)
        private val valueTextView: TextView = itemView.findViewById(R.id.text_view_item_value)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.button_delete)
        private val dateTextView: TextView = itemView.findViewById(R.id.text_view_item_date)

        fun bind(transaction: Transaction) {
            descriptionTextView.text = transaction.description

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            dateTextView.text = dateFormat.format(Date(transaction.date))

            if (transaction.type == "RECEITA") {
                valueTextView.text = "+ R$ ${"%.2f".format(transaction.value)}"
                valueTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.green))
            } else {
                valueTextView.text = "- R$ ${"%.2f".format(transaction.value)}"
                valueTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
            }
            itemView.setOnClickListener {
                onItemClick(transaction)
            }

            deleteButton.setOnClickListener{
                onDeleteClick(transaction)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view, onItemClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}