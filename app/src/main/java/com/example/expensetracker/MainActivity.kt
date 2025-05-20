package com.example.expensetracker

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.graphics.toColorInt

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Main container layout
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // FrameLayout to host fragments
        val fragmentContainer = FrameLayout(this).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }

        // BottomNavigationView
        val bottomNav = BottomNavigationView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            setBackgroundColor("#FFF176".toColorInt()) // Yellow background
            itemIconTintList = null // Use original icon colors if you have custom drawables

            menu.add(Menu.NONE, 1, Menu.NONE, "Expenses")
                .setIcon(android.R.drawable.ic_menu_view)
            menu.add(Menu.NONE, 2, Menu.NONE, "Income")
                .setIcon(android.R.drawable.ic_menu_add)
        }

        // Add views to root
        rootLayout.addView(fragmentContainer)
        rootLayout.addView(bottomNav)

        setContentView(rootLayout)

        // Load default fragment
        supportFragmentManager.beginTransaction()
            .replace(fragmentContainer.id, ExpenseFragment())
            .commit()

        // Handle nav item clicks
        bottomNav.setOnItemSelectedListener { item ->
            val selectedFragment = when (item.itemId) {
                1 -> ExpenseFragment()
                2 -> IncomeFragment()
                else -> null
            }

            selectedFragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(fragmentContainer.id, it)
                    .commit()
            }

            true
        }
    }
}
