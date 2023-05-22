package com.vitorthemyth.use_cases

import com.vitorthemyth.domain.models.Equipe
import com.vitorthemyth.domain.models.Partida
import com.vitorthemyth.domain.models.Estatisticas
import com.vitorthemyth.domain.models.Time
import java.util.UUID

fun returnTime() = Time(
    id = (0..10000).random(),
    time = "Corinthians",
    partidas = returnListOfPartidas()
)

fun returnListOfPartidas() = mutableListOf<Partida>(
    Partida(
        matchToken = UUID.randomUUID().toString(),
        equipeCasa = Equipe(
            id = (0..999).random(),
            nome = "Corinthians",
            ultimasPartidas =  returnListOfResultadosA()
        ),
        equipeVisitante = Equipe(
            id = (0..999).random(),
            nome = "São Paulo",
            ultimasPartidas =  returnListOfResultadosB()
        ),
        dificuldade = 2,
        importancia = 3
    )
)

fun returnListOfResultadosA() = mutableListOf<Estatisticas>(
    Estatisticas(
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

fun returnListOfResultadosB() = mutableListOf<Estatisticas>(
    Estatisticas(
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