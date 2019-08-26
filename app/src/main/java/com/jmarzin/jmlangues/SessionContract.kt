package com.jmarzin.jmlangues

import android.provider.BaseColumns

/**
 * Created by jacques on 01/01/15.
 */
class SessionContract {

    abstract class SessionTable : BaseColumns {
        companion object {
            const val TABLE_NAME = "sessions"
            const val COLUMN_NAME_ID = "_id"
            const val COLUMN_NAME_LANGUE = "langue"
            const val COLUMN_NAME_DERNIERE = "derniere"
            const val COLUMN_NAME_MODE_REVISION = "modeRev"
            const val COLUMN_NAME_POIDS_MIN = "poidsMin"
            const val COLUMN_NAME_ERR_MIN = "errMin"
            const val COLUMN_NAME_AGE_REV = "ageRev"
            const val COLUMN_NAME_NIV_MAX = "nivMax"
            const val COLUMN_NAME_CONSERVE_STATS = "conserveStats"
            const val COLUMN_NAME_PARLE_AUTO = "parleAuto"
            const val COLUMN_NAME_NB_QUESTIONS = "nbQuestions"
            const val COLUMN_NAME_NB_ERREURS = "nbErreurs"
            const val COLUMN_NAME_LISTE_THEMES = "listeThemes"
            const val COLUMN_NAME_LISTE_VERBES = "listeVerbes"
            const val COLUMN_NAME_LISTE = "listeObjets"
            const val COLUMN_NAME_THEME_ID = "themeId"
            const val COLUMN_NAME_MOT_ID = "motId"
            const val COLUMN_NAME_VERBE_ID = "verbeId"
            const val COLUMN_NAME_FORME_ID = "formeID"
            const val COLUMN_NAME_FORME_TYPE_NUMERO = "formeTypeNumero"
        }
    }
}
