package com.jmarzin.jmlangues

import android.provider.BaseColumns

/**
 * Created by jacques on 01/01/15.
 */

class MotContract {

    abstract class MotTable : BaseColumns {
        companion object {
            const val TABLE_NAME = "mots"
            const val COLUMN_NAME_ID = "_id"
            const val COLUMN_NAME_THEME_ID = "theme_id"
            const val COLUMN_NAME_DIST_ID = "distId"
            const val COLUMN_NAME_LANGUE_NIVEAU = "langue_niveau"
            const val COLUMN_NAME_FRANCAIS = "francais"
            const val COLUMN_NAME_MOT_DIRECTEUR = "mot_directeur"
            const val COLUMN_NAME_LANGUE_ID = "langue_id"
            const val COLUMN_NAME_LANGUE = "langue"
            const val COLUMN_NAME_PRONUNCIATION = "prononciation"
            const val COLUMN_NAME_DATE_MAJ = "dateMaj"
            const val COLUMN_NAME_DATE_REV = "dateRev"
            const val COLUMN_NAME_POIDS = "poids"
            const val COLUMN_NAME_NB_ERR = "nbErr"
        }

    }
}