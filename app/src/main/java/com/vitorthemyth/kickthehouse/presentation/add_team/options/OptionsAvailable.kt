package com.vitorthemyth.kickthehouse.presentation.add_team.options

import com.vitorthemyth.kickthehouse.R
import com.vitorthemyth.kickthehouse.helper.BaseEnums


enum class Importance(val text: Int, val value: Int) : BaseEnums {

    irrelevant(R.string.importance_irrelevant, 0),
    default(R.string.importance_normal, 2),
    importat(R.string.importance_important, 4),
    veryImportante(R.string.importance_very_important, 6)
}


enum class Difficult(val text: Int, val value: Int) : BaseEnums {

    veryEasy(R.string.difficult_irrelevant, 0),
    default(R.string.difficult_normal, 2),
    difficult(R.string.difficult_difficult, 4),
    veryDifficult(R.string.difficult_very_difficult, 6)
}


enum class MatchLocal(val text: Int, val value: Int) : BaseEnums {

    home(R.string.local_home, 0),
    away(R.string.local_away, 1)

}