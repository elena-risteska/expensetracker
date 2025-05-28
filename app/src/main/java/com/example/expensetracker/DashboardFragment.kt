package com.example.expensetracker

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter

class DashboardFragment : Fragment() {

    private lateinit var viewModel: TransactionViewModel

    private lateinit var incomeText: TextView
    private lateinit var expenseText: TextView
    private lateinit var balanceText: TextView
    private lateinit var pieChart: PieChart

    private var currentIncome = 0.0
    private var currentExpense = 0.0

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val isTablet = resources.configuration.smallestScreenWidthDp >= 600
        val orientation = resources.configuration.orientation
        val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE

        return when {
            isTablet && isLandscape -> buildTabletLandscapeLayout()
            isTablet && !isLandscape -> buildTabletPortraitLayout()
            !isTablet && isLandscape -> buildPhoneLandscapeLayout()
            else -> buildPhonePortraitLayout()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun buildPhonePortraitLayout(): View {
        val context = requireContext()
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor("#FFF176".toColorInt())
            setPadding(32, 32, 32, 32)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        incomeText = createRoundedBubbleTextView("#9C27B0")
        expenseText = createRoundedBubbleTextView("#9C27B0")
        balanceText = createRoundedBubbleTextView("#9C27B0")

        root.addView(incomeText)
        root.addView(expenseText)
        root.addView(balanceText)

        // Spacer to push pie chart down
        val spacer = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }
        root.addView(spacer)

        pieChart = createPieChart(ViewGroup.LayoutParams.MATCH_PARENT).apply {
            layoutParams.height = 800 // taller pie chart
        }
        root.addView(pieChart)

        setupViewModel()

        return root
    }

    @SuppressLint("SetTextI18n")
    private fun buildPhoneLandscapeLayout(): View {
        val context = requireContext()
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor("#FFF176".toColorInt())
            setPadding(32, 32, 32, 32)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val leftColumn = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
        }

        incomeText = createRoundedBubbleTextView("#9C27B0")
        expenseText = createRoundedBubbleTextView("#9C27B0")
        balanceText = createRoundedBubbleTextView("#9C27B0")

        leftColumn.addView(incomeText)
        leftColumn.addView(expenseText)
        leftColumn.addView(balanceText)

        pieChart = createPieChart(ViewGroup.LayoutParams.MATCH_PARENT)
        pieChart.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)

        root.addView(leftColumn)
        root.addView(pieChart)

        setupViewModel()

        return root
    }

    @SuppressLint("SetTextI18n")
    private fun buildTabletPortraitLayout(): View {
        val context = requireContext()
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor("#FFF176".toColorInt())
            setPadding(64, 64, 64, 64)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        incomeText = createRoundedBubbleTextView("#7B1FA2")
        expenseText = createRoundedBubbleTextView("#7B1FA2")
        balanceText = createRoundedBubbleTextView("#7B1FA2")

        incomeText.textSize = 24f
        expenseText.textSize = 24f
        balanceText.textSize = 24f

        root.addView(incomeText)
        root.addView(expenseText)
        root.addView(balanceText)

        // Spacer to push pie chart down
        val spacer = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }
        root.addView(spacer)

        pieChart = createPieChart(ViewGroup.LayoutParams.MATCH_PARENT).apply {
            layoutParams.height = 900 // bigger height for tablet
        }
        root.addView(pieChart)

        setupViewModel()

        return root
    }

    @SuppressLint("SetTextI18n")
    private fun buildTabletLandscapeLayout(): View {
        val context = requireContext()
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor("#FFF176".toColorInt())
            setPadding(64, 64, 64, 64)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val leftColumn = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 3f)
        }

        incomeText = createRoundedBubbleTextView("#7B1FA2")
        expenseText = createRoundedBubbleTextView("#7B1FA2")
        balanceText = createRoundedBubbleTextView("#7B1FA2")

        incomeText.textSize = 26f
        expenseText.textSize = 26f
        balanceText.textSize = 26f

        leftColumn.addView(incomeText)
        leftColumn.addView(expenseText)
        leftColumn.addView(balanceText)

        pieChart = createPieChart(ViewGroup.LayoutParams.MATCH_PARENT)
        pieChart.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 7f)

        root.addView(leftColumn)
        root.addView(pieChart)

        setupViewModel()

        return root
    }


    private fun createPieChart(height: Int): PieChart {
        return PieChart(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                height
            )
            setUsePercentValues(true)
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            setTransparentCircleAlpha(110)
            centerText = "Spending Overview"
            setCenterTextSize(18f)
            legend.orientation = Legend.LegendOrientation.HORIZONTAL
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        viewModel.getTotalAmount("income").observe(viewLifecycleOwner) { income ->
            updateDashboard(income ?: 0.0, null)
        }
        viewModel.getTotalAmount("expense").observe(viewLifecycleOwner) { expense ->
            updateDashboard(null, expense ?: 0.0)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateDashboard(income: Double?, expense: Double?) {
        income?.let { currentIncome = it }
        expense?.let { currentExpense = it }

        incomeText.text = "Total Income: ${"%.2f".format(currentIncome)} ден."
        expenseText.text = "Total Expenses: ${"%.2f".format(currentExpense)} ден."
        val balance = currentIncome - currentExpense
        balanceText.text = "Balance: ${"%.2f".format(balance)} ден."

        updatePieChart()
    }

    private fun updatePieChart() {
        val total = currentIncome + currentExpense
        if (total == 0.0) {
            pieChart.clear()
            return
        }

        val entries = mutableListOf<PieEntry>()
        if (currentIncome > 0) entries.add(PieEntry(currentIncome.toFloat(), "Income"))
        if (currentExpense > 0) entries.add(PieEntry(currentExpense.toFloat(), "Expense"))

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                "#7B1FA2".toColorInt(), // Purple
                "#D81B60".toColorInt()  // Pink
            )
            valueTextSize = 16f
            valueTextColor = Color.WHITE
        }

        val data = PieData(dataSet).apply {
            setValueFormatter(PercentFormatter(pieChart))
        }

        pieChart.data = data
        pieChart.invalidate()
    }

    private fun createRoundedBubbleTextView(bgColorHex: String): TextView {
        val radius = 40f
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadii = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
            setColor(bgColorHex.toColorInt())
        }

        return TextView(requireContext()).apply {
            background = drawable
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
            setPadding(40, 30, 40, 30)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 40
                bottomMargin = 40
            }
        }
    }
}
