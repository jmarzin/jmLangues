package com.jmarzin.jmlangues

import android.provider.BaseColumns


/**
 * Created by jacques on 01/01/15.
 */
class FormeTypeContract {

    abstract class FormeTypeTable : BaseColumns {
        companion object {
            const val TABLE_NAME = "formestypes"
            const val COLUMN_NAME_ID = "_id"
            const val COLUMN_NAME_NUMERO = "numero"
            const val COLUMN_NAME_DIST_ID = "dist_id"
            const val COLUMN_NAME_LANGUE_ID = "langue_id"
            const val COLUMN_NAME_LANGUE = "langue"
            const val COLUMN_NAME_DATE_MAJ = "date_maj"
        }
    }
}
