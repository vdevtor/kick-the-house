package com.vitorthemyth.kickthehouse

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.vitorthemyth.kickthehouse.databinding.ActivityMainBinding
import com.vitorthemyth.kickthehouse.helper.version_checker.checkInAppUpdateNeeded
import com.vitorthemyth.kickthehouse.presentation.add_team.AddNewTeamFragment
import com.vitorthemyth.kickthehouse.presentation.probabilities.ManualProbabilityFragment
import com.vitorthemyth.kickthehouse.presentation.probabilities.ProbabilityFragment
import com.vitorthemyth.kickthehouse.presentation.team_list.TeamListFragment
import dagger.hilt.android.AndroidEntryPoint

const val FRAG_PROB = "probability Fragment"
const val FRAG_ADD = "ADD Fragment"
const val FRAG_PROB_MANUAL = "probability MANUAL Fragment"
const val FRAG_TEAM_LIST = "team list fragment"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val fragmentManager = supportFragmentManager
    private val probabilityFragment = ProbabilityFragment()
    private val addTeamFragment = AddNewTeamFragment()
    private val manualProbabilityFragment = ManualProbabilityFragment()
    private val teamListFragment = TeamListFragment()
    private lateinit var startFragment: Fragment

    private val viewModel : MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            viewModel.splashTimeout()

            this.setKeepOnScreenCondition {
                viewModel.isLoading.value
            }
            this.setOnExitAnimationListener { splashScreenView ->
                val slideBack = ObjectAnimator.ofFloat(
                    splashScreenView.view,
                    View.TRANSLATION_X,
                    0f,
                    -splashScreenView.view.width.toFloat()
                ).apply {
                    interpolator = DecelerateInterpolator()
                    duration = 800L
                    doOnEnd {
                        splashScreenView.remove()
                    }
                }
                slideBack.start()
            }
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        defineTheStarterFragment()
        setupBottomNavigation()
        this.checkInAppUpdateNeeded()
    }

    private fun defineTheStarterFragment() {
        startFragment = probabilityFragment

        // Add both fragments to the FragmentManager
        fragmentManager.beginTransaction().apply {
            add(R.id.main_fragment, startFragment, FRAG_PROB)
            add(R.id.main_fragment, addTeamFragment, FRAG_ADD)
            add(R.id.main_fragment, manualProbabilityFragment, FRAG_PROB_MANUAL)
            add(R.id.main_fragment, teamListFragment, FRAG_TEAM_LIST)
            commit()
        }

        // Hide all fragments except the starter one
        fragmentManager.beginTransaction().apply {
            hide(addTeamFragment)
            hide(manualProbabilityFragment)
            hide(teamListFragment)
            commitNow()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_probabilities -> {
                    fragmentManager.beginTransaction().apply {
                        fragmentManager.findFragmentByTag(FRAG_PROB)?.let { show(it) }
                        fragmentManager.findFragmentByTag(FRAG_ADD)?.let { hide(it) }
                        fragmentManager.findFragmentByTag(FRAG_PROB_MANUAL)?.let { hide(it) }
                        fragmentManager.findFragmentByTag(FRAG_TEAM_LIST)?.let { hide(it) }
                        commitNow()
                    }
                    true
                }
                R.id.menu_add_team -> {
                    fragmentManager.beginTransaction().apply {
                        fragmentManager.findFragmentByTag(FRAG_ADD)?.let { show(it) }
                        fragmentManager.findFragmentByTag(FRAG_PROB)?.let { hide(it) }
                        fragmentManager.findFragmentByTag(FRAG_PROB_MANUAL)?.let { hide(it) }
                        fragmentManager.findFragmentByTag(FRAG_TEAM_LIST)?.let { hide(it) }
                        commitNow()
                    }
                    true
                }
                R.id.menu_see_teams -> {
                    fragmentManager.beginTransaction().apply {
                        fragmentManager.findFragmentByTag(FRAG_ADD)?.let { hide(it) }
                        fragmentManager.findFragmentByTag(FRAG_PROB)?.let { hide(it) }
                        fragmentManager.findFragmentByTag(FRAG_PROB_MANUAL)?.let { hide(it) }
                        fragmentManager.findFragmentByTag(FRAG_TEAM_LIST)?.let { show(it) }
                        commitNow()
                    }

                    true
                }
                else -> false
            }
        }
    }

}