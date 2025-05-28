package com.example.expensetracker

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(
    private val items: MutableList<TransactionEntity>,
    private val onDelete: (TransactionEntity) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(val layout: LinearLayout) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val context = parent.context

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(32, 24, 32, 24)
            background = GradientDrawable().apply {
                cornerRadius = 32f
                setColor("#D81B60".toColorInt())
            }
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(32, 16, 32, 16)
            }
            gravity = Gravity.CENTER_VERTICAL
        }

        val textView = TextView(context).apply {
            id = View.generateViewId()
            setTextColor(Color.DKGRAY)
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }

        val deleteButton = ImageButton(context).apply {
            id = View.generateViewId()
            setImageResource(R.drawable.ic_delete)
            setBackgroundColor(Color.TRANSPARENT)
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        layout.addView(textView)
        layout.addView(deleteButton)

        return TransactionViewHolder(layout)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = items[position]
        val textView = holder.layout.getChildAt(0) as TextView
        val deleteBtn = holder.layout.getChildAt(1) as ImageButton

        textView.text = "- ${item.amount} ден. (${item.category})"
        deleteBtn.setOnClickListener { onDelete(item) }
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun update(newList: List<TransactionEntity>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    fun getItemAt(position: Int): TransactionEntity = items[position]
}
