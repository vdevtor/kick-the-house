package com.vitorthemyth.use_cases.interfaces



interface SaveTeamUseCase {

    suspend fun  execute(team:com.vitorthemyth.domain.models.Time)
}