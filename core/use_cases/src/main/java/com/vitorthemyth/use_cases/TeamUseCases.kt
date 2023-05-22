package com.vitorthemyth.use_cases

import com.vitorthemyth.use_cases.interfaces.DeleteTeamUseCase
import com.vitorthemyth.use_cases.interfaces.GetTeamListUseCase
import com.vitorthemyth.use_cases.interfaces.SaveTeamUseCase
import com.vitorthemyth.use_cases.interfaces.UpdateTeam

class TeamUseCases constructor(
    val getTeamListUseCase: GetTeamListUseCase,
    val saveTeamUseCase: SaveTeamUseCase,
    val deleteTeamUseCase: DeleteTeamUseCase,
    val updateTeamUseCase: UpdateTeam
)