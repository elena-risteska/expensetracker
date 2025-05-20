package com.example.expensetracker

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class DashboardFragment : Fragment() {

    private lateinit var viewModel: TransactionViewModel
    private lateinit var layout: LinearLayout
    private lateinit var incomeText: TextView
    private lateinit var expenseText: TextView
    private lateinit var balanceText: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor("#FFF176".toColorInt()) // yellow background
            setPadding(64, 64, 64, 64)
        }

        incomeText = TextView(requireContext()).apply {
            textSize = 20f
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 0, 0, 32)
            setTextColor("#388E3C".toColorInt())
        }

        expenseText = TextView(requireContext()).apply {
            textSize = 20f
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 0, 0, 32)
            setTextColor(Color.RED)
        }

        balanceText = TextView(requireContext()).apply {
            textSize = 24f
            setTypeface(null, Typeface.BOLD_ITALIC)
            setTextColor(Color.BLACK)
        }

        layout.addView(incomeText)
        layout.addView(expenseText)
        layout.addView(balanceText)

        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        viewModel.getTotalAmount("income").observe(viewLifecycleOwner) { income ->
            updateDashboard(income ?: 0.0, null)
        }

        viewModel.getTotalAmount("expense").observe(viewLifecycleOwner) { expense ->
            updateDashboard(null, expense ?: 0.0)
        }

        return layout
    }

    private var currentIncome = 0.0
    private var currentExpense = 0.0

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun updateDashboard(income: Double?, expense: Double?) {
        income?.let { currentIncome = it }
        expense?.let { currentExpense = it }

        incomeText.text = "Total Income: +${String.format("%.2f", currentIncome)} ден."
        expenseText.text = "Total Expenses: -${String.format("%.2f", currentExpense)} ден."
        val balance = currentIncome - currentExpense
        balanceText.text = "Balance: ${String.format("%.2f", balance)} ден."
    }
}
