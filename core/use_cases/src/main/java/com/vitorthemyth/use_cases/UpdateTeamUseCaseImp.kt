package com.vitorthemyth.use_cases

import com.vitorthemyth.domain.models.Time
import com.vitorthemyth.domain.repository.TeamRepository
import com.vitorthemyth.use_cases.interfaces.UpdateTeam

class UpdateTeamUseCaseImp(private val repository: TeamRepository) : UpdateTeam {
    override suspend fun execute(team: Time) {
        return repository.updateTeam(team =team )
    }
}