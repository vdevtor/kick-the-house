package com.vitorthemyth.persistence.mock

import com.vitorthemyth.persistence.models.EquipeEntity
import com.vitorthemyth.persistence.models.PartidaEntity
import com.vitorthemyth.persistence.models.EstatisticasEntity
import com.vitorthemyth.persistence.models.TimeEntity
import java.util.*

fun returnTime() = TimeEntity(
    time = "Corinthians",
    partidas = returnListOfPartidas()
)

fun returnListOfPartidas() = mutableListOf<PartidaEntity>(
    PartidaEntity(
        matchToken = UUID.randomUUID().toString(),
        equipeCasa = EquipeEntity(
            id = (0..999).random(),
            nome = "Corinthians",
            ultimasPartidas =  returnListOfResultadosA()
        ),
        equipeVisitante = EquipeEntity(
            id = (0..999).random(),
            nome = "São Paulo",
            ultimasPartidas =  returnListOfResultadosB()
        ),
        dificuldade = 2,
        importancia = 3
    )
)

fun returnListOfResultadosA() = mutableListOf<EstatisticasEntity>(
    EstatisticasEntity(
        id = (0..999).random(),
        golsMarcados = 2,
        golsSofridos = 2,
        posseDeBola = 52.0,
        chutesForaDoGol = 11,
        chutesAoGol = 3,
        escanteios = 7,
        faltas = 14,
        cartoesAmarelos = 2,
        cartoesVermelhos = 0,
        jogouEmCasa = true
    )
)

fun returnListOfResultadosB() = mutableListOf<EstatisticasEntity>(
    EstatisticasEntity(
        id = (0..999).random(),
        golsMarcados = 1,
        golsSofridos = 0,
        posseDeBola = 47.0,
        chutesForaDoGol = 7,
        chutesAoGol = 2,
        escanteios = 5,
        faltas = 6,
        cartoesAmarelos = 1,
        cartoesVermelhos = 0,
        jogouEmCasa = false
    )
)