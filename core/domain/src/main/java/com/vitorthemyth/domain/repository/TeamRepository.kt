package com.vitorthemyth.domain.repository

import com.vitorthemyth.domain.models.Time

interface TeamRepository {

    suspend fun getTeams(): List<Time>
    suspend fun getTeamById(teamId: Int): Time?
    suspend fun saveTeam(team: Time)
    suspend fun updateTeam(team: Time)
    suspend fun deleteTeam(team: Time)
    suspend fun deleteAll()
}