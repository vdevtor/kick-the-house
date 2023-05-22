package com.vitorthemyth.use_cases

import com.vitorthemyth.domain.models.Time
import com.vitorthemyth.domain.repository.ProbabilitiesRepository
import com.vitorthemyth.use_cases.interfaces.CalculateProbabilities

class CalculateProbabilitiesUseCaseImp(val repository: ProbabilitiesRepository) : CalculateProbabilities {
    override suspend fun execute(
        time1: Time,
        time2: Time
    ): Pair<Triple<Double, Double, Double>, Triple<Double, Double, Double>> {
        return repository.compareProbabilities(time1, time2)
    }
}