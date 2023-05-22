package com.vitorthemyth.use_cases.interfaces

interface CalculateEV {
    suspend  fun execute(
        probability: Double,
        odds: String,
        betValue: String,
        loseProbability: Double
    ): Double

}