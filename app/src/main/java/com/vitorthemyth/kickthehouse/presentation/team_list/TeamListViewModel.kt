package com.vitorthemyth.kickthehouse.presentation.team_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitorthemyth.domain.models.Time
import com.vitorthemyth.use_cases.TeamUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamListViewModel @Inject constructor(
    private val getAllTeamsUseCase: TeamUseCases
) : ViewModel() {


    private val _teamList = MutableStateFlow<List<Time>?>(null)
    val teamList: StateFlow<List<Time>?>
        get() = _teamList


    fun fetchTeamList() {
        viewModelScope.launch(Dispatchers.IO) {
            _teamList.emit(null)
            try {
                val teams = getAllTeamsUseCase.getTeamListUseCase.execute()
                _teamList.emit(teams)
            } catch (e: Exception) {
                // handle exception
                _teamList.emit(emptyList())
            }
        }
    }

    fun deleteTeam(team:Time) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getAllTeamsUseCase.deleteTeamUseCase.execute(team)
                delay(DELAY)
                fetchTeamList()
            } catch (e: Exception) {
                // handle exception
                Log.d(TAG, "deleteTeam: ${e.localizedMessage}")
            }
        }
    }

    fun updateTeam(team: Time){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getAllTeamsUseCase.updateTeamUseCase.execute(team)

            } catch (e: Exception) {
                // handle exception
                Log.d(TAG, "deleteTeam: ${e.localizedMessage}")
            }
        }
    }

    companion object{
        private const val TAG = "team list VM"
        private const val DELAY = 50L
    }
}