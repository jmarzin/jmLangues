package com.jmarzin.jmlangues

import android.provider.BaseColumns

/**
 * Created by jacques on 21/01/15.
 */
class StatsContract {

    abstract class StatsTable : BaseColumns {
        companion object {
            val TABLE_NAME = "stats"
            val COLUMN_NAME_ID = "id"
            val COLUMN_NAME_LANGUE_ID = "langueId"
            val COLUMN_NAME_DATE = "date"
            val COLUMN_NAME_NB_QUESTIONS_MOTS = "nb_questions_mots"
            val COLUMN_NAME_NB_ERREURS_MOTS = "nb_erreurs_mots"
            val COLUMN_NAME_NB_QUESTIONS_FORMES = "nb_questions_formes"
            val COLUMN_NAME_NB_ERREURS_FORMES = "nb_erreurs_formes"
        }
    }
}
