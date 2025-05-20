package com.example.expensetracker

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class ExpenseFragment : Fragment() {

    private lateinit var viewModel: TransactionViewModel
    private lateinit var layout: LinearLayout

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            setBackgroundColor(Color.WHITE)
        }

        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        val addBtn = Button(requireContext()).apply {
            text = "Add Expense"
            setOnClickListener { showAddDialog() }
        }

        layout.addView(addBtn)

        viewModel.getTransactions("expense").observe(viewLifecycleOwner) { expenses ->
            refreshList(expenses)
        }

        return layout
    }

    private fun showAddDialog() {
        val context = requireContext()
        val input = EditText(context).apply {
            hint = "Amount"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        AlertDialog.Builder(context)
            .setTitle("New Expense")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val amount = input.text.toString().toDoubleOrNull()
                if (amount != null) {
                    viewModel.insert(
                        TransactionEntity(
                            amount = amount,
                            category = "Other",
                            type = "expense"
                        )
                    )
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    @SuppressLint("SetTextI18n")
    private fun refreshList(expenses: List<TransactionEntity>) {
        // Clear all but first button
        layout.removeViews(1, layout.childCount - 1)

        expenses.forEach {
            val item = TextView(requireContext()).apply {
                text = "- ${it.amount} ден. (${it.category})"
                setTextColor(Color.RED)
                textSize = 16f
            }
            layout.addView(item)
        }
    }
}
