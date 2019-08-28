package com.jmarzin.jmlangues

import android.provider.BaseColumns

/**
 * Created by jacques on 21/01/15.
 */
class StatsContract {

    abstract class StatsTable : BaseColumns {
        companion object {
            const val TABLE_NAME = "stats"
            const val COLUMN_NAME_ID = "_id"
            const val COLUMN_NAME_LANGUE_ID = "langue_id"
            const val COLUMN_NAME_DATE = "date"
            const val COLUMN_NAME_NB_QUESTIONS_MOTS = "nb_questions_mots"
            const val COLUMN_NAME_NB_ERREURS_MOTS = "nb_erreurs_mots"
            const val COLUMN_NAME_NB_QUESTIONS_FORMES = "nb_questions_formes"
            const val COLUMN_NAME_NB_ERREURS_FORMES = "nb_erreurs_formes"
        }
    }
}
