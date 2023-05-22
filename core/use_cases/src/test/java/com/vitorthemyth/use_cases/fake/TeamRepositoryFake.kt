package com.vitorthemyth.use_cases.fake

import com.vitorthemyth.domain.models.Time
import com.vitorthemyth.domain.repository.TeamRepository

class FakeTeamRepository : TeamRepository {
    private val teams = mutableListOf<Time>()

    override suspend fun getTeams(): List<Time> {
        return teams.toList()
    }

    override suspend fun getTeamById(teamId: Int): Time? {
        return teams.find { it.id == teamId }
    }

    override suspend fun saveTeam(team: Time) {
        if (teams.size >= 5) {
            throw Exception("Não é possível salvar mais de 5 partidas")
        }
        teams.add(team)
    }

    override suspend fun updateTeam(team: Time) {
        val index = teams.indexOfFirst { it.id == team.id }
        if (index == -1) {
            throw Exception("Partida não encontrada")
        }
        teams[index] = team
    }

    override suspend fun deleteTeam(team: Time) {
        teams.removeIf { it.id == team.id }
    }

    override suspend fun deleteAll() {
        teams.clear()
    }
}