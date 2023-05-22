package com.vitorthemyth.domain.repository

import com.vitorthemyth.domain.models.Time

interface ProbabilitiesRepository {

 suspend fun calculateEV(
     probability: Double,
     odds: Double,
     betValue: Double,
     loseProbability: Double
    ): Double

  suspend  fun compareProbabilities(
        time1: Time,
        time2: Time
    ): Pair<Triple<Double, Double, Double>, Triple<Double, Double, Double>>
}