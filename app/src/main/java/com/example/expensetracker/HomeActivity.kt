package com.example.expensetracker

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.core.graphics.toColorInt

data class Expense(val category: String, val amount: Double)

class HomeActivity : AppCompatActivity() {

    private val expenses = mutableListOf<Expense>()
    private lateinit var expenseListLayout: LinearLayout

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

        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val title = TextView(this).apply {
            text = "Your Expenses"
            textSize = 28f
            setTypeface(null, Typeface.BOLD)
            setTextColor(textColor)
            setPadding(0, 0, 0, 32)
        }

        expenseListLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }

        mainLayout.addView(title)
        mainLayout.addView(expenseListLayout)

        val addButton = Button(this).apply {
            text = "+ Add Expense"
            setTextColor(Color.WHITE)
            textSize = 16f
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 64f
                setColor(buttonColor)
            }
            setPadding(60, 20, 60, 20)
            elevation = 12f
        }

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
            showAddExpenseDialog()
        }

        rootLayout.addView(mainLayout)
        rootLayout.addView(addButton)

        setContentView(rootLayout)

        // Optional: start with some example expenses
        addExpense(Expense("Groceries", 45.00))
        addExpense(Expense("Taxi", 12.30))
        addExpense(Expense("Coffee", 4.50))
    }

    @SuppressLint("SetTextI18n")
    private fun showAddExpenseDialog() {
        val backgroundColor = "#FFF176".toColorInt()   // Yellow
        val buttonColor = "#7E57C2".toColorInt()       // Purple
        val textColor = "#212121".toColorInt()         // Dark Gray

        val dialogLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48)
            setBackgroundColor(backgroundColor)
        }

        val categoryLabel = TextView(this).apply {
            text = "Category"
            setTextColor(textColor)
            textSize = 16f
            setPadding(0, 0, 0, 8)
            setTypeface(null, Typeface.BOLD)
        }

        val categories = listOf("Groceries", "Taxi", "Coffee", "Rent", "Entertainment", "Other")
        val spinner = Spinner(this)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner.adapter = adapter

        spinner.background = GradientDrawable().apply {
            cornerRadius = 32f
            setColor(Color.WHITE)
            setStroke(3, buttonColor)
        }
        spinner.setPadding(32, 16, 32, 16)

        // Add margin below spinner
        val spinnerParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 32
        }
        spinner.layoutParams = spinnerParams

        val amountInput = EditText(this).apply {
            hint = "Amount"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setPadding(32, 24, 32, 24)
            background = GradientDrawable().apply {
                cornerRadius = 32f
                setColor(Color.WHITE)
            }
            setTextColor(textColor)
            textSize = 16f
        }

        // Add margin below amount input
        val amountParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 48
        }
        amountInput.layoutParams = amountParams

        dialogLayout.addView(categoryLabel)
        dialogLayout.addView(spinner)
        dialogLayout.addView(amountInput)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add New Expense")
            .setView(dialogLayout)
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            fun styleButton(button: Button) {
                button.setTextColor(Color.WHITE)
                button.background = GradientDrawable().apply {
                    cornerRadius = 48f
                    setColor(buttonColor)
                }
                button.setPadding(60, 20, 60, 20)
            }
            styleButton(addButton)
            styleButton(cancelButton)

            // Add margin between buttons
            val addBtnParams = addButton.layoutParams as LinearLayout.LayoutParams
            addBtnParams.marginStart = 32
            addButton.layoutParams = addBtnParams

            addButton.setOnClickListener {
                val selectedCategory = spinner.selectedItem.toString()
                val amountStr = amountInput.text.toString().trim()

                if (amountStr.isEmpty()) {
                    Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val amount = amountStr.toDoubleOrNull()
                if (amount == null || amount <= 0) {
                    Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                addExpense(Expense(selectedCategory, amount))
                dialog.dismiss()
            }

            cancelButton.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.show()
    }



    private fun addExpense(expense: Expense) {
        expenses.add(expense)
        refreshExpenseList()
    }

    @SuppressLint("SetTextI18n")
    private fun refreshExpenseList() {
        expenseListLayout.removeAllViews()

        val buttonColor = "#7E57C2".toColorInt()       // Purple
        val textColor = "#212121".toColorInt()         // Dark Gray

        for (expense in expenses) {
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
                text = expense.category
                textSize = 18f
                setTextColor(textColor)
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            }

            val expenseAmount = TextView(this).apply {
                text = "$%.2f".format(expense.amount)
                textSize = 18f
                setTypeface(null, Typeface.BOLD)
                setTextColor(buttonColor)
            }

            itemLayout.addView(expenseTitle)
            itemLayout.addView(expenseAmount)

            expenseListLayout.addView(itemLayout)
        }
    }
}
