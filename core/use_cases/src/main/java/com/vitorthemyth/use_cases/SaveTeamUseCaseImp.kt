package com.vitorthemyth.use_cases

import com.vitorthemyth.domain.models.Time
import com.vitorthemyth.domain.repository.TeamRepository
import com.vitorthemyth.use_cases.interfaces.SaveTeamUseCase

class SaveTeamUseCaseImp(private val repository: TeamRepository) : SaveTeamUseCase {
    override suspend fun execute(team: Time) {
        return repository.saveTeam(team =team )
    }
}