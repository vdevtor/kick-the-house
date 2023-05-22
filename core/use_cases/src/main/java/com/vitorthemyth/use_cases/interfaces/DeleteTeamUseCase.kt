package com.vitorthemyth.use_cases.interfaces

import com.vitorthemyth.domain.models.Time

interface DeleteTeamUseCase {
    suspend fun execute(team: Time)
}