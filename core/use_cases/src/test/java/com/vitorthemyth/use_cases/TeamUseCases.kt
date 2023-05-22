package com.vitorthemyth.use_cases

import com.vitorthemyth.use_cases.fake.FakeTeamRepository
import com.vitorthemyth.use_cases.interfaces.DeleteTeamUseCase
import com.vitorthemyth.use_cases.interfaces.GetTeamListUseCase
import com.vitorthemyth.use_cases.interfaces.SaveTeamUseCase
import com.vitorthemyth.use_cases.interfaces.UpdateTeam
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*


@OptIn(ExperimentalCoroutinesApi::class)
class TeamUseCases {
    private lateinit var teamRepository: FakeTeamRepository
    private lateinit var getTeamListUseCase: GetTeamListUseCase
    private lateinit var saveTeamUseCases: SaveTeamUseCase
    private lateinit var deleteTeamUseCase: DeleteTeamUseCase
    private lateinit var updateTeam: UpdateTeam
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)


    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        teamRepository = FakeTeamRepository()
        getTeamListUseCase = GetTeamListUseCaseImpl(teamRepository)
        saveTeamUseCases = SaveTeamUseCaseImp(teamRepository)
        deleteTeamUseCase = DeleteTeamUseCaseImp(teamRepository)
        updateTeam = UpdateTeamUseCaseImp(teamRepository)
        scope.launch {
            teamRepository.deleteAll()
        }

    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun `check if getTeamList will return empty at first call`() = scope.runTest {
        assert(getTeamListUseCase.execute().isEmpty())
    }

    @Test
    fun `check if getTeamList will return notEmpty after adding team`() = scope.runTest {
        val time = returnTime()
        saveTeamUseCases.execute(time)
        assert(getTeamListUseCase.execute().isNotEmpty())
    }

    @Test
    fun `check if the team has been deleted`() = scope.runTest {
        val time = returnTime()
        saveTeamUseCases.execute(time)

        val teamsBefore = getTeamListUseCase.execute()
        assert(teamsBefore.size == 1)

        deleteTeamUseCase.execute(time)

        val teamsAfter = getTeamListUseCase.execute()
        assert(teamsAfter.isEmpty())
    }

    @Test
    fun `check if the team has been update successfully`() = scope.runTest {
        val time = returnTime()
        val oldName = time.time
        val newName = "Bahia"
        saveTeamUseCases.execute(time)

        assert(getTeamListUseCase.execute().first().time == oldName)


        updateTeam.execute(time.copy(time = newName))

        assert(getTeamListUseCase.execute().first().time == newName)
    }
}

