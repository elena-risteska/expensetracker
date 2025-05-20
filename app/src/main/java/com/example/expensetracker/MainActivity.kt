package com.example.expensetracker

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = TextView(this).apply {
            text = "Welcome to the Expense Tracker Dashboard"
            textSize = 20f
            setPadding(32, 0, 0, 0)
        }

        setContentView(textView)
    }
}
