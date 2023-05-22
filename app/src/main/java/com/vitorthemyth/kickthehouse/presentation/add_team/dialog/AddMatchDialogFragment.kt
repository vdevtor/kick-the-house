package com.vitorthemyth.kickthehouse.presentation.add_team.dialog

import android.app.Dialog
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.vitorthemyth.domain.models.Equipe
import com.vitorthemyth.domain.models.Estatisticas
import com.vitorthemyth.domain.models.Partida
import com.vitorthemyth.domain.models.Resultado
import com.vitorthemyth.kickthehouse.R
import com.vitorthemyth.kickthehouse.databinding.DialogAddMatchBinding
import com.vitorthemyth.kickthehouse.helper.date_picker.showDatePickerDialog
import com.vitorthemyth.kickthehouse.helper.dialog.DialogInteractionListener
import com.vitorthemyth.kickthehouse.helper.dialog.setFullScreen
import com.vitorthemyth.kickthehouse.helper.dialog.showDialog
import com.vitorthemyth.kickthehouse.helper.strings_numers.Default
import com.vitorthemyth.kickthehouse.helper.strings_numers.Empty
import com.vitorthemyth.kickthehouse.presentation.add_team.AddNewTeamFragment.Companion.TAG_ADD_MATCH
import com.vitorthemyth.kickthehouse.presentation.add_team.AddTeamViewModel
import com.vitorthemyth.kickthehouse.presentation.add_team.adapter.SpinnerGenericAdapter
import com.vitorthemyth.kickthehouse.presentation.add_team.options.Difficult
import com.vitorthemyth.kickthehouse.presentation.add_team.options.Importance
import com.vitorthemyth.kickthehouse.presentation.add_team.options.MatchLocal

class AddMatchDialogFragment : DialogFragment() {

    private var interactionListener: DialogInteractionListener? = null

    private val viewModel: AddTeamViewModel by activityViewModels()

    private lateinit var binding: DialogAddMatchBinding

    private lateinit var importanceAdapter: SpinnerGenericAdapter
    private lateinit var difficultAdapter: SpinnerGenericAdapter
    private lateinit var resultAdapter: SpinnerGenericAdapter
    private lateinit var localAdapter: SpinnerGenericAdapter

    private val SCROLL_VALUE = 480
    private val EXECUTION_DELAY = 600L
    private val EXECUTION_DELAY_BACK = 1400L


    private var importance: Int? = null
    private var difficult: Int? = null
    private var resultado: Int? = null
    private var local: Int? = null

    private var isEdit: Boolean = false
    private var token: String = String.Empty
    private var match: Partida? = null


