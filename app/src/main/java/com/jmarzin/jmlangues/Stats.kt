package com.jmarzin.jmlangues

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

/**
 * Created by jacques on 26/01/15.
 */
class Stats {

    var id: Int = 0
    var langueId = ""
    var dateRev = "1900-01-01"
    var nbQuestionsMots = 0
    var nbErreursMots = 0
    var nbQuestionsFormes = 0
    var nbErreursFormes = 0

    fun save(db: SQLiteDatabase) {
        val values = ContentValues()
        values.put(StatsContract.StatsTable.COLUMN_NAME_LANGUE_ID, langueId)
        values.put(StatsContract.StatsTable.COLUMN_NAME_DATE, dateRev)
        values.put(StatsContract.StatsTable.COLUMN_NAME_NB_QUESTIONS_MOTS, nbQuestionsMots)
        values.put(StatsContract.StatsTable.COLUMN_NAME_NB_ERREURS_MOTS, nbErreursMots)
        values.put(StatsContract.StatsTable.COLUMN_NAME_NB_QUESTIONS_FORMES, nbQuestionsFormes)
        values.put(StatsContract.StatsTable.COLUMN_NAME_NB_ERREURS_FORMES, nbErreursFormes)

        if (this.id > 0) {
            val selection = StatsContract.StatsTable.COLUMN_NAME_ID + " = " + id
            db.update(StatsContract.StatsTable.TABLE_NAME, values, selection, null)
        } else {
            this.id = db.insert(StatsContract.StatsTable.TABLE_NAME, null, values).toInt()
        }
    }

    companion object {

        fun findBy(db: SQLiteDatabase, selection: String): Stats? {
            val mCursor = db.query(
                StatsContract.StatsTable.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                null
            )
            var stats: Stats? = Stats()
            if (mCursor.moveToFirst()) {
                stats!!.id =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(StatsContract.StatsTable.COLUMN_NAME_ID))
                stats.langueId =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(StatsContract.StatsTable.COLUMN_NAME_LANGUE_ID))
                stats.dateRev =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(StatsContract.StatsTable.COLUMN_NAME_DATE))
                stats.nbQuestionsMots =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(StatsContract.StatsTable.COLUMN_NAME_NB_QUESTIONS_MOTS))
                stats.nbErreursMots =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(StatsContract.StatsTable.COLUMN_NAME_NB_ERREURS_MOTS))
                stats.nbQuestionsFormes =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(StatsContract.StatsTable.COLUMN_NAME_NB_QUESTIONS_FORMES))
                stats.nbErreursFormes =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(StatsContract.StatsTable.COLUMN_NAME_NB_ERREURS_FORMES))
            } else {
                stats = null
            }
            mCursor.close()
            return stats
        }

        fun where(db: SQLiteDatabase, selection: String): Cursor {
            val sortOrder = StatsContract.StatsTable.COLUMN_NAME_DATE + " ASC"
            return db.query(
                StatsContract.StatsTable.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                sortOrder
            )
        }
    }
}

