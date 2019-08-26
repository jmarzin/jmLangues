package com.jmarzin.jmlangues

import android.provider.BaseColumns

/**
 * Created by jacques on 01/01/15.
 */
class FormeContract {


    abstract class FormeTable : BaseColumns {
        companion object {
            const val TABLE_NAME = "formes"
            const val COLUMN_NAME_ID = "_id"
            const val COLUMN_NAME_VERBE_ID = "verbe_id"
            const val COLUMN_NAME_FORME_TYPE_NUMERO = "forme_type_numero"
            const val COLUMN_NAME_DIST_ID = "dist_id"
            const val COLUMN_NAME_LANGUE_ID = "langue_id"
            const val COLUMN_NAME_LANGUE = "langue"
            const val COLUMN_NAME_DATE_MAJ = "date_maj"
            const val COLUMN_NAME_DATE_REV = "date_rev"
            const val COLUMN_NAME_POIDS = "poids"
            const val COLUMN_NAME_NB_ERR = "nb_err"
        }
    }
}
