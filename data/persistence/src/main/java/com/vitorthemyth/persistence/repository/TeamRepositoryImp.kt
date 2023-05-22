package com.vitorthemyth.persistence.repository

import android.util.Log
import com.vitorthemyth.domain.models.Time
import com.vitorthemyth.domain.repository.TeamRepository
import com.vitorthemyth.persistence.TeamDao
import com.vitorthemyth.persistence.toListTime
import com.vitorthemyth.persistence.toTeam
import com.vitorthemyth.persistence.toTeamEntity

class TeamRepositoryImpl(private val teamDao: TeamDao) : TeamRepository {

    override suspend fun  getTeams(): List<Time> {
        return teamDao.getAllTimes().toListTime()
    }

    override suspend fun getTeamById(teamId: Int): Time? {
        return teamDao.getTimeById(teamId)?.toTeam()
    }

    override suspend fun saveTeam(team: Time) {
        try {
            val teams = teamDao.getAllTimes()
            if (teams.size >= 5) {
                throw Exception("Não é possível salvar mais de 5 partidas")
            }
            teamDao.insertTime(team.toTeamEntity())
        }catch (e:Exception){
            Log.d("babi", "saveTeam:  ${e.localizedMessage}")
        }

    }

    override suspend fun updateTeam(team: Time) {
        val time = teamDao.getAllTimes()
        if (time.size >= 5 && time.none { it.id == team.id }) {
            throw Exception("Não é possível atualizar a partida porque já há 5 partidas salvas")
        }
        teamDao.updateTime(team.toTeamEntity())
    }

    override suspend fun deleteTeam(team: Time) {
        teamDao.deleteTime(team.toTeamEntity())
    }

    override suspend fun deleteAll() {
        teamDao.deleteAllTimes()
    }
}
