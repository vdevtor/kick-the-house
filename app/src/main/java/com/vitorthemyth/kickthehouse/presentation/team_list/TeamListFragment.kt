package com.vitorthemyth.kickthehouse.presentation.team_list

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.vitorthemyth.components.databinding.ItemMatchListBinding
import com.vitorthemyth.components.databinding.ItemTeamEditBinding
import com.vitorthemyth.domain.models.Partida
import com.vitorthemyth.domain.models.Time
import com.vitorthemyth.kickthehouse.R
import com.vitorthemyth.kickthehouse.databinding.FragmentTeamListBinding
import com.vitorthemyth.kickthehouse.helper.adapter.BaseAdapter
import com.vitorthemyth.kickthehouse.helper.dialog.DialogInteractionListener
import com.vitorthemyth.kickthehouse.helper.strings_numers.Empty
import com.vitorthemyth.kickthehouse.presentation.add_team.AddTeamViewModel
import com.vitorthemyth.kickthehouse.presentation.add_team.dialog.AddMatchDialogFragment
import com.vitorthemyth.kickthehouse.presentation.probabilities.ProbabilityViewModel
import kotlinx.coroutines.launch

class TeamListFragment : Fragment() {

    private lateinit var binding: FragmentTeamListBinding

    private val viewModel: TeamListViewModel by activityViewModels()
    private val probabilityVm: ProbabilityViewModel by activityViewModels()
    private val addTeamViewModel: AddTeamViewModel by activityViewModels()


    //Initiate the adapter with type
    private var mAdapter = BaseAdapter<Time>()

