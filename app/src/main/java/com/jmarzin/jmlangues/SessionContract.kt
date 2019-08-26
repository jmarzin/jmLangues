package com.jmarzin.jmlangues

import android.provider.BaseColumns

/**
 * Created by jacques on 01/01/15.
 */
class SessionContract {

    abstract class SessionTable : BaseColumns {
        companion object {
            val TABLE_NAME = "sessions"
            val COLUMN_NAME_ID = "_id"
            val COLUMN_NAME_LANGUE = "langue"
            val COLUMN_NAME_DERNIERE = "derniere"
            val COLUMN_NAME_MODE_REVISION = "modeRev"
            val COLUMN_NAME_POIDS_MIN = "poidsMin"
            val COLUMN_NAME_ERR_MIN = "errMin"
            val COLUMN_NAME_AGE_REV = "ageRev"
            val COLUMN_NAME_NIV_MAX = "nivMax"
            val COLUMN_NAME_CONSERVE_STATS = "conserveStats"
            val COLUMN_NAME_PARLE_AUTO = "parleAuto"
            val COLUMN_NAME_NB_QUESTIONS = "nbQuestions"
            val COLUMN_NAME_NB_ERREURS = "nbErreurs"
            val COLUMN_NAME_LISTE_THEMES = "listeThemes"
            val COLUMN_NAME_LISTE_VERBES = "listeVerbes"
            val COLUMN_NAME_LISTE = "listeObjets"
            val COLUMN_NAME_THEME_ID = "themeId"
            val COLUMN_NAME_MOT_ID = "motId"
            val COLUMN_NAME_VERBE_ID = "verbeId"
            val COLUMN_NAME_FORME_ID = "formeID"
            val COLUMN_NAME_FORME_TYPE_NUMERO = "formeTypeNumero"
        }
    }
}
