package dev.guilherme.financeapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val description: String,
    val value: Double,
    val type: String,
    val date: Long
)