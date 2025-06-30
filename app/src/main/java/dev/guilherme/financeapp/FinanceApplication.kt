package dev.guilherme.financeapp

import android.app.Application

class FinanceApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}