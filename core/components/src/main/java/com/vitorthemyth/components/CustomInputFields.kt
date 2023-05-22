package com.vitorthemyth.components

import android.content.Context
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.vitorthemyth.components.databinding.CustomDoubleInputsTextBinding

class CustomInputField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var title: String? = null
    private var alternativeTitle: String? = null

    private val binding = CustomDoubleInputsTextBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    private var fieldState: DropDownState = DropDownState.Enabled
        set(value) {
            field = value
            refreshState()
        }


    private var alternativeFieldState: DropDownState = DropDownState.Enabled
        set(value) {
            field = value
            refreshAlternativeState()
        }

    init {
        setLayout(attrs)
        refreshState()
        refreshAlternativeState()
    }

    private fun setLayout(attrs: AttributeSet?) {
        attrs.let { attributeSet ->
            val attributes = context.obtainStyledAttributes(
                attributeSet,
                R.styleable.CustomInputField
            )

            val titleResId =
                attributes.getResourceId(R.styleable.CustomInputField_input_title, 0)

            if (titleResId != 0) {
                title = context.getString(titleResId)
            }

            val titleAlternative =
                attributes.getResourceId(R.styleable.CustomInputField_alternative_input_title, 0)

            if (titleAlternative != 0) {
                alternativeTitle = context.getString(titleAlternative)
            }

            attributes.recycle()
        }
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

    fun hideAlternativeInput(){
        binding.inputTextAlternative.visibility = View.INVISIBLE
        binding.tvAlternativeTitle.visibility = View.INVISIBLE
        binding.dropDownArrowAlternative.visibility = View.GONE
    }

    fun showAlternativeInput(){
        binding.inputTextAlternative.visibility = View.VISIBLE
        binding.tvAlternativeTitle.visibility = View.VISIBLE
        binding.dropDownArrowAlternative.visibility = View.VISIBLE
    }

    fun setAlternativeInputText(text:String){
        binding.inputTextAlternative.setText(text)
    }

    fun setAlternativeInputTextType(inputType: Int){
        binding.inputTextAlternative.inputType = inputType
    }
    fun setAlternativeInputTextIMEOptions(action: Int){
        binding.inputTextAlternative.imeOptions = action
    }

    fun setInputTextType(inputType: Int){
        binding.inputText.inputType = inputType
    }
    fun setInputTextIMEOptions(action: Int){
        binding.inputText.imeOptions = action
    }


    fun setAlternativeFieldTitle(title:String){
        binding.tvAlternativeTitle.text = title
    }

    fun setAlternativeDropDownEnabled() {
        alternativeFieldState = DropDownState.Enabled
    }

    fun setAlternativeDropDownDisabled() {
        alternativeFieldState = DropDownState.Disabled
    }

    fun setAlternativeIsFocusable(isFocusable : Boolean){
        binding.inputTextAlternative.isFocusable = isFocusable
    }

    fun setAlternativeHint(hint:String){
        binding.inputTextAlternative.hint = hint
    }
    fun setHint(hint:String){
        binding.inputText.hint = hint
    }


    fun setOnLongPressAction(longClicked: onFieldLongClicked){
        binding.inputText.setOnLongClickListener {
            longClicked.invoke()
            true
        }
    }

    fun setOnAlternativeLongPressAction(longClicked: onAlternativeFieldLongClicked){
        binding.inputTextAlternative.setOnLongClickListener {
            longClicked.invoke()
            true
        }
    }

    fun setAlternativeInputAsDataPicker(onAlternativeFieldClicked: onAlternativeFieldClicked){

        binding.inputTextAlternative.apply {
            isFocusable = false
            setCompoundDrawablesWithIntrinsicBounds(0,0,
                R.drawable.calendar_icon,0)

            setOnClickListener {
                onAlternativeFieldClicked.invoke()
            }
        }
    }

    fun setInputAsDataPicker(onFieldClicked: onFieldClicked){

        binding.inputText.apply {
            isFocusable = false
            setCompoundDrawablesWithIntrinsicBounds(0,0,
                R.drawable.calendar_icon,0)

            setOnClickListener {
                onFieldClicked.invoke()
            }
        }
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



    fun onAlternativeDropdownClicked(onAlternativeFieldClick: onAlternativeFieldClicked){
        when (alternativeFieldState) {
            DropDownState.Enabled -> {
                binding.inputTextAlternative.isFocusable = false
                binding.inputTextAlternative.setOnClickListener {
                    onAlternativeFieldClick.invoke()
                }
            }
            else ->return
        }
    }

    fun onAlternativeClicked(onAlternativeFieldClick: onAlternativeFieldClicked){
        binding.inputTextAlternative.isFocusable = false
       binding.inputTextAlternative.setOnClickListener {
           onAlternativeFieldClick.invoke()
       }
    }

    fun restoreAlternativeInputText() : String {
        return binding.inputTextAlternative.text.toString()
    }

    fun restoreInputText() : String {
        return binding.inputText.text.toString()
    }

    fun clearFieldsFocus(){
        binding.inputText.clearFocus()
        binding.inputTextAlternative.clearFocus()
    }

    fun setInputTextWatcher(textWatcher: TextWatcher){
        binding.inputText.addTextChangedListener(textWatcher)
    }

    fun setAlternativeInputTextWatcher(textWatcher: TextWatcher){
        binding.inputTextAlternative.addTextChangedListener(textWatcher)
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

    private fun refreshAlternativeState() {
        binding.dropDownArrowAlternative.visibility = alternativeFieldState.arrowVisibility
        refreshDrawableState()

        when (alternativeFieldState) {
            DropDownState.Enabled -> {
                binding.inputTextAlternative.inputType = InputType.TYPE_NULL
            }

            DropDownState.Disabled -> {
                binding.inputTextAlternative.inputType = InputType.TYPE_CLASS_TEXT
            }
        }
    }

    sealed class DropDownState(val arrowVisibility: Int) {
        object Enabled : DropDownState(View.VISIBLE)
        object Disabled : DropDownState( View.GONE)
    }

}

typealias onAlternativeFieldClicked = () -> Unit

typealias onFieldLongClicked = () -> Unit
typealias onAlternativeFieldLongClicked = () -> Unit