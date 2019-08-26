package com.jmarzin.jmlangues

import android.provider.BaseColumns

/**
 * Created by jacques on 01/01/15.
 */

class MotContract {

    abstract class MotTable : BaseColumns {
        companion object {
            val TABLE_NAME = "mots"
            val COLUMN_NAME_ID = "_id"
            val COLUMN_NAME_THEME_ID = "theme_id"
            val COLUMN_NAME_DIST_ID = "distId"
            val COLUMN_NAME_LANGUE_NIVEAU = "langue_niveau"
            val COLUMN_NAME_FRANCAIS = "francais"
            val COLUMN_NAME_MOT_DIRECTEUR = "mot_directeur"
            val COLUMN_NAME_LANGUE_ID = "langue_id"
            val COLUMN_NAME_LANGUE = "langue"
            val COLUMN_NAME_PRONUNCIATION = "prononciation"
            val COLUMN_NAME_DATE_MAJ = "dateMaj"
            val COLUMN_NAME_DATE_REV = "dateRev"
            val COLUMN_NAME_POIDS = "poids"
            val COLUMN_NAME_NB_ERR = "nbErr"
        }

    }
}