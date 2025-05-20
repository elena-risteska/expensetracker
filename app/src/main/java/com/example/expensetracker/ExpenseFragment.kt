package com.example.expensetracker

//noinspection SuspiciousImport
import android.R
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.core.graphics.toColorInt

class ExpenseFragment : Fragment() {

    private lateinit var expenseListLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val context = requireContext()

        val rootLayout = FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.WHITE)
        }

        // ScrollView to hold list
        val scrollView = ScrollView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        expenseListLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 160)
        }

        scrollView.addView(expenseListLayout)
        rootLayout.addView(scrollView)

        // Add Floating Action Button
        val fab = Button(context).apply {
            text = "+"
            textSize = 28f
            setTextColor(Color.WHITE)
            setBackgroundColor("#7B1FA2".toColorInt()) // Purple
            setPadding(32, 16, 32, 16)
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor("#7B1FA2".toColorInt())
            }

            val params = FrameLayout.LayoutParams(180, 180).apply {
                gravity = Gravity.BOTTOM or Gravity.END
                marginEnd = 48
                bottomMargin = 48
            }

            layoutParams = params

            setOnClickListener {
                showAddExpenseDialog()
            }
        }

        rootLayout.addView(fab)

        return rootLayout
    }

    private fun showAddExpenseDialog() {
        val context = requireContext()

        val dialogLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(64, 32, 64, 32)
        }

        val titleInput = EditText(context).apply {
            hint = "Title"
        }

        val amountInput = EditText(context).apply {
            hint = "Amount"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        val categories = arrayOf("Food", "Transport", "Rent", "Other")
        val categoryDropdown = Spinner(context).apply {
            adapter = ArrayAdapter(context, R.layout.simple_spinner_dropdown_item, categories)
        }

        dialogLayout.addView(titleInput)
        dialogLayout.addView(amountInput)
        dialogLayout.addView(categoryDropdown)

        AlertDialog.Builder(context)
            .setTitle("Add Expense")
            .setView(dialogLayout)
            .setPositiveButton("Add") { _, _ ->
                val title = titleInput.text.toString()
                val amount = amountInput.text.toString()
                val category = categoryDropdown.selectedItem.toString()

                addExpenseView(title, amount, category)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    @SuppressLint("SetTextI18n")
    private fun addExpenseView(title: String, amount: String, category: String) {
        val context = requireContext()

        val card = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 32, 48, 32)
            setBackgroundColor("#FFF9C4".toColorInt()) // Light yellow
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 24
            }
            background = GradientDrawable().apply {
                cornerRadius = 32f
                setColor("#FFF9C4".toColorInt())
            }
        }

        val titleText = TextView(context).apply {
            text = "$title (${category})"
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
        }

        val amountText = TextView(context).apply {
            text = "- $amount ден."
            setTextColor(Color.RED)
            textSize = 16f
        }

        card.addView(titleText)
        card.addView(amountText)

        expenseListLayout.addView(card, 0)
    }
}
