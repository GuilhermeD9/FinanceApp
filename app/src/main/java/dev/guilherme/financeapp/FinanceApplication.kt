package dev.guilherme.financeapp

import android.app.Application
import dev.guilherme.financeapp.data.AppDatabase

class FinanceApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}