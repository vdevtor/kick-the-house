package com.vitorthemyth.domain.models

import com.vitorthemyth.domain.R

data class Time(
    var id : Int? = null,
    var nome: String,
    var ultimasPartidas: MutableList<Partida>,
    var proximaPartida: Partida? = null
)


data class Partida(
    var matchToken: String,
    var adversario: Equipe?= null,
    var dificuldade: Int,
    var importancia: Int,
    var estatisticas: Estatisticas,
    var date : String
)


data class Equipe(
    var nome: String,
    var ultimasPartidas: List<Estatisticas>?
)

data class Estatisticas(
    var golsMarcados: Int,
    var golsSofridos: Int,
    var posseDeBola: Int,
    var chutesAoGol: Int,
    var chutesForaDoGol: Int,
    var escanteios: Int,
    var faltas: Int,
    var cartoesAmarelos: Int,
    var cartoesVermelhos: Int,
    var jogouEmCasa: Boolean,
    var resultado : Resultado,
    var storedResultInPoints: Double = 0.0
){
    val resultadoInPoints: Double
        get() = when (resultado) {
            Resultado.vitoria -> 3.0
            Resultado.empate -> 1.0
            Resultado.derrota -> 0.0
        }
}

enum class Resultado(val text:Int,val value:Int) {
    vitoria(R.string.result_win,6),empate(R.string.result_draw,3),derrota(R.string.result_lose,0);

    companion object {
        fun returnType(value: Int?): Resultado =
            Resultado.values().find { it.value == value } ?: empate
    }
}

data class Probabilities(val probVitoria: Double, val probEmpate: Double, val probDerrota: Double)
data class CalcResults(val probVitoria: Double,val probDerrota: Double,val ev : Double){
    val formattedWinProb = "%.2f".format(probVitoria * 100)
    val formattedLoseProb = "%.2f".format(probDerrota * 100)
    val formattedEv = "%.2f".format(ev)
}

