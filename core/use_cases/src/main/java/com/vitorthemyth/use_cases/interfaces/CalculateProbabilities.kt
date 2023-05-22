package com.vitorthemyth.use_cases.interfaces

import com.vitorthemyth.domain.models.Time

interface CalculateProbabilities {

    suspend fun execute(
        time1: Time,
        time2: Time
    ): Pair<Triple<Double, Double, Double>, Triple<Double, Double, Double>>
}