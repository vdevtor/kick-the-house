package com.vitorthemyth.kickthehouse.presentation.probabilities

import android.graphics.Rect
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.google.android.material.snackbar.Snackbar
import com.vitorthemyth.components.R
import com.vitorthemyth.domain.models.CalcResults
import com.vitorthemyth.kickthehouse.FRAG_ADD
import com.vitorthemyth.kickthehouse.FRAG_PROB
import com.vitorthemyth.kickthehouse.FRAG_PROB_MANUAL
import com.vitorthemyth.kickthehouse.FRAG_TEAM_LIST
import com.vitorthemyth.kickthehouse.databinding.FragmentProbabilityManualBinding
import com.vitorthemyth.kickthehouse.helper.dialog.showDialog
import com.vitorthemyth.kickthehouse.helper.image.loadGifIntoImageView
import com.vitorthemyth.kickthehouse.helper.strings_numers.moneyTextWatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ManualProbabilityFragment : Fragment() {

    private lateinit var binding: FragmentProbabilityManualBinding

    private val viewModel: ProbabilityViewModel by activityViewModels()

    private var maxLoadingCount = 100
    private var loadingStartCount = 0
    private var loadingDelay = 20L
    private var loadingTextUntil = 30
    private var comparingTeamsTextFrom = 31
    private var comparingTeamsTextTo = 66
    private var resetAnimationAt = 100

    private var isKeyboardVisible = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProbabilityManualBinding.inflate(inflater, container, false)

        // Get the ViewTreeObserver of the root view
        val viewTreeObserver = binding.root.viewTreeObserver

        // Add a OnGlobalLayoutListener to the ViewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener {
            // Check if the keyboard is being displayed
            val r = Rect()
            binding.root.getWindowVisibleDisplayFrame(r)
            val screenHeight = binding.root.rootView.height
            val keyboardHeight = screenHeight - r.bottom

            val isKeyboardVisibleNow = keyboardHeight > screenHeight * 0.15

            if (isKeyboardVisible != isKeyboardVisibleNow) {
                isKeyboardVisible = isKeyboardVisibleNow
                onKeyboardStateChanged(isKeyboardVisibleNow)
            }
        }

        return binding.root
    }

    // Method to check whether the keyboard is being displayed or not
    private fun onKeyboardStateChanged(isVisible: Boolean) {
        // Update your view accordingly
        if (!isVisible) binding.root.clearFocus()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set initial mode
        updateModeText(ProbabilityFragment.Companion.MODE.none)

        initViews()
        setupOddLabel()
        setupValueLabel()
        setupWinProb()
        setupWinProb()
        setupLossProb()
        observeResults()
        setupLabels()
        observeErrors()
    }

    private fun observeErrors() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.errorMessage.collectLatest { messageId ->
                // Atualizar a interface com os novos resultados
                if (messageId != null){
                    showDialog(
                        requireContext(),
                        getString(com.vitorthemyth.kickthehouse.R.string.txt_attention),
                        getString(messageId),
                        onPositiveClick = {

                        },
                        onNegativeClick = {

                        }
                    )
                }
            }
        }
    }

    private fun setupLabels() {
        binding.winProbLabel.text = getString(com.vitorthemyth.kickthehouse.R.string.win_prob)
        binding.loseProbLabel.text = getString(com.vitorthemyth.kickthehouse.R.string.lose_prob)
        binding.evLabel.text = getString(com.vitorthemyth.kickthehouse.R.string.ev_prob)
    }

    private fun updateModeText(mode: ProbabilityFragment.Companion.MODE) {
        when(mode){

            ProbabilityFragment.Companion.MODE.none ->{
                binding.switchLayout.modeTextView.text = getString(com.vitorthemyth.kickthehouse.R.string.button_manual)
            }

            ProbabilityFragment.Companion.MODE.auto ->{
                parentFragmentManager.beginTransaction().apply {
                    parentFragmentManager.findFragmentByTag(FRAG_PROB)?.let { show(it) }
                    parentFragmentManager.findFragmentByTag(FRAG_ADD)?.let { hide(it) }
                    parentFragmentManager.findFragmentByTag(FRAG_PROB_MANUAL)?.let { hide(it) }
                    parentFragmentManager.findFragmentByTag(FRAG_TEAM_LIST)?.let { hide(it) }
                    commitNow()
                }
            }
            else -> return
        }
    }

    private fun initViews(){
        binding.switchLayout.modeSwitch.setOnClickListener {
            updateModeText(ProbabilityFragment.Companion.MODE.auto)
        }

        binding.btnCalculateProb.setOnClickListener {
            if (binding.probWinLoss.restoreInputText().isEmpty() || binding.probWinLoss.restoreAlternativeInputText().isEmpty()
                || binding.valueAndOdd.restoreInputText().isEmpty()
                || binding.valueAndOdd.restoreAlternativeInputText().isEmpty()
            ) {
                missingDataWarning(it)
            } else {
                setButtonsAndLabelsVisibility()
                displayLoadingAnimation()
            }
        }
    }

    private fun observeResults() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.resultsManually.collectLatest { results ->
                // Atualizar a interface com os novos resultados
                if (results != null && results.ev != 0.0) {
                    bindResultsOnScreen(results)
                }
            }
        }
    }

    private fun bindResultsOnScreen(results: CalcResults) {
        binding.group1.visibility = View.VISIBLE
        binding.group2.visibility = View.GONE
        binding.winProbValue.text = results.formattedWinProb
        binding.loseProbValue.text = results.formattedLoseProb
        binding.evValue.text = results.formattedEv
    }

    private fun displayLoadingAnimation() {
        loadGifIntoImageView(requireContext(), binding.loadingGif, R.drawable.loading_ball)

        lifecycleScope.launch(Dispatchers.Main) {

            for (i in loadingStartCount..maxLoadingCount) {
                delay(loadingDelay)

                val count = i.toString()
                binding.statusLoading.text = when {
                    i <= loadingTextUntil -> getString(com.vitorthemyth.kickthehouse.R.string.loading)
                    i in comparingTeamsTextFrom..comparingTeamsTextTo -> getString(com.vitorthemyth.kickthehouse.R.string.analyze_bet)
                    else -> getString(com.vitorthemyth.kickthehouse.R.string.analyze_bet)
                }
                binding.statusLoadingPercent.text = count.let { "$it%" }

                if (i >= resetAnimationAt) {
                    binding.statusLoadingPercent.text =
                        getString(com.vitorthemyth.kickthehouse.R.string.completed_txt)
                    stopGifAnimationAndShowButton()
                    calculateProbabilities()
                }
            }
        }
    }

    private fun calculateProbabilities() {
        binding.probWinLoss.restoreInputText().let { lossProb ->
            binding.probWinLoss.restoreAlternativeInputText().let{ probWin ->
                binding.valueAndOdd.restoreInputText().let { value ->
                    binding.valueAndOdd.restoreAlternativeInputText().let { odd ->
                        viewModel.calculateManualProbabilities(probWin, lossProb, odd, value)
                    }
                }
            }
        }
    }

    private fun stopGifAnimationAndShowButton() {
        val backgroundDrawable = binding.loadingGif.drawable
        if (backgroundDrawable is GifDrawable) {
            backgroundDrawable.stop()
            binding.loadingGif.viewTreeObserver.addOnPreDrawListener(object :
                ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    binding.loadingGif.viewTreeObserver.removeOnPreDrawListener(this)
                    binding.loadingGif.visibility = View.GONE
                    binding.btnCalculateProb.isEnabled = true
                    return true
                }
            })
        } else if (backgroundDrawable is TransitionDrawable) {
            for (i in 0 until backgroundDrawable.numberOfLayers) {
                val layerDrawable = backgroundDrawable.getDrawable(i)
                if (layerDrawable is GifDrawable) {
                    layerDrawable.stop()
                }
            }
            binding.loadingGif.visibility = View.GONE
            binding.btnCalculateProb.isEnabled = true
        }
    }

    private fun missingDataWarning(it: View) {
        Snackbar.make(
            it,
            requireContext()
                .getString(com.vitorthemyth.kickthehouse.R.string.missing_data),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun setButtonsAndLabelsVisibility() {
        binding.btnCalculateProb.isEnabled = false
        binding.group1.visibility = View.GONE
        binding.group2.visibility = View.VISIBLE
    }

    private fun setupValueLabel() {
        binding.valueAndOdd.apply {
            val title = getString(com.vitorthemyth.kickthehouse.R.string.desired_value)
            setFieldTitle(title)
            setDropDownDisabled()
            setInputTextType(InputType.TYPE_NUMBER_FLAG_DECIMAL)
            setInputTextWatcher(moneyTextWatcher)
        }
    }

    private fun setupOddLabel() {
        binding.valueAndOdd.apply {
            val title = getString(com.vitorthemyth.kickthehouse.R.string.house_odd)
            setAlternativeFieldTitle(title)
            setAlternativeDropDownDisabled()
            setAlternativeHint(getString(com.vitorthemyth.kickthehouse.R.string.odd_hint))
            setAlternativeInputTextType(InputType.TYPE_NUMBER_FLAG_DECIMAL)
        }
    }

    private fun setupWinProb() {
        binding.probWinLoss.apply {
            val title = getString(com.vitorthemyth.kickthehouse.R.string.win_prob)
            setAlternativeFieldTitle(title)
            setAlternativeDropDownDisabled()
            setAlternativeHint(getString(com.vitorthemyth.kickthehouse.R.string.prob_percent_hint))
            setAlternativeInputTextType(InputType.TYPE_CLASS_NUMBER)
        }
    }

    private fun setupLossProb() {
        binding.probWinLoss.apply {
            val title = getString(com.vitorthemyth.kickthehouse.R.string.lose_prob)
            setFieldTitle(title)
            setDropDownDisabled()
            setInputTextType(InputType.TYPE_CLASS_NUMBER)
            setHint(getString(com.vitorthemyth.kickthehouse.R.string.prob_percent_hint))
        }
    }

}