    private var isKeyboardVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireActivity(), theme)

        //defining dialog animation
        dialog.window?.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        dialog.window?.attributes?.windowAnimations =
            R.style.DialogAnimation

        dialog.setOnKeyListener { _, keyCode, event -> // prevent dialog to be dismissed when pressing back button
            keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddMatchBinding.inflate(inflater, container, false)

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

        loadArguments()

        return binding.root
    }

    private fun loadArguments() {
        isEdit = arguments?.getBoolean(IS_EDIT) ?: Boolean.Default
        token = arguments?.getString(MATCH_TOKEN) ?: String.Empty
        match = viewModel.getTeamModel()?.ultimasPartidas?.find { it.matchToken == token }
    }

    // Method to check whether the keyboard is being displayed or not
    private fun onKeyboardStateChanged(isVisible: Boolean) {
        // Update your view accordingly
        if (!isVisible) binding.root.clearFocus()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupOpponentField()
        setupDifficultAdapter()
        setupImportanceAdapter()
        setupLabels()
        setupResultsAndLocal()
        setupYellowAndRedCards()
        setupFoulsAndCorners()
        setupShootsInOut()
        setupGoalsScoredAndSuffered()
        setupBallControlAndDate()
        executeSmoothScroll()
    }

    private fun setupLabels() {
        binding.tvDifficult.text = requireContext().getString(R.string.select_a_difficult)
        binding.tvImportance.text = requireContext().getString(R.string.select_importance)
        binding.tvLocal.text = requireContext().getString(R.string.select_local)
        binding.tvResult.text = requireContext().getString(R.string.select_result)
    }

    private fun setupOpponentField() {
        binding.opponentName.apply {
            val title = requireContext().getString(R.string.opponent_team_name)
            setFieldTitle(title)
            setDropDownDisabled()

            if (isEdit) {
                setInputText(match?.adversario?.nome ?: String.Empty)
            }
        }
    }

    fun setInteractionListener(listener: DialogInteractionListener) {
        interactionListener = listener
    }

    private fun initViews() {
        binding.ivBackArrow.setOnClickListener {
            interactionListener?.onPositiveCallback(data = false)
        }

        binding.btnSaveMatch.setOnClickListener {
            if (isEdit) editMatch() else buildMatch()
        }
    }

    private fun editMatch() {
        val checkIfFieldsAreOkay = verifyFields()

        if (checkIfFieldsAreOkay) {
            val team = viewModel.getTeamModel()
            val index = team?.ultimasPartidas?.indexOfFirst { it.matchToken == token } ?: Int.Empty

            team?.ultimasPartidas?.set(
                index, Partida(
                    matchToken = token,
                    adversario = Equipe(
                        nome = binding.opponentName.restoreInputText(),
                        ultimasPartidas = null
                    ),
                    dificuldade = difficult ?: Int.Empty,
                    importancia = importance ?: Int.Empty,
                    estatisticas = Estatisticas(
                        golsMarcados = binding.goalsScoredAndSuffered.restoreInputText().toInt(),
                        golsSofridos = binding.goalsScoredAndSuffered.restoreAlternativeInputText()
                            .toInt(),
                        posseDeBola = binding.ballControlAndDate.restoreInputText().toInt(),
                        chutesAoGol = binding.shootsInOut.restoreInputText().toInt(),
                        chutesForaDoGol = binding.shootsInOut.restoreAlternativeInputText().toInt(),
                        escanteios = binding.foulsAndCorners.restoreAlternativeInputText().toInt(),
                        faltas = binding.foulsAndCorners.restoreInputText().toInt(),
                        cartoesVermelhos = binding.yellowAndRedCards.restoreAlternativeInputText()
                            .toInt(),
                        cartoesAmarelos = binding.yellowAndRedCards.restoreInputText().toInt(),
                        jogouEmCasa = local == 0,
                        resultado = Resultado.returnType(resultado ?: Int.Empty),
                    ),
                    date = binding.ballControlAndDate.restoreAlternativeInputText()
                )
            )


            viewModel.setTeam(team)
            interactionListener?.onPositiveCallback(data = team)


        } else {
            showDialog(
                requireContext(),
                title = requireContext().getString(R.string.txt_attention),
                message = requireContext().getString(R.string.empty_fields),
                onPositiveClick = {

                },
                onNegativeClick = {

                }
            )
        }
    }

    private fun buildMatch() {

        val checkIfFieldsAreOkay = verifyFields()

        if (checkIfFieldsAreOkay) {
            val team = viewModel.getTeamModel()
            team?.ultimasPartidas?.add(
                Partida(
                    matchToken = System.currentTimeMillis().toString(),
                    adversario = Equipe(
                        nome = binding.opponentName.restoreInputText(),
                        ultimasPartidas = null
                    ),
                    dificuldade = difficult ?: Int.Empty,
                    importancia = importance ?: Int.Empty,
                    estatisticas = Estatisticas(
                        golsMarcados = binding.goalsScoredAndSuffered.restoreInputText().toInt(),
                        golsSofridos = binding.goalsScoredAndSuffered.restoreAlternativeInputText()
                            .toInt(),
                        posseDeBola = binding.ballControlAndDate.restoreInputText().toInt(),
                        chutesAoGol = binding.shootsInOut.restoreInputText().toInt(),
                        chutesForaDoGol = binding.shootsInOut.restoreAlternativeInputText().toInt(),
                        escanteios = binding.foulsAndCorners.restoreAlternativeInputText().toInt(),
                        faltas = binding.foulsAndCorners.restoreInputText().toInt(),
                        cartoesVermelhos = binding.yellowAndRedCards.restoreAlternativeInputText()
                            .toInt(),
                        cartoesAmarelos = binding.yellowAndRedCards.restoreInputText().toInt(),
                        jogouEmCasa = local == 0,
                        resultado = Resultado.returnType(resultado ?: Int.Empty),
                    ),
                    date = binding.ballControlAndDate.restoreAlternativeInputText()
                )
            )

            viewModel.setTeam(team)
            interactionListener?.onPositiveCallback(data = team)


        } else {
            showDialog(
                requireContext(),
                title = requireContext().getString(R.string.txt_attention),
                message = requireContext().getString(R.string.empty_fields),
                onPositiveClick = {

                },
                onNegativeClick = {

                }
            )
        }
    }

    private fun verifyFields(): Boolean {
        if (resultado == null || local == null || difficult == null || importance == null) {
            return false
        }

        //name

        if (binding.opponentName.restoreInputText().isEmpty()) {
            return false
        }
        //cards
        if (binding.yellowAndRedCards.restoreInputText().isEmpty() ||
            binding.yellowAndRedCards.restoreAlternativeInputText().isEmpty()
        ) {
            return false
        }

        //fouls and corners
        if (binding.foulsAndCorners.restoreInputText().isEmpty() ||
            binding.foulsAndCorners.restoreAlternativeInputText().isEmpty()
        ) {
            return false
        }

        //shoots in
        if (binding.shootsInOut.restoreInputText().isEmpty() ||
            binding.shootsInOut.restoreAlternativeInputText().isEmpty()
        ) {
            return false
        }

        // goals


        if (binding.goalsScoredAndSuffered.restoreInputText().isEmpty() ||
            binding.goalsScoredAndSuffered.restoreAlternativeInputText().isEmpty()
        ) {
            return false
        }

        //ball control and position
        if (binding.ballControlAndDate.restoreInputText().isEmpty() ||
            binding.ballControlAndDate.restoreAlternativeInputText().isEmpty()
        ) {
            return false
        }

        return true
    }

    private fun setupImportanceAdapter() {
        val list = Importance.values().map { Pair(it.text, it.value) }
        var initialItem: Pair<Int, Int>? = null
        if (isEdit) {
            list.find { it.second == match?.importancia }?.let {
                importance = it.second
                initialItem = it
            }
        }

        importanceAdapter = SpinnerGenericAdapter(requireContext(), list, initialItem)
        binding.importance.adapter = importanceAdapter

        // Handle the item click event
        binding.importance.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (p2 > 0) {
                        importance = list[p2 - 1].second
                        Toast.makeText(requireContext(), "$importance", Toast.LENGTH_SHORT).show()
                    } else {
                        importance = 0
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
    }

    private fun setupDifficultAdapter() {
        val list = Difficult.values().map { Pair(it.text, it.value) }
        var initialItem: Pair<Int, Int>? = null
        if (isEdit) {
            list.find { it.second == match?.dificuldade }?.let {
                difficult = it.second
                initialItem = it
            }
        }

        difficultAdapter = SpinnerGenericAdapter(requireContext(), list, initialItem)
        binding.dificult.adapter = difficultAdapter

        // Handle the item click event
        binding.dificult.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (p2 > 0) {
                        difficult = list[p2 - 1].second
                        Toast.makeText(requireContext(), "$difficult", Toast.LENGTH_SHORT).show()
                    } else {
                        difficult = 0
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }

    }

    private fun setupResultsAndLocal() {
        setupResults()
        setupLocal()
    }

    private fun setupYellowAndRedCards() {
        setupYellowCard()
        setupRedCard()
    }

    private fun setupFoulsAndCorners() {
        setupFouls()
        setupCorners()
    }

    private fun setupYellowCard() {
        binding.yellowAndRedCards.apply {
            val title = requireContext().getString(R.string.num_yellow_cards)
            setFieldTitle(title)
            setDropDownDisabled()
            setInputTextType(InputType.TYPE_CLASS_NUMBER)

            if (isEdit) {
                setInputText(match?.estatisticas?.cartoesAmarelos.toString())
            }
        }
    }

    private fun setupRedCard() {
        binding.yellowAndRedCards.apply {
            val title = requireContext().getString(R.string.num_red_cards)
            setAlternativeFieldTitle(title)
            setAlternativeDropDownDisabled()
            setAlternativeInputTextType(InputType.TYPE_CLASS_NUMBER)

            if (isEdit) {
                setAlternativeInputText(match?.estatisticas?.cartoesVermelhos.toString())
            }
        }
    }

    private fun setupCorners() {
        binding.foulsAndCorners.apply {
            val title = requireContext().getString(R.string.num_corners)
            setAlternativeFieldTitle(title)
            setAlternativeDropDownDisabled()
            setAlternativeInputTextType(InputType.TYPE_CLASS_NUMBER)

            if (isEdit) {
                setAlternativeInputText(match?.estatisticas?.escanteios.toString())
            }
        }
    }

    private fun setupFouls() {
        binding.foulsAndCorners.apply {
            val title = requireContext().getString(R.string.num_fouls)
            setFieldTitle(title)
            setDropDownDisabled()
            setInputTextType(InputType.TYPE_CLASS_NUMBER)
            if (isEdit) {
                setInputText(match?.estatisticas?.faltas.toString())
            }
        }
    }

    private fun setupResults() {
        val list = Resultado.values().map { Pair(it.text, it.value) }
        var initialItem: Pair<Int, Int>? = null
        if (isEdit) {
            list.find { it.second == Resultado.returnType(match?.estatisticas?.resultado?.value).value }
                ?.let {
                    resultado = it.second
                    initialItem = it
                }
        }

        resultAdapter = SpinnerGenericAdapter(requireContext(), list, initialItem)
        binding.resultSpinner.adapter = resultAdapter

        // Handle the item click event
        binding.resultSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (p2 > 0) {
                        resultado = list[p2 - 1].second
                        Toast.makeText(requireContext(), "$resultado", Toast.LENGTH_SHORT).show()
                    } else {
                        resultado = 0
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }


    }

    private fun setupLocal() {
        val list = MatchLocal.values().map { Pair(it.text, it.value) }
        var initialItem: Pair<Int, Int>? = null
        if (isEdit) {
            val place = if (match?.estatisticas?.jogouEmCasa == true) 0 else 1

            list.find { it.second == MatchLocal.values().find { it.value == place }?.value }?.let {
                local = it.second
                initialItem = it
            }
        }
        localAdapter = SpinnerGenericAdapter(requireContext(), list, initialItem)
        binding.localSpinner.adapter = localAdapter

        // Handle the item click event
        binding.localSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (p2 > 0) {
                        local = list[p2 - 1].second
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }
    }

    private fun setupShootsInOut() {
        setupShootsIn()
        setupShootsOut()
    }

    private fun setupShootsIn() {
        binding.shootsInOut.apply {
            val title = requireContext().getString(R.string.num_shoots_in)
            setFieldTitle(title)
            setDropDownDisabled()
            setInputTextType(InputType.TYPE_CLASS_NUMBER)

            if (isEdit) {
                setInputText(match?.estatisticas?.chutesAoGol.toString())
            }
        }
    }

    private fun setupShootsOut() {
        binding.shootsInOut.apply {
            val title = requireContext().getString(R.string.num_shoots_out)
            setAlternativeFieldTitle(title)
            setAlternativeDropDownDisabled()
            setAlternativeInputTextType(InputType.TYPE_CLASS_NUMBER)
            if (isEdit) {
                setAlternativeInputText(match?.estatisticas?.chutesForaDoGol.toString())
            }
        }
    }

    private fun setupGoalsScoredAndSuffered() {
        setupGoalsScored()
        setupGoalsSuffered()
    }

    private fun setupGoalsScored() {
        binding.goalsScoredAndSuffered.apply {
            val title = requireContext().getString(R.string.num_goals_Scored)
            setFieldTitle(title)
            setDropDownDisabled()
            setInputTextType(InputType.TYPE_CLASS_NUMBER)

            if (isEdit) {
                setInputText(match?.estatisticas?.golsMarcados.toString())
            }
        }
    }

    private fun setupGoalsSuffered() {
        binding.goalsScoredAndSuffered.apply {
            val title = requireContext().getString(R.string.num_goals_suffered)
            setAlternativeFieldTitle(title)
            setAlternativeDropDownDisabled()
            setAlternativeInputTextType(InputType.TYPE_CLASS_NUMBER)
            if (isEdit) {
                setAlternativeInputText(match?.estatisticas?.golsSofridos.toString())
            }
        }
    }

    private fun setupBallControlAndDate() {
        setupBallControl()
        setupDate()
    }

    private fun setupBallControl() {
        binding.ballControlAndDate.apply {
            val title = requireContext().getString(R.string.ball_control)
            setFieldTitle(title)
            setDropDownDisabled()
            setInputTextType(InputType.TYPE_CLASS_NUMBER)

            if (isEdit) {
                setInputText(match?.estatisticas?.posseDeBola.toString())
            }
        }
    }

    private fun setupDate() {
        binding.ballControlAndDate.apply {
            val title = requireContext().getString(R.string.date)
            setAlternativeFieldTitle(title)
            setAlternativeDropDownDisabled()
            onAlternativeClicked {
                showDatePickerDialog(requireContext(), null) { selectedDate ->
                    setAlternativeInputText(selectedDate)
                }
            }

            if (isEdit) {
                setAlternativeInputText(match?.date.toString())
            }
        }
    }

    private fun smoothScrollRecycler() {
        try {
            binding.nestedContainer.smoothScrollTo(0, SCROLL_VALUE)

        } catch (e: Exception) {
            Log.d(TAG_ADD_MATCH, "smoothScrollRecycler: ${e.localizedMessage}")
        }
    }

    private fun smoothScrollRecyclerBack() {
        try {
            binding.nestedContainer.smoothScrollTo(0, 0)

        } catch (e: Exception) {
            Log.d(TAG_ADD_MATCH, "smoothScrollRecycler: ${e.localizedMessage}")
        }
    }

    private fun executeSmoothScroll() {
        Handler(Looper.getMainLooper()).postDelayed(
            this::smoothScrollRecycler, EXECUTION_DELAY
        )
        Handler(Looper.getMainLooper()).postDelayed(
            this::smoothScrollRecyclerBack, EXECUTION_DELAY_BACK
        )
    }

    companion object {
        private const val IS_EDIT = "editing team"
        private const val MATCH_TOKEN = "token"
        fun getEditInstance(isEdit: Boolean, matchToken: String): AddMatchDialogFragment {
            val bundle = Bundle()
            bundle.putBoolean(IS_EDIT, isEdit)
            bundle.putString(MATCH_TOKEN, matchToken)
            val fragment = AddMatchDialogFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

}