package com.vitorthemyth.persistence.repository

import com.vitorthemyth.domain.models.*
import com.vitorthemyth.domain.repository.ProbabilitiesRepository
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class ProbabilityRepositoryImp : ProbabilitiesRepository {
    override suspend fun calculateEV(
        probability: Double,
        odds: Double,
        betValue: Double,
        loseProbability: Double
    ): Double {

        val outcomes = betValue * odds

        return (probability * (outcomes - betValue)) - (loseProbability * betValue)

    }

    override suspend fun compareProbabilities(
        time1: Time,
        time2: Time
    ): Pair<Triple<Double, Double, Double>, Triple<Double, Double, Double>> {

        // Obter as últimas partidas de cada equipe

        val lastMatchesTeam1 = time1.ultimasPartidas
        val lastMatchesTeam2 = time2.ultimasPartidas

        // Calcular as probabilidades de vitória, empate e derrota para cada partida
        val probabilitiesTeam1 = lastMatchesTeam1.map { calculateProbabilities(time1, time2) }
        val probabilitiesTeam2 = lastMatchesTeam2.map { calculateProbabilities(time2, time1) }

        // Calcular a média das probabilidades de vitória, empate e derrota para cada equipe
        val averageTeam1 = calculateAverageProbabilities(probabilitiesTeam1)
        val averageTeam2 = calculateAverageProbabilities(probabilitiesTeam2)



        return Pair(averageTeam1, averageTeam2)
    }

    private fun calculateAverageProbabilities(probabilidades: List<Probabilities>): Triple<Double, Double, Double> {
        val n = min(probabilidades.size, 5) // número de partidas a considerar
        val probabilitiesSum = probabilidades.takeLast(n)
            .fold(Triple(0.0, 0.0, 0.0)) { acc, (probVitoria, probEmpate, probDerrota) ->
                Triple(acc.first + probVitoria, acc.second + probEmpate, acc.third + probDerrota)
            }
        return Triple(
            abs(probabilitiesSum.first / n),
            probabilitiesSum.second / n,
            probabilitiesSum.third / n
        )
    }

    private fun calcularMediaEstatisticas(partidas: List<Partida>): Estatisticas {
        val n = min(partidas.size, 5) // número de partidas a considerar
        val statisticsSum = partidas.takeLast(n).fold(
            Estatisticas(
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                false,
                Resultado.empate
            )
        ) { acc, partida ->
            Estatisticas(
                acc.golsMarcados + partida.estatisticas.golsMarcados,
                acc.golsSofridos + partida.estatisticas.golsSofridos,
                acc.posseDeBola + partida.estatisticas.posseDeBola,
                acc.chutesAoGol + partida.estatisticas.chutesAoGol,
                acc.chutesForaDoGol + partida.estatisticas.chutesForaDoGol,
                acc.escanteios + partida.estatisticas.escanteios,
                acc.faltas + partida.estatisticas.faltas,
                acc.cartoesAmarelos + partida.estatisticas.cartoesAmarelos,
                acc.cartoesVermelhos + partida.estatisticas.cartoesVermelhos,
                false,
                acc.resultado,
                storedResultInPoints = acc.resultadoInPoints + partida.estatisticas.resultadoInPoints
            )
        }
        return Estatisticas(
            statisticsSum.golsMarcados / n,
            statisticsSum.golsSofridos / n,
            statisticsSum.posseDeBola / n,
            statisticsSum.chutesAoGol / n,
            statisticsSum.chutesForaDoGol / n,
            statisticsSum.escanteios / n,
            statisticsSum.faltas / n,
            statisticsSum.cartoesAmarelos / n,
            statisticsSum.cartoesVermelhos / n,
            false,
            Resultado.empate,
            storedResultInPoints = statisticsSum.storedResultInPoints / n
        )
    }


    private fun calculateProbabilities(timeA: Time, timeB: Time): Probabilities {
        val lastMatchesTeamA = timeA.ultimasPartidas.takeLast(5)
        val lastMatchesTeamB = timeB.ultimasPartidas.takeLast(5)
        val averageStatisticTeamA = calcularMediaEstatisticas(lastMatchesTeamA)
        val averageStatisticTeamB = calcularMediaEstatisticas(lastMatchesTeamB)

        // Goal-adjusted scores with shots and corners included
        val scoredGoalsTeamA =
            averageStatisticTeamA.golsMarcados + (averageStatisticTeamA.chutesAoGol + averageStatisticTeamA.escanteios) / 10.0 + (averageStatisticTeamB.storedResultInPoints / 3.0) - 0.5
        val scoredGoalsTeamB =
            averageStatisticTeamB.golsSofridos + (averageStatisticTeamB.chutesAoGol + averageStatisticTeamB.escanteios) / 10.0 + (averageStatisticTeamA.storedResultInPoints / 3.0) - 0.5

        // Ensure that the goal-adjusted scores are non-negative
        val nonNegativeScoredGoalsTeamA = max(scoredGoalsTeamA, 0.0)
        val nonNegativeScoredGoalsTeamB = max(scoredGoalsTeamB, 0.0)

        // Calculate the probabilities
        val totalScoredGoals = nonNegativeScoredGoalsTeamA + nonNegativeScoredGoalsTeamB
        val winProbability = nonNegativeScoredGoalsTeamA / totalScoredGoals
        val loseProbability = nonNegativeScoredGoalsTeamB / totalScoredGoals
        val drawProbability = 1.0 - winProbability - loseProbability

        return Probabilities(winProbability, drawProbability, loseProbability)
    }



}