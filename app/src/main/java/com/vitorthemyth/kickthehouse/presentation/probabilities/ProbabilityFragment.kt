package com.vitorthemyth.kickthehouse.presentation.probabilities

import TeamSpinnerAdapter
import android.graphics.Rect
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.google.android.material.snackbar.Snackbar
import com.vitorthemyth.components.R
import com.vitorthemyth.domain.models.CalcResults
import com.vitorthemyth.domain.models.Time
import com.vitorthemyth.kickthehouse.FRAG_ADD
import com.vitorthemyth.kickthehouse.FRAG_PROB
import com.vitorthemyth.kickthehouse.FRAG_PROB_MANUAL
import com.vitorthemyth.kickthehouse.FRAG_TEAM_LIST
import com.vitorthemyth.kickthehouse.databinding.FragmentProbabilityBinding
import com.vitorthemyth.kickthehouse.helper.dialog.showDialog
import com.vitorthemyth.kickthehouse.helper.image.loadGifIntoImageView
import com.vitorthemyth.kickthehouse.helper.strings_numers.moneyTextWatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ProbabilityFragment : Fragment() {

    private lateinit var binding: FragmentProbabilityBinding
    private lateinit var teamsAdapter: TeamSpinnerAdapter
    private lateinit var teamsAdapterAlternative: TeamSpinnerAdapter
    private var selectedTeam: String? = null
    private var selectedTeamAlternative: String? = null
    private var teams: List<Time> = listOf()
    private val viewModel: ProbabilityViewModel by activityViewModels()

    private var timeA: Time? = null
    private var timeB: Time? = null

    private var maxLoadingCount = 100
    private var loadingStartCount = 0
    private var loadingDelay = 25L
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
        binding = FragmentProbabilityBinding.inflate(inflater, container, false)

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
        setupOddLabel()
        setupValueLabel()
        fetchTeamList()
        initViews()
        observeTeamList()
        observeResults()
        observeErrors()
        setupLabels()

        // Set initial mode
        updateModeText(MODE.none)
    }

    private fun setupLabels() {
        binding.winProbLabel.text = getString(com.vitorthemyth.kickthehouse.R.string.win_prob)
        binding.loseProbLabel.text = getString(com.vitorthemyth.kickthehouse.R.string.lose_prob)
        binding.evLabel.text = getString(com.vitorthemyth.kickthehouse.R.string.ev_prob)
    }

    private fun fetchTeamList() {
        viewModel.fetchTeamList()
    }

    private fun updateModeText(mode:MODE) {

        when(mode){

            MODE.none ->{
                binding.switchLayout.modeTextView.text =getString(com.vitorthemyth.kickthehouse.R.string.button_auto)
            }

            MODE.manual ->{
                parentFragmentManager.beginTransaction().apply {
                    parentFragmentManager.findFragmentByTag(FRAG_PROB)?.let { hide(it) }
                    parentFragmentManager.findFragmentByTag(FRAG_ADD)?.let { hide(it) }
                    parentFragmentManager.findFragmentByTag(FRAG_TEAM_LIST)?.let { hide(it) }
                    parentFragmentManager.findFragmentByTag(FRAG_PROB_MANUAL)?.let { show(it) }
                    commitNow()
                }
            }
            else -> return
        }
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

    private fun observeTeamList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.teamList.collect { teamList ->
                // update UI with the new teamList
                teamList?.let {
                    teams = it
                    setupAdapter()
                    setupAdapterAlternative()
                }
            }
        }
    }

    private fun observeResults() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.results.collectLatest { results ->
                // Atualizar a interface com os novos resultados
                if (results != null && results.ev != 0.0) {
                    bindResultsOnScreen(results)
                }
            }
        }
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

    private fun bindResultsOnScreen(results: CalcResults) {
        binding.group1.visibility = View.VISIBLE
        binding.group2.visibility = View.GONE
        binding.winProbValue.text = results.formattedWinProb
        binding.loseProbValue.text = results.formattedLoseProb
        binding.evValue.text = results.formattedEv
    }

    private fun setupAdapter() {
        teamsAdapter = TeamSpinnerAdapter(requireContext(), teams.map { it.nome }, selectedTeam)
        binding.firstTeamSpinner.adapter = teamsAdapter

        // Set the selected team (if any)
        teamsAdapter.setSelectedTeam(selectedTeam)

        // Handle the item click event
        binding.firstTeamSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    timeA = if (p2 > 0) {
                        teams[p2 - 1]
                    } else {
                        null
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
    }

    private fun setupAdapterAlternative() {
        teamsAdapterAlternative =
            TeamSpinnerAdapter(requireContext(), teams.map { it.nome }, selectedTeamAlternative)
        binding.secondTeamSpinner.adapter = teamsAdapterAlternative

        // Set the selected team (if any)
        teamsAdapterAlternative.setSelectedTeam(selectedTeamAlternative)

        // Handle the item click event
        binding.secondTeamSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    timeB = if (p2 > 0) {
                        teams[p2 - 1]
                    } else {
                        null
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
    }

    private fun initViews() {
        binding.btnCalculateProb.setOnClickListener {
            if (timeA == null || timeB == null || binding.valueAndOdd.restoreInputText().isEmpty()
                || binding.valueAndOdd.restoreAlternativeInputText().isEmpty()
            ) {
                missingDataWarning(it)
            } else {
                setButtonsAndLabelsVisibility()
                displayLoadingAnimation()
            }
        }

        binding.switchLayout.modeSwitch.setOnClickListener {
            updateModeText(MODE.manual)
        }
    }

    private fun calculateProbabilities() {
        timeA?.let { aTeam ->
            timeB?.let { bTeam ->
                binding.valueAndOdd.restoreInputText().let { value ->
                    binding.valueAndOdd.restoreAlternativeInputText().let { odd ->
                        viewModel.calculateProbabilities(aTeam, bTeam, value, odd)
                    }
                }
            }
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


    private fun displayLoadingAnimation() {
        loadGifIntoImageView(requireContext(), binding.loadingGif, R.drawable.loading_ball)

        lifecycleScope.launch(Dispatchers.Main) {

            for (i in loadingStartCount..maxLoadingCount) {
                delay(loadingDelay)

                val count = i.toString()
                binding.statusLoading.text = when {
                    i <= loadingTextUntil -> getString(com.vitorthemyth.kickthehouse.R.string.loading)
                    i in comparingTeamsTextFrom..comparingTeamsTextTo -> getString(com.vitorthemyth.kickthehouse.R.string.compare_data)
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


    private fun setButtonsAndLabelsVisibility() {
        binding.btnCalculateProb.isEnabled = false
        binding.group1.visibility = View.GONE
        binding.group2.visibility = View.VISIBLE
    }


    companion object {
        const val TAG_PROB = "probabilityFragment"

        enum class MODE{
            none,auto,manual
        }
    }
}





