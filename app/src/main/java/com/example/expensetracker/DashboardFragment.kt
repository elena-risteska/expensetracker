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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.edit
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

    private fun changeLanguage(language: String) {
        val updatedContext = LocaleHelper.setLocale(requireContext(), language)
        activity?.recreate() // Restart activity to apply changes
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

        val topBar = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.CENTER_VERTICAL
        }

        val logoutButton = TextView(context).apply {
            text = getString(R.string.logout)
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            setPadding(20, 10, 20, 10)
            background = GradientDrawable().apply {
                setColor("#7E57C2".toColorInt())
                cornerRadius = 24f
            }
            setOnClickListener {
                performLogout()
            }
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        // Flags row aligned to the right
        val flagRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f // take the remaining space
            )
        }

        val macedonianFlag = ImageView(context).apply {
            setImageResource(R.drawable.ic_flag_mk)
            layoutParams = LinearLayout.LayoutParams(96, 96).apply {
                marginEnd = 16
            }
            setOnClickListener {
                changeLanguage("mk")
            }
        }

        val englishFlag = ImageView(context).apply {
            setImageResource(R.drawable.ic_flag_en)
            layoutParams = LinearLayout.LayoutParams(96, 96)
            setOnClickListener {
                changeLanguage("en")
            }
        }

        flagRow.addView(macedonianFlag)
        flagRow.addView(englishFlag)

        // Add Logout button and flags to the top bar
        topBar.addView(logoutButton)
        topBar.addView(flagRow)

        root.addView(topBar)

        // Bubble TextViews
        incomeText = createRoundedBubbleTextView("#9C27B0")
        expenseText = createRoundedBubbleTextView("#9C27B0")
        balanceText = createRoundedBubbleTextView("#9C27B0")

        root.addView(incomeText)
        root.addView(expenseText)
        root.addView(balanceText)

        // Spacer
        val spacer = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }
        root.addView(spacer)

        pieChart = createPieChart(ViewGroup.LayoutParams.MATCH_PARENT).apply {
            layoutParams.height = 800
        }
        root.addView(pieChart)

        setupViewModel()

        return root
    }

    @SuppressLint("RestrictedApi", "CommitPrefEdits")
    private fun performLogout() {
        val sharedPref = requireActivity().getSharedPreferences("user_prefs", 0)
        sharedPref.edit() {
            clear() // clear all saved data
        }

        val intent = android.content.Intent(requireContext(), LoginActivity::class.java)
        intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        requireActivity().finish()
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
            centerText = getString(R.string.overview)
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

        incomeText.text = getString(R.string.total_income) + ": ${"%.2f".format(currentIncome)} ден."
        expenseText.text = getString(R.string.total_expense) + ": ${"%.2f".format(currentExpense)} ден."
        val balance = currentIncome - currentExpense
        balanceText.text = getString(R.string.balance) + ": ${"%.2f".format(balance)} ден."


        updatePieChart()
    }

    private fun updatePieChart() {
        val total = currentIncome + currentExpense
        if (total == 0.0) {
            pieChart.clear()
            return
        }

        val entries = mutableListOf<PieEntry>()
        if (currentIncome > 0) entries.add(PieEntry(currentIncome.toFloat(), getString(R.string.income)))
        if (currentExpense > 0) entries.add(PieEntry(currentExpense.toFloat(), getString(R.string.expense)))
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
