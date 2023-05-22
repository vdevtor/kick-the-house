package com.vitorthemyth.persistence

import com.vitorthemyth.domain.models.*
import com.vitorthemyth.persistence.models.*

fun TimeEntity.toTeam() : Time{
    return Time(
        id,
        nome,
        ultimasPartidas = ultimasPartidas.map { it.toPartida() }.toMutableList(),
        proximaPartida = proximaPartida?.toPartida()
    )
}

fun PartidaEntity.toPartida() = Partida(
    matchToken,
    adversario = adversario?.toEquipe(),
    dificuldade,
    importancia,
    estatisticas = estatisticas.toEstatisticas(),
    date
)

fun EquipeEntity.toEquipe() = Equipe(
    nome,
    ultimasPartidas = ultimasPartidas?.map { it.toEstatisticas() }
)

fun EstatisticasEntity.toEstatisticas() = Estatisticas(
    golsMarcados,
    golsSofridos,
    posseDeBola,
    chutesAoGol,
    chutesForaDoGol,
    escanteios,
    faltas,
    cartoesAmarelos,
    cartoesVermelhos,
    jogouEmCasa,
    resultado = resultado
)



fun List<TimeEntity>.toListTime() =
    this.map { it.toTeam() }