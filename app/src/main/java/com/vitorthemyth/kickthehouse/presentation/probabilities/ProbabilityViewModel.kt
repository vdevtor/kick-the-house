package com.vitorthemyth.kickthehouse.presentation.probabilities

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitorthemyth.domain.models.CalcResults
import com.vitorthemyth.domain.models.Time
import com.vitorthemyth.kickthehouse.R
import com.vitorthemyth.kickthehouse.helper.strings_numers.Empty
import com.vitorthemyth.use_cases.TeamUseCases
import com.vitorthemyth.use_cases.interfaces.CalculateEV
import com.vitorthemyth.use_cases.interfaces.CalculateProbabilities
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProbabilityViewModel @Inject constructor(
    private val getAllTeamsUseCase: TeamUseCases,
    private val calculateEV: CalculateEV,
    private val calculateProbabilities: CalculateProbabilities
) : ViewModel() {

    private val TAG = "probViewModel"

    private val _teamList = MutableStateFlow<List<Time>?>(null)
    val teamList: StateFlow<List<Time>?>
        get() = _teamList

    private val _results = MutableStateFlow<CalcResults?>(null)
    val results: StateFlow<CalcResults?>
        get() = _results

    private val _resultsManually = MutableStateFlow<CalcResults?>(null)
    val resultsManually: StateFlow<CalcResults?>
        get() = _resultsManually


    private val _errorMessage = MutableStateFlow<Int?>(null)
    val errorMessage: StateFlow<Int?>
        get() = _errorMessage

    fun fetchTeamList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val teams = getAllTeamsUseCase.getTeamListUseCase.execute()
                _teamList.emit(teams)
            } catch (e: Exception) {
                // handle exception
                _teamList.emit(emptyList())
            }
        }
    }

    fun calculateManualProbabilities(
        winProb: String,
        lossProb: String,
        odd: String,
        betValue: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {

            //kind of cleaning cache so the result can be emit again
            _resultsManually.emit(null)

            val doubleProbWin = (winProb.toDouble() / 100)
            val doubleProbLoss = (lossProb.toDouble() / 100)

            val result = calculateEV.execute(
                probability = doubleProbWin,
                odds = odd.trim(),
                betValue = betValue,
                loseProbability = doubleProbLoss
            )

            delay(500)

            _resultsManually.emit(
                CalcResults(
                    probVitoria = doubleProbWin,
                    probDerrota = doubleProbLoss,
                    ev = result
                )
            )

        } catch (e: Exception) {
            _errorMessage.emit(R.string.error_generic)
            Log.d(TAG, "calculateProbabilities Manually:${e.localizedMessage} ")
        }
    }

    fun calculateProbabilities(team1: Time, team2: Time, value: String, odd: String) =
        viewModelScope.launch(Dispatchers.IO) {
            try {

                //kind of cleaning cache so the result can be emit again

                _results.emit(
                    CalcResults(
                        probVitoria = Double.Empty,
                        probDerrota = Double.Empty,
                        ev = Double.Empty
                    )
                )
                calculateProbabilities.execute(team1, team2).also { probs ->
                    val result = calculateEV.execute(
                        probability = probs.first.first,
                        odds = odd.trim(),
                        betValue = value,
                        loseProbability = probs.first.third
                    )

                    _results.emit(
                        CalcResults(
                            probVitoria = probs.first.first,
                            probDerrota = probs.first.third,
                            ev = result
                        )
                    )
                }

            } catch (e: Exception) {
                _errorMessage.emit(R.string.error_generic)
                Log.d(TAG, "calculateProbabilities:${e.localizedMessage} ")
            }
        }
}