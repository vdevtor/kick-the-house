package com.vitorthemyth.kickthehouse.presentation.add_team

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.vitorthemyth.components.databinding.ItemMatchAddBinding
import com.vitorthemyth.domain.models.Partida
import com.vitorthemyth.domain.models.Time
import com.vitorthemyth.kickthehouse.R
import com.vitorthemyth.kickthehouse.databinding.FragmentAddNewTeamBinding
import com.vitorthemyth.kickthehouse.helper.adapter.BaseAdapter
import com.vitorthemyth.kickthehouse.helper.dialog.DialogInteractionListener
import com.vitorthemyth.kickthehouse.helper.dialog.showDialog
import com.vitorthemyth.kickthehouse.helper.strings_numers.Empty
import com.vitorthemyth.kickthehouse.presentation.add_team.dialog.AddMatchDialogFragment
import com.vitorthemyth.kickthehouse.presentation.probabilities.ProbabilityViewModel
import com.vitorthemyth.kickthehouse.presentation.team_list.TeamListViewModel
import kotlinx.coroutines.launch

class AddNewTeamFragment : Fragment() {

    private lateinit var binding: FragmentAddNewTeamBinding

    private val viewModel: AddTeamViewModel by activityViewModels()
    private val probabilityViewModel: ProbabilityViewModel by activityViewModels()
    private val teamListViewModel: TeamListViewModel by activityViewModels()

    private val addMatchDialogTag = "add match tag"

    private var isKeyboardVisible = false

    //Initiate the adapter with type
    private var mAdapter = BaseAdapter<Partida>()

    //Sample data
    private var matchesList = mutableListOf<Partida>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddNewTeamBinding.inflate(inflater, container, false)

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
        if (!isVisible) binding.teamName.clearFieldsFocus()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTeamNameLabel()
        initViews()
        listenToEvents()
    }

    private fun setupTeamNameLabel() {
        binding.teamName.apply {
            val title = requireContext().getString(R.string.team_name)
            setFieldTitle(title)
            setDropDownDisabled()
        }
    }

    private fun initViews() {
        binding.btnAddItem.setOnClickListener {

            val size = viewModel.getTeamModel()?.ultimasPartidas?.size ?: Int.Empty
            if (size >= 5) {
                showDialog(
                    requireContext(),
                    requireContext().getString(R.string.txt_attention),
                    message = requireContext().getString(R.string.matches_limit),
                    onNegativeClick = {},
                    onPositiveClick = {}
                )
                return@setOnClickListener
            }
            if (binding.teamName.restoreInputText().isEmpty()) {
                showDialog(
                    requireContext(),
                    requireContext().getString(R.string.txt_attention),
                    message = requireContext().getString(R.string.main_team_name),
                    onNegativeClick = {},
                    onPositiveClick = {}
                )
            } else {
                viewModel.getTeamModel()?.nome = binding.teamName.restoreInputText()
                openAddMatchDialog()
            }
        }
        binding.btnSaveTime.setOnClickListener {
            viewModel.saveTime()
        }
    }

    private fun listenToEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.teamSavedEvent.collect { result ->
                if (result == null) return@collect

                if (result) {
                    restoreInitialState()
                    probabilityViewModel.fetchTeamList()
                    teamListViewModel.fetchTeamList()
                    view?.let {
                        Snackbar.make(
                            it, requireContext()
                                .getString(R.string.successfully_save), Snackbar.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    view?.let {
                        Snackbar.make(
                            it, requireContext()
                                .getString(R.string.failed_save), Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun restoreInitialState() {
        viewModel.setTeam(
            Time(
                nome = String.Empty,
                ultimasPartidas = mutableListOf(),
                proximaPartida = null

            )
        )
        matchesList.clear()
        mAdapter.clearList()
        binding.teamName.setInputText(String.Empty)
        binding.group1.visibility = View.GONE
    }

    private fun openAddMatchDialog() {
        val dialog = AddMatchDialogFragment()
        dialog.setInteractionListener(object : DialogInteractionListener() {
            override fun onPositiveCallback(tag: String?, data: Any?) {
                dialog.dismiss()
                if ((data is Time)) {
                    populateRecycler()
                }
            }
        })

        if (isAdded) dialog.show(parentFragmentManager, addMatchDialogTag)
    }

    private fun openAddMatchDialogEdition(match: Partida) {
        val dialog = AddMatchDialogFragment.getEditInstance(
            isEdit = true,
            matchToken = match.matchToken
        )
        dialog.setInteractionListener(object : DialogInteractionListener() {
            override fun onPositiveCallback(tag: String?, data: Any?) {
                dialog.dismiss()
                if ((data is Time)) {
                    populateRecycler()

                }
            }
        })

        if (isAdded) dialog.show(parentFragmentManager, addMatchDialogTag)
    }

    private fun populateRecycler() {
        matchesList = viewModel.getTeamModel()?.ultimasPartidas ?: mutableListOf()
        mAdapter.listOfItems = matchesList

        if (matchesList.isNotEmpty()) enableSaveButton()
        else disableSaveButton()

        mAdapter.expressionViewHolderBinding = { eachItem, viewBinding ->

            val view = viewBinding as ItemMatchAddBinding

            if (eachItem.estatisticas.jogouEmCasa) {

                view.tvHomeTeam.text = viewModel.getTeamModel()?.nome
                view.tvAwayTeam.text = eachItem.adversario?.nome
            } else {
                view.tvHomeTeam.text = eachItem.adversario?.nome
                view.tvAwayTeam.text = viewModel.getTeamModel()?.nome
            }

            view.matchDate.text = eachItem.date

            view.editMatch.setOnClickListener {
                openAddMatchDialogEdition(eachItem)
            }
        }

        mAdapter.expressionOnCreateViewHolder = { viewGroup ->
            //Inflate the layout and send it to the adapter
            ItemMatchAddBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        }

        //finally put the adapter to recyclerview
        binding.rvMatches.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }

    private fun enableSaveButton() {
        binding.group1.visibility = View.VISIBLE
    }

    private fun disableSaveButton() {
        binding.group1.visibility = View.GONE
    }

    companion object {
        internal const val TAG_ADD_MATCH = "addMatchFragment"
    }
}