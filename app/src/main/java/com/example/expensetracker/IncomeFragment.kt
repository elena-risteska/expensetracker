package com.example.expensetracker

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
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
import android.widget.Spinner
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class IncomeFragment : Fragment() {

    private lateinit var viewModel: TransactionViewModel
    private lateinit var adapter: TransactionAdapter

    private val green = "#388E3C".toColorInt()
    private val white = Color.WHITE
    private val yellow = "#FFF176".toColorInt()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = FrameLayout(requireContext()).apply {
            setBackgroundColor(yellow)
            setPadding(0, 32, 0, 0)  // Push list down a bit
        }

        val recyclerView = RecyclerView(requireContext()).apply {
            layoutManager = LinearLayoutManager(requireContext())
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            ).apply {
                setMargins(0, 0, 0, 200)
            }
        }

        adapter = TransactionAdapter(mutableListOf()) {
            viewModel.delete(it)
        }
        recyclerView.adapter = adapter

        val addButton = Button(requireContext()).apply {
            text = "Add Income"
            textSize = 16f
            setTextColor(white)
            background = GradientDrawable().apply {
                cornerRadius = 48f
                setColor(green)
            }
            setPadding(40, 30, 40, 30)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            ).apply {
                setMargins(64, 16, 64, 64)
            }
            setOnClickListener { showAddDialog() }
        }

        root.addView(recyclerView)
        root.addView(addButton)

        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        viewModel.getTransactions("income").observe(viewLifecycleOwner) {
            adapter.update(it)
        }

        setupSwipeToDelete(recyclerView)

        return root
    }

    private fun setupSwipeToDelete(recyclerView: RecyclerView) {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, tgt: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = adapter.getItemAt(position)
                viewModel.delete(item)
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun showAddDialog() {
        val context = requireContext()

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 32, 48, 16)
            setBackgroundColor(white)
        }

        val inputAmount = EditText(context).apply {
            hint = "Amount"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setPadding(32, 24, 32, 24)
            background = GradientDrawable().apply {
                cornerRadius = 24f
                setStroke(2, green)
                setColor(white)
            }
        }
        container.addView(inputAmount, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 32
        })

        val categories = listOf("Category", "Salary", "Bonus", "Other")

        // Create a custom adapter with disabled first item and grayed text
        val adapter = object : ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, categories) {
            override fun isEnabled(position: Int): Boolean {
                // Disable first item (the label)
                return position != 0
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as? android.widget.TextView
                if (position == 0) {
                    // Gray color for the first item
                    tv?.setTextColor(Color.GRAY)
                } else {
                    // Default text color for others
                    tv?.setTextColor(Color.BLACK)
                }
                return view
            }
        }

        // Create Spinner container to add chevron icon
        val spinnerContainer = FrameLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 24
            }
        }

        val spinner = Spinner(context).apply {
            this.adapter = adapter
            background = GradientDrawable().apply {
                cornerRadius = 24f
                setStroke(2, green)
                setColor(white)
            }
            setPadding(24, 24, 24, 24)
            // Initially select the label item
            setSelection(0)
        }

        spinnerContainer.addView(spinner)

        // Add chevron ImageView on the right side of Spinner
        val chevron = android.widget.ImageView(context).apply {
            setImageResource(android.R.drawable.arrow_down_float) // You can replace with a custom chevron drawable if you want
            val size = 48
            layoutParams = FrameLayout.LayoutParams(size, size, Gravity.END or Gravity.CENTER_VERTICAL).apply {
                marginEnd = 24
            }
            isClickable = false
            isFocusable = false
        }
        spinnerContainer.addView(chevron)

        container.addView(spinnerContainer)

        val dialog = AlertDialog.Builder(context)
            .setView(container)
            .setTitle("New Income")
            .setPositiveButton("Add") { _, _ ->
                val amount = inputAmount.text.toString().toDoubleOrNull()
                val category = spinner.selectedItem as String
                // Ignore if first item is selected
                if (amount != null && spinner.selectedItemPosition != 0) {
                    viewModel.insert(
                        TransactionEntity(
                            amount = amount,
                            category = category,
                            type = "income"
                        )
                    )
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.window?.setBackgroundDrawable(
            GradientDrawable().apply {
                cornerRadius = 48f
                setColor(Color.WHITE)
            }
        )

        dialog.show()
    }

}