    private var teams: List<Time> = listOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeamListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchTeams()
        observeTeamList()

    }

    private fun observeTeamList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.teamList.collect { teamList ->
                // update UI with the new teamList
                teamList?.let {
                    teams = it
                    populateRecycler()

                }
            }
        }
    }

    private fun fetchTeams() {
        viewModel.fetchTeamList()
    }

    private fun populateRecycler() {
        mAdapter.clearList()
        mAdapter.listOfItems = teams.toMutableList()


        mAdapter.expressionViewHolderBinding = { eachItem, viewBinding ->

            val view = viewBinding as ItemTeamEditBinding

            setupItemLabels(view, eachItem)

            populateTeamMatches(eachItem, view.rvMatches, view) {
                initRecyclerViews(view, eachItem, it)
            }


        }

        mAdapter.expressionOnCreateViewHolder = { viewGroup ->
            //Inflate the layout and send it to the adapter
            ItemTeamEditBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        }

        //finally put the adapter to recyclerview
        binding.rvTeams.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }

    private fun initRecyclerViews(
        view: ItemTeamEditBinding,
        eachItem: Time,
        baseAdapter: BaseAdapter<Partida>
    ) {
        view.ivExpand.setOnClickListener {
            if (view.group1.isVisible) {
                view.group1.visibility = View.GONE
            } else {
                view.group1.visibility = View.VISIBLE
            }
        }

        view.deleteTeam.setOnClickListener {
            viewModel.deleteTeam(eachItem)
            notifyProbabilitiesFragment()
        }

        view.addMatch.setOnClickListener {
            if (eachItem.ultimasPartidas.size >= 5) {
                Snackbar.make(
                    it, requireContext()
                        .getString(R.string.matches_limit), Snackbar.LENGTH_SHORT
                ).show()
            } else {
                addTeamViewModel.setTeam(
                    Time(
                        nome = String.Empty,
                        ultimasPartidas = mutableListOf(),
                        proximaPartida = null
                    )
                )
                openAddMatchDialog(baseAdapter, eachItem, mainView = view)
            }
        }
    }

    private fun setupItemLabels(
        view: ItemTeamEditBinding,
        eachItem: Time
    ) {
        //name
        view.teamName.text = eachItem.nome

        //matches num
        view.tvMatchesNum.text = eachItem.ultimasPartidas.size.toString()

        //add and delete team/match
        view.deleteTeam.text = getString(R.string.delete_team)
        view.addMatch.text = getString(R.string.add_match)

        //lastUpdate
        val formattedText =
            getString(
                R.string.last_update,
                eachItem.ultimasPartidas.lastOrNull()?.date ?: String.Empty
            )
        val styledText = HtmlCompat.fromHtml(formattedText, HtmlCompat.FROM_HTML_MODE_LEGACY)

        view.tvLastUpdate.text = styledText
    }

    private fun populateTeamMatches(
        team: Time,
        recyclerView: RecyclerView,
        mainView: ItemTeamEditBinding,
        onAddClick: (BaseAdapter<Partida>) -> Unit
    ) {
        val list = team.ultimasPartidas
        val mAdapterMatches = BaseAdapter<Partida>()

        mAdapterMatches.listOfItems = list


        mAdapterMatches.expressionViewHolderBinding = { eachItem, viewBinding ->

            val view = viewBinding as ItemMatchListBinding

            setupInnerRecyclerLabels(view, eachItem)

            initInnerRecyclerViews(view, team, eachItem, mAdapterMatches, mainView)
        }

        mAdapterMatches.expressionOnCreateViewHolder = { viewGroup ->
            //Inflate the layout and send it to the adapter
            ItemMatchListBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        }

        //finally put the adapter to recyclerview
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapterMatches
            onAddClick(mAdapterMatches)
            requestLayout()
        }
    }

    private fun initInnerRecyclerViews(
        view: ItemMatchListBinding,
        team: Time,
        eachItem: Partida,
        mAdapterMatches: BaseAdapter<Partida>,
        mainView: ItemTeamEditBinding
    ) {
        view.editMatch.setOnClickListener {
            addTeamViewModel.setTeam(team)
            openAddMatchDialogEdition(mAdapterMatches, eachItem, mainView)
        }

        view.deleteMatch.setOnClickListener {
            team.ultimasPartidas.remove(eachItem)

            mAdapterMatches.listOfItems = team.ultimasPartidas

            viewModel.updateTeam(team)
            notifyProbabilitiesFragment()

            mainView.tvMatchesNum.text = team.ultimasPartidas.size.toString()

        }
    }

    private fun setupInnerRecyclerLabels(
        view: ItemMatchListBinding,
        eachItem: Partida,
    ) {
        view.tvAwayTeam.text = eachItem.adversario?.nome
        view.matchDate.text = eachItem.date
    }


    private fun openAddMatchDialog(
        baseAdapter: BaseAdapter<Partida>,
        teamToBeUpdated: Time,
        mainView: ItemTeamEditBinding
    ) {
        val dialog = AddMatchDialogFragment()
        dialog.setInteractionListener(object : DialogInteractionListener() {
            override fun onPositiveCallback(tag: String?, data: Any?) {
                dialog.dismiss()
                if ((data is Time)) {
                    teamToBeUpdated.ultimasPartidas.add(data.ultimasPartidas.first())
                    baseAdapter.addItem(teamToBeUpdated.ultimasPartidas)
                    viewModel.updateTeam(teamToBeUpdated)
                    notifyProbabilitiesFragment()
                    mainView.tvMatchesNum.text = teamToBeUpdated.ultimasPartidas.size.toString()
                }
            }
        })

        if (isAdded) dialog.show(parentFragmentManager, TAG_ADD_MATCH)
    }


    private fun openAddMatchDialogEdition(
        baseAdapter: BaseAdapter<Partida>,
        match: Partida,
        mainView: ItemTeamEditBinding
    ) {
        val dialog = AddMatchDialogFragment.getEditInstance(
            isEdit = true,
            matchToken = match.matchToken
        )
        dialog.setInteractionListener(object : DialogInteractionListener() {
            override fun onPositiveCallback(tag: String?, data: Any?) {
                dialog.dismiss()
                if ((data is Time)) {
                    val index =
                        baseAdapter.listOfItems?.indexOfFirst { it.matchToken == match.matchToken }
                            ?: Int.Empty
                    data.ultimasPartidas.find { it.matchToken == match.matchToken }?.let {
                        baseAdapter.listOfItems?.set(index, it)
                        baseAdapter.addItem(data.ultimasPartidas)
                    }

                    mainView.tvMatchesNum.text = data.ultimasPartidas.size.toString()
                    viewModel.updateTeam(data)
                    notifyProbabilitiesFragment()
                }
            }
        })

        if (isAdded) dialog.show(parentFragmentManager, TAG_EDIT_MATCH)
    }

    private fun notifyProbabilitiesFragment() {
        Handler(Looper.getMainLooper()).postDelayed({
            probabilityVm.fetchTeamList()
        }, 1500)
    }

    companion object {
        const val TAG_ADD_MATCH = "add match"
        const val TAG_EDIT_MATCH = "edit match"
    }
}