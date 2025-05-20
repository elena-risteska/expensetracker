package com.example.expensetracker

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.core.graphics.toColorInt

class HomeActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val backgroundColor = "#FFF176".toColorInt()   // Yellow
        val buttonColor = "#7E57C2".toColorInt()       // Purple
        val textColor = "#212121".toColorInt()         // Dark Gray

        val rootLayout = FrameLayout(this).apply {
            setBackgroundColor(backgroundColor)
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Vertical main layout
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Title
        val title = TextView(this).apply {
            text = "Your Expenses"
            textSize = 28f
            setTypeface(null, Typeface.BOLD)
            setTextColor(textColor)
            setPadding(0, 0, 0, 32)
        }

        // Placeholder list for expenses
        val expenseList = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }

        // Example expense item (for demo)
        fun createExpenseItem(title: String, amount: String): LinearLayout {
            val itemLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(32)
                background = GradientDrawable().apply {
                    cornerRadius = 24f
                    setColor(Color.WHITE)
                }
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 24)
                }
            }

            val expenseTitle = TextView(this).apply {
                text = title
                textSize = 18f
                setTextColor(textColor)
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            }

            val expenseAmount = TextView(this).apply {
                text = amount
                textSize = 18f
                setTypeface(null, Typeface.BOLD)
                setTextColor(buttonColor)
            }

            itemLayout.addView(expenseTitle)
            itemLayout.addView(expenseAmount)
            return itemLayout
        }

        // Add example expenses
        expenseList.addView(createExpenseItem("Groceries", "$45.00"))
        expenseList.addView(createExpenseItem("Taxi", "$12.30"))
        expenseList.addView(createExpenseItem("Coffee", "$4.50"))

        mainLayout.addView(title)
        mainLayout.addView(expenseList)

        // Floating Add button
        val addButton = Button(this).apply {
            text = "+ Add Expense"
            setTextColor(Color.WHITE)
            textSize = 16f
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 64f
                setColor(buttonColor)
            }
            // Padding to make it pill shaped
            setPadding(60, 20, 60, 20)
            elevation = 12f
        }

        // Position addButton bottom-right in FrameLayout
        val buttonParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.END
            marginEnd = 48
            bottomMargin = 48
        }

        addButton.layoutParams = buttonParams

        addButton.setOnClickListener {
            // TODO: Navigate to Add Expense screen (to be implemented)
            Toast.makeText(this, "Add Expense clicked", Toast.LENGTH_SHORT).show()
        }

        rootLayout.addView(mainLayout)
        rootLayout.addView(addButton)

        setContentView(rootLayout)
    }
}
