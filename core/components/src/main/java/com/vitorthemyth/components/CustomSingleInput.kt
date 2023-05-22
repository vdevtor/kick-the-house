package com.vitorthemyth.components

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.vitorthemyth.components.databinding.CustomSingleInputTextBinding

class CustomSingleInputField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var title: String? = null
    private var alternativeTitle: String? = null

    private val binding = CustomSingleInputTextBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    private var fieldState: DropDownState = DropDownState.Enabled
        set(value) {
            field = value
            refreshState()
        }


    init {
        setLayout(attrs)
        refreshState()
    }

    private fun setLayout(attrs: AttributeSet?) {
        attrs?.let { attributeSet ->
            val attributes = context.obtainStyledAttributes(
                attributeSet,
                R.styleable.CustomInputField
            )

            val titleResId =
                attributes.getResourceId(R.styleable.CustomInputField_input_title, 0)

            if (titleResId != 0) {
                title = context.getString(titleResId)
            }

            attributes.recycle()
        }
    }

    fun clearFieldsFocus(){
        binding.inputText.clearFocus()
    }


    fun setDropDownEnabled() {
        fieldState = DropDownState.Enabled
    }

    fun setDropDownDisabled() {
        fieldState = DropDownState.Disabled
    }


    fun setFieldTitle(title:String){
        binding.tvTitle.text = title
    }

    fun setInputText(text:String){
        binding.inputText.setText(text)
    }

    fun setInputTextType(inputType: Int){
        binding.inputText.inputType = inputType
    }


    fun unableInputField(){
        binding.inputText.isEnabled = false
    }


    fun onDropdownClicked(onFieldClicked: onFieldClicked){
        when (fieldState) {
            DropDownState.Enabled -> {
                binding.inputText.isFocusable = false
                binding.inputText.setOnClickListener {
                    onFieldClicked.invoke()
                }
            }
            else ->return
        }
    }


    fun restoreInputText() : String {
        return binding.inputText.text.toString()
    }

    private fun refreshState() {
        binding.dropDownArrow.visibility = fieldState.arrowVisibility
        refreshDrawableState()

        when (fieldState) {
            DropDownState.Enabled -> {
                binding.inputText.inputType = InputType.TYPE_NULL
            }

            DropDownState.Disabled -> {
                binding.inputText.inputType = InputType.TYPE_CLASS_TEXT
            }
        }
    }

    fun setOnLongPressAction(longClicked: onSingleInputClicked){
        binding.inputText.setOnLongClickListener {
            longClicked.invoke()
            true
        }
    }

    sealed class DropDownState(val arrowVisibility: Int) {
        object Enabled : DropDownState(View.VISIBLE)
        object Disabled : DropDownState( View.GONE)
    }

}

typealias onSingleInputClicked = () -> Unit
typealias onFieldClicked = () -> Unit