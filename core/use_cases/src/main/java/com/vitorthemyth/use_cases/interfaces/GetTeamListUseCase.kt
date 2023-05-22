package com.vitorthemyth.use_cases.interfaces

import com.vitorthemyth.domain.models.Time

interface GetTeamListUseCase{

    suspend fun execute(): List<Time>
}