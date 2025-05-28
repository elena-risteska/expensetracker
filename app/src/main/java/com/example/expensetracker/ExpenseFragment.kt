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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Spinner
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExpenseFragment : Fragment() {

    private lateinit var viewModel: TransactionViewModel
    private lateinit var adapter: TransactionAdapter

    private val purple = "#9C27B0".toColorInt()
    private val yellow = "#FFF176".toColorInt()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = FrameLayout(requireContext()).apply {
            background = GradientDrawable().apply {
                cornerRadius = 48f
                setColor(yellow)
            }
        }

        val recyclerView = RecyclerView(requireContext()).apply {
            layoutManager = LinearLayoutManager(requireContext())
            setPadding(0, 40, 0, 0)
            clipToPadding = false
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
            text = "Add Expense"
            textSize = 16f
            setTextColor(Color.WHITE)
            background = GradientDrawable().apply {
                cornerRadius = 48f
                setColor(purple)
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
        viewModel.getTransactions("expense").observe(viewLifecycleOwner) {
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
            setBackgroundColor(Color.WHITE) // inner content white
        }

        val inputAmount = EditText(context).apply {
            hint = "Amount"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setPadding(32, 24, 32, 24)
            background = GradientDrawable().apply {
                cornerRadius = 24f
                setStroke(2, purple)
                setColor(Color.WHITE)
            }
        }
        container.addView(inputAmount, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = 32 })

        val categories = listOf("Category", "Food", "Transport", "Shopping", "Bills", "Entertainment", "Other")

        val spinnerAdapter = object : ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, categories) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as? android.widget.TextView
                if (position == 0) {
                    tv?.setTextColor(Color.GRAY)
                } else {
                    tv?.setTextColor(Color.BLACK)
                }
                return view
            }
        }
        val spinnerContainer = RelativeLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        val spinner = Spinner(context).apply {
            id = View.generateViewId()
            adapter = spinnerAdapter
            setSelection(0)
            background = GradientDrawable().apply {
                cornerRadius = 24f
                setStroke(2, purple)
                setColor(Color.WHITE)
            }
            setPadding(24, 24, 64, 24)
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_START)
                addRule(RelativeLayout.CENTER_VERTICAL)
            }
        }
        val chevron = ImageView(context).apply {
            setImageDrawable(AppCompatResources.getDrawable(context, android.R.drawable.arrow_down_float))
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_END)
                addRule(RelativeLayout.CENTER_VERTICAL)
                marginEnd = 50
            }
        }

        spinnerContainer.addView(spinner)
        spinnerContainer.addView(chevron)
        container.addView(spinnerContainer, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 24
        })

        val dialog = AlertDialog.Builder(context)
            .setView(container)
            .setTitle("New Expense")
            .setPositiveButton("Add") { _, _ ->
                val amount = inputAmount.text.toString().toDoubleOrNull()
                val category = spinner.selectedItem as String
                if (amount != null) {
                    viewModel.insert(
                        TransactionEntity(
                            amount = amount,
                            category = category,
                            type = "expense"
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
