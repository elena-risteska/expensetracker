package com.example.expensetracker//noinspection SuspiciousImport
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

class IncomeFragment : Fragment() {

    private lateinit var incomeListLayout: LinearLayout

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

        val scrollView = ScrollView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        incomeListLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 160)
        }

        scrollView.addView(incomeListLayout)
        rootLayout.addView(scrollView)

        // Floating Action Button
        val fab = Button(context).apply {
            text = "+"
            textSize = 28f
            setTextColor(Color.WHITE)
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor("#4CAF50".toColorInt()) // Vibrant green
            }

            layoutParams = FrameLayout.LayoutParams(180, 180).apply {
                gravity = Gravity.BOTTOM or Gravity.END
                marginEnd = 48
                bottomMargin = 48
            }

            setOnClickListener {
                showAddIncomeDialog()
            }
        }

        rootLayout.addView(fab)

        return rootLayout
    }

    private fun showAddIncomeDialog() {
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

        val sources = arrayOf("Salary", "Gift", "Freelance", "Other")
        val sourceDropdown = Spinner(context).apply {
            adapter = ArrayAdapter(context, R.layout.simple_spinner_dropdown_item, sources)
        }

        dialogLayout.addView(titleInput)
        dialogLayout.addView(amountInput)
        dialogLayout.addView(sourceDropdown)

        AlertDialog.Builder(context)
            .setTitle("Add Income")
            .setView(dialogLayout)
            .setPositiveButton("Add") { _, _ ->
                val title = titleInput.text.toString()
                val amount = amountInput.text.toString()
                val source = sourceDropdown.selectedItem.toString()

                addIncomeView(title, amount, source)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    @SuppressLint("SetTextI18n")
    private fun addIncomeView(title: String, amount: String, source: String) {
        val context = requireContext()

        val card = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 32, 48, 32)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 24
            }
            background = GradientDrawable().apply {
                cornerRadius = 32f
                setColor("#C8E6C9".toColorInt()) // Light green
            }
        }

        val titleText = TextView(context).apply {
            text = "$title ($source)"
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
        }

        val amountText = TextView(context).apply {
            text = "+ $amount ден."
            setTextColor("#388E3C".toColorInt()) // Dark green
            textSize = 16f
        }

        card.addView(titleText)
        card.addView(amountText)

        incomeListLayout.addView(card, 0)
    }
}
