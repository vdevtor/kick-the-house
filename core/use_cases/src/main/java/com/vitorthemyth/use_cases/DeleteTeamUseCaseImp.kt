package com.vitorthemyth.use_cases

import com.vitorthemyth.domain.models.Time
import com.vitorthemyth.domain.repository.TeamRepository

class DeleteTeamUseCaseImp(private val repository: TeamRepository) : com.vitorthemyth.use_cases.interfaces.DeleteTeamUseCase {
    override suspend fun execute(team: Time) {
        return repository.deleteTeam(team =team )
    }
}