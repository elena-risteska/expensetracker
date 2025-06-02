package com.example.expensetracker

import android.content.Context
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
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity() {

    private lateinit var fragmentContainer: FrameLayout

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = "#FFF176".toColorInt()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor("#FFF176".toColorInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setPadding(0, 64, 0, 70)
        }

        fragmentContainer = FrameLayout(this).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }

        rootLayout.addView(fragmentContainer)

        val bottomNav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(24, 24, 24, 48)
            setBackgroundColor("#FFF176".toColorInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                WRAP_CONTENT
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
                layoutParams = LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f).apply {
                    setMargins(16, 0, 16, 0)
                }
                setOnClickListener { onClick() }
            }
        }

        bottomNav.addView(navButton(getString(R.string.dashboard)) {
            supportFragmentManager.beginTransaction()
                .replace(fragmentContainer.id, DashboardFragment())
                .commit()
        })

        bottomNav.addView(navButton(getString(R.string.expense)) {
            supportFragmentManager.beginTransaction()
                .replace(fragmentContainer.id, ExpenseFragment())
                .commit()
        })

        bottomNav.addView(navButton(getString(R.string.income)) {
            supportFragmentManager.beginTransaction()
                .replace(fragmentContainer.id, IncomeFragment())
                .commit()
        })

        rootLayout.addView(bottomNav)
        setContentView(rootLayout)

        supportFragmentManager.beginTransaction()
            .replace(fragmentContainer.id, DashboardFragment())
            .commit()
    }
}
