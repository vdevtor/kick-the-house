package com.vitorthemyth.use_cases

import android.util.Log
import com.vitorthemyth.domain.repository.ProbabilitiesRepository
import com.vitorthemyth.use_cases.interfaces.CalculateEV
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.ParseException
import java.util.*

class CalculateEvUseCaseImp(val repository: ProbabilitiesRepository) : CalculateEV {
    override suspend fun execute(
        probability: Double,
        odds: String,
        betValue: String,
        loseProbability: Double
    ): Double {

        return try {

            val numberFormat = NumberFormat.getInstance(Locale.getDefault())



            val decimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale.getDefault())
            decimalFormatSymbols.groupingSeparator = '.' // Set the grouping separator to '.'

            val decimalFormat = DecimalFormat("#,##0.00", decimalFormatSymbols)
            val doubleBetValue: Double = try {
                decimalFormat.parse(betValue)?.toDouble() ?: 0.0
            } catch (e: ParseException) {
                0.0
            }


            val doubleOdd: Double = try {
                numberFormat.parse(odds.replace(",","."))?.toDouble()?.div(100) ?: 0.0
            } catch (e: ParseException) {
                0.0
            }

            val formattedProbability = numberFormat.parse(String.format("%.2f", probability))?.toDouble() ?: 0.0

            val formattedLoseProbability = numberFormat.parse(String.format("%.2f", loseProbability))?.toDouble()?: 0.0

            repository.calculateEV(formattedProbability,doubleOdd, doubleBetValue,formattedLoseProbability)

        }catch (e:Exception){
            Log.d(TAG, "execute:${e.localizedMessage} ")
            0.0
        }
    }

    companion object{
        private const val TAG = "calculateEV Use Case"
    }

}
