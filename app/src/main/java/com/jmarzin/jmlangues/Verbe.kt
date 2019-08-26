package com.jmarzin.jmlangues

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import org.json.JSONArray

/**
 * Created by jacques on 24/01/15.
 */
class Verbe : TermeBase() {

    fun save(db: SQLiteDatabase) {
        val values = ContentValues()

        values.put(VerbeContract.VerbeTable.COLUMN_NAME_DATE_MAJ, dateMaj)
        values.put(VerbeContract.VerbeTable.COLUMN_NAME_DIST_ID, distId)
        values.put(VerbeContract.VerbeTable.COLUMN_NAME_LANGUE, langue)
        values.put(VerbeContract.VerbeTable.COLUMN_NAME_LANGUE_ID, langueId)

        if (this.id > 0) {
            val selection = VerbeContract.VerbeTable.COLUMN_NAME_ID + " = " + id
            val count = db.update(VerbeContract.VerbeTable.TABLE_NAME, values, selection, null)
        } else {
            this.id = db.insert(VerbeContract.VerbeTable.TABLE_NAME, null, values).toInt()
        }
    }

    companion object {

        fun find_by(db: SQLiteDatabase, selection: String): Verbe? {
            val mCursor = db.query(VerbeContract.VerbeTable.TABLE_NAME, null, selection, null, null, null, null)
            var verbe: Verbe? = Verbe()
            if (mCursor.moveToFirst()) {
                verbe!!.id = mCursor.getInt(mCursor.getColumnIndexOrThrow(VerbeContract.VerbeTable.COLUMN_NAME_ID))
                verbe.langueId =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(VerbeContract.VerbeTable.COLUMN_NAME_LANGUE_ID))
                verbe.langue =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(VerbeContract.VerbeTable.COLUMN_NAME_LANGUE))
                verbe.distId =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(VerbeContract.VerbeTable.COLUMN_NAME_DIST_ID))
                verbe.dateMaj =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(VerbeContract.VerbeTable.COLUMN_NAME_DATE_MAJ))
            } else {
                verbe = null
            }
            mCursor.close()
            return verbe
        }

        fun find(db: SQLiteDatabase, id: Int): Verbe? {
            val selection = VerbeContract.VerbeTable.COLUMN_NAME_ID + " = " + id
            return Verbe.find_by(db, selection)
        }

        fun find_max_date_update(db: SQLiteDatabase, langue: String): String {
            val cursor =
                db.query(VerbeContract.VerbeTable.TABLE_NAME,
                    arrayOf<String>("MAX(${VerbeContract.VerbeTable.COLUMN_NAME_DATE_MAJ}) AS MAX"),
                    VerbeContract.VerbeTable.COLUMN_NAME_LANGUE_ID + " = ?",
                    arrayOf<String>(langue),null, null, null, null)
            cursor.moveToFirst()
            val index = cursor.getColumnIndex("MAX")
            val data = cursor.getString(index)
            return if (data != null) data else ""
        }

        fun where(db: SQLiteDatabase, selection: String): Cursor {
            val mCursor: Cursor
            val sortOrder = VerbeContract.VerbeTable.COLUMN_NAME_LANGUE + " ASC"
            mCursor = db.query(VerbeContract.VerbeTable.TABLE_NAME, null, selection, null, null, null, sortOrder)
            return mCursor
        }

        fun maj_base(table: JSONArray, db: SQLiteDatabase, langue: String, date_maj: String): Triple<Int, Int, Int> {
            var nbPlus = 0
            var nbMoins = 0
            var nbModif = 0
            for (it in 1..table.length()) {
                val verbeJson = table.optJSONArray(it - 1)
                val distId = verbeJson.getInt(0)
                val supp = verbeJson.getString(2)
                if (supp === "t") {
                    db.execSQL(
                        "DELETE FROM " + VerbeContract.VerbeTable.TABLE_NAME +
                                " WHERE " + VerbeContract.VerbeTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue + "\" AND " +
                                VerbeContract.VerbeTable.COLUMN_NAME_DIST_ID + " = " + distId )
                    nbMoins++
                } else {
                    val selection =
                        VerbeContract.VerbeTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue + "\"" +
                                " AND " + VerbeContract.VerbeTable.COLUMN_NAME_DIST_ID + " = " + distId
                    var verbe = Verbe.find_by(db, selection)
                    if (verbe == null) {
                        verbe = Verbe()
                        verbe.distId = distId
                        verbe.langueId = langue
                        nbPlus++
                    } else {
                        nbModif++
                    }
                    verbe.langue = verbeJson.getString(1)
                    verbe.dateMaj = date_maj
                    verbe.save(db)
                }
            }
            return Triple(nbPlus, nbMoins, nbModif)
        }
    }
}
