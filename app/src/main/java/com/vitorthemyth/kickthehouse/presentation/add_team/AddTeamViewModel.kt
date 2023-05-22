package com.vitorthemyth.kickthehouse.presentation.add_team

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitorthemyth.domain.models.Time
import com.vitorthemyth.kickthehouse.helper.strings_numers.Empty
import com.vitorthemyth.kickthehouse.presentation.add_team.AddNewTeamFragment.Companion.TAG_ADD_MATCH
import com.vitorthemyth.use_cases.TeamUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTeamViewModel @Inject constructor(
    private val getAllTeamsUseCase: TeamUseCases
) : ViewModel() {

    private val _teamSavedEvent = MutableStateFlow<Boolean?>(null)
    val teamSavedEvent: StateFlow<Boolean?>
        get() = _teamSavedEvent

    private var team: Time? = Time(
        nome = String.Empty,
        ultimasPartidas = mutableListOf(),
        proximaPartida = null
    )

    fun getTeamModel() = team

    fun setTeam(newTeam: Time?) {
        team = newTeam
    }

    fun saveTime() = viewModelScope.launch(Dispatchers.IO) {

        try {
            team?.let {
                getAllTeamsUseCase.saveTeamUseCase.execute(it)
                _teamSavedEvent.emit(true)
            }

        } catch (e: Exception) {
            Log.d(TAG_ADD_MATCH, "saveTime ${e.localizedMessage}: ")
            _teamSavedEvent.emit(false)
        }
    }
}
