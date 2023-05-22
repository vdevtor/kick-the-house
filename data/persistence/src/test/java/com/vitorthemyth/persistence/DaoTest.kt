package com.vitorthemyth.persistence

import com.vitorthemyth.persistence.mock.returnTime
import com.vitorthemyth.persistence.models.TimeEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test


class DaoTest {

    private val mockDaoProvider = mockk<TeamDao>(relaxed = true)

    @Before
    fun setup() {
        // Delete all the times from the database to start with an empty database
        mockDaoProvider.deleteAllTimes()
    }

    @After
    fun cleanup() {
        mockDaoProvider.deleteAllTimes()
    }

    @Test
    fun `check if a team is being saved on database`() {
        mockDaoProvider.insertTime(returnTime())
        coVerify {
            mockDaoProvider.insertTime(any() as TimeEntity)
        }
    }

    @Test
    fun `check if a team is being updated on database`() {
        coEvery {
            mockDaoProvider.getAllTimes()
        } returns listOf(returnTime())

        val time = mockDaoProvider.getAllTimes().first()

        mockDaoProvider.updateTime(time.copy(time = "Bahia"))

        coVerify {
            mockDaoProvider.updateTime(any() as TimeEntity)
        }
    }

    @Test
    fun `check if a team is being removed from the database`(){
        // Insert a team entity into the database
        val teamEntity = returnTime()
        mockDaoProvider.insertTime(teamEntity)
        coEvery {
            mockDaoProvider.insertTime(any())
        } returns Unit

        // Get the inserted team entity from the database
        coEvery {
            mockDaoProvider.getAllTimes()
        } returns listOf(returnTime())

        val savedTeam = mockDaoProvider.getAllTimes().first()

        // Delete the team entity from the database
        coEvery {
            mockDaoProvider.deleteTime(savedTeam)
        } returns Unit
        mockDaoProvider.deleteTime(savedTeam)

        // Verify that the team entity no longer exists in the database
        val deletedTeam = mockDaoProvider.getTimeById(timeId = savedTeam.id)
        assertNotNull(deletedTeam?.time)

        // Check that the getAllTimes() method now returns an empty list
        coEvery {
            mockDaoProvider.getAllTimes()
        } returns emptyList()
        assertTrue(mockDaoProvider.getAllTimes().isEmpty())
    }

}