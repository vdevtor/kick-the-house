package com.vitorthemyth.kickthehouse.presentation.add_team.adapter

import android.content.Context
import android.graphics.Color
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.vitorthemyth.components.R
import com.vitorthemyth.kickthehouse.helper.strings_numers.Empty

class SpinnerGenericAdapter(
    context: Context,
    private val options: List<Pair<Int,Int>>,
    private val initialValue: Pair<Int,Int>?
) : ArrayAdapter<Pair<Int,Int>>(context, 0, options) {

    private val layoutInflater = LayoutInflater.from(context)
    private var itemSpinnerView: View? = null

    private var selectedPosition: Int = 0

    init {
        // Define a posição selecionada com base no valor inicial
        if (initialValue != null) {
            selectedPosition = options.indexOf(initialValue) + 1
        }
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.item_spinner_text, parent, false)
        } else {
            view = convertView
        }
        getItem(position)?.let { opt ->
            setItemForOption(view, opt)
        }
        itemSpinnerView = view

        // Define a seleção do Spinner
        if (position == selectedPosition && initialValue!= null) {
            val spinner = parent as Spinner
            spinner.setSelection(selectedPosition)
        }

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        if (position == 0) {
            val tvOption = itemSpinnerView?.findViewById<TextView>(R.id.tvOption)
            tvOption?.text = String.Empty
            view = layoutInflater.inflate(R.layout.item_spinner_header, parent, false)
            val tvHeader = view.findViewById<TextView>(R.id.tvHeader)
            tvHeader.text = context.getString(com.vitorthemyth.kickthehouse.R.string.select_option)
            view.setBackgroundColor(Color.TRANSPARENT) // set the background to be transparent
            view.setOnClickListener {
                val root = parent.rootView
                root.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK))
                root.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK))
            }
        } else {
            view = layoutInflater.inflate(R.layout.item_drop_down_generic, parent, false)

            getItem(position)?.let { opt ->
                setItemForOption(view, opt)
            }
        }
        return view
    }

    override fun getItemId(position: Int): Long {
        return if (position == 0) {
            0 // for header
        } else {
            options[position - 1].hashCode().toLong() // for other items
        }
    }

    override fun getItem(position: Int): Pair<Int,Int>? {
        return if (position == 0) {
            null
        } else {
            options[position - 1]
        }
    }

    override fun getCount() = options.size + 1

    override fun isEnabled(position: Int) = position != 0

     fun setItemForOption(view: View, option: Pair<Int,Int>) {
        val tvOption = view.findViewById<TextView>(R.id.tvOption)
        tvOption?.text = context.getString(option.first)
    }
}
