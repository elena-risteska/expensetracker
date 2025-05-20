package com.example.expensetracker

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt

class MainActivity : AppCompatActivity() {

    private lateinit var fragmentContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Root layout
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Fragment container (center of screen)
        fragmentContainer = FrameLayout(this).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }

        rootLayout.addView(fragmentContainer)

        // Bottom Navigation
        val bottomNav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(16, 16, 16, 32)
            setBackgroundColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        fun navButton(text: String, onClick: () -> Unit): Button {
            return Button(this).apply {
                this.text = text
                textSize = 14f
                setTextColor(Color.WHITE)
                background = GradientDrawable().apply {
                    cornerRadius = 48f
                    setColor("#7B1FA2".toColorInt()) // Purple
                }
                val params = LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f)
                params.setMargins(12, 0, 12, 0)
                layoutParams = params
                setOnClickListener { onClick() }
            }
        }

        val dashboardBtn = navButton("Dashboard") {
            supportFragmentManager.beginTransaction()
                .replace(fragmentContainer.id, DashboardFragment())
                .commit()
        }

        val expensesBtn = navButton("Expenses") {
            supportFragmentManager.beginTransaction()
                .replace(fragmentContainer.id, ExpenseFragment())
                .commit()
        }

        val incomeBtn = navButton("Income") {
            supportFragmentManager.beginTransaction()
                .replace(fragmentContainer.id, IncomeFragment())
                .commit()
        }

        bottomNav.addView(dashboardBtn)
        bottomNav.addView(expensesBtn)
        bottomNav.addView(incomeBtn)

        rootLayout.addView(bottomNav)
        setContentView(rootLayout)

        // Set default screen
        supportFragmentManager.beginTransaction()
            .replace(fragmentContainer.id, DashboardFragment())
            .commit()
    }
}
