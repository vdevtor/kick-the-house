package com.vitorthemyth.persistence

import com.vitorthemyth.domain.models.Equipe
import com.vitorthemyth.domain.models.Estatisticas
import com.vitorthemyth.domain.models.Partida
import com.vitorthemyth.domain.models.Time
import com.vitorthemyth.persistence.models.EquipeEntity
import com.vitorthemyth.persistence.models.EstatisticasEntity
import com.vitorthemyth.persistence.models.PartidaEntity
import com.vitorthemyth.persistence.models.TimeEntity

fun Time.toTeamEntity() : TimeEntity {
    return TimeEntity(
        id= id ?: 0,
        nome =  nome,
        ultimasPartidas = ultimasPartidas.map { it.toPartidaEntity() },
        proximaPartida= proximaPartida?.toPartidaEntity(),
    )
}

fun Partida.toPartidaEntity() = PartidaEntity(
    matchToken,
    adversario = adversario?.toEquipeEntity(),
    dificuldade,
    importancia,
    estatisticas.toEstatisticasEntity(),
    date = date
)

fun Equipe.toEquipeEntity() = EquipeEntity(

    nome,
    ultimasPartidas = ultimasPartidas?.map { it.toEstatisticasEntity() }
)

fun Estatisticas.toEstatisticasEntity() = EstatisticasEntity(
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
    resultado
)

fun List<Time>.toListTimeEntity() =
    this.map { it.toTeamEntity() }