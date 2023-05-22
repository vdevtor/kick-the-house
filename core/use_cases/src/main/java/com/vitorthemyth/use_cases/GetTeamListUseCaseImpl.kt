package com.vitorthemyth.use_cases

import com.vitorthemyth.domain.models.Time
import com.vitorthemyth.domain.repository.TeamRepository
import com.vitorthemyth.use_cases.interfaces.GetTeamListUseCase



class GetTeamListUseCaseImpl(private val repository: TeamRepository) : GetTeamListUseCase {
    override suspend fun execute(): List<Time> {
        return repository.getTeams()
    }
}