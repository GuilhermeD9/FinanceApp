package dev.guilherme.financeapp.data

import androidx.room.Embedded

data class TransactionWithCategory(
    @Embedded val transaction: Transaction,
    val categoryName: String
)