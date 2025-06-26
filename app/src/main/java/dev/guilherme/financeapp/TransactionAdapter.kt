package dev.guilherme.financeapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(
    private val onDeleteClick: (Transaction) -> Unit ) :
    ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    class TransactionViewHolder(itemView: View, private val onDeleteClick: (Transaction) -> Unit
        ) : RecyclerView.ViewHolder(itemView) {

        private val descriptionTextView: TextView = itemView.findViewById(R.id.text_view_item_description)
        private val valueTextView: TextView = itemView.findViewById(R.id.text_view_item_value)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.button_delete)

        fun bind(transaction: Transaction) {
            descriptionTextView.text = transaction.description
            valueTextView.text = "R$ ${"%.2f".format(transaction.value)}"

            deleteButton.setOnClickListener{
                onDeleteClick(transaction)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view, onDeleteClick)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)
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