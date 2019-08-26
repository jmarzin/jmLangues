package com.jmarzin.jmlangues

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import org.json.JSONArray

/**
 * Created by jacques on 24/01/15.
 */
class FormeType : TermeBase() {
    var numero: Int = 0

    fun save(db: SQLiteDatabase) {
        val values = ContentValues()

        values.put(FormeTypeContract.FormeTypeTable.COLUMN_NAME_DATE_MAJ, dateMaj)
        values.put(FormeTypeContract.FormeTypeTable.COLUMN_NAME_DIST_ID, distId)
        values.put(FormeTypeContract.FormeTypeTable.COLUMN_NAME_LANGUE, langue)
        values.put(FormeTypeContract.FormeTypeTable.COLUMN_NAME_LANGUE_ID, langueId)
        values.put(FormeTypeContract.FormeTypeTable.COLUMN_NAME_NUMERO, numero)

        if (this.id > 0) {
            val selection = FormeTypeContract.FormeTypeTable.COLUMN_NAME_ID + " = " + id
            db.update(FormeTypeContract.FormeTypeTable.TABLE_NAME, values, selection, null)
        } else {
            this.id = db.insert(FormeTypeContract.FormeTypeTable.TABLE_NAME, null, values).toInt()
        }
    }

    companion object {

        private fun findBy(db: SQLiteDatabase, selection: String): FormeType? {
            val mCursor = db.query(
                FormeTypeContract.FormeTypeTable.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                null
            )
            var formeType: FormeType? = FormeType()
            if (mCursor.moveToFirst()) {
                formeType!!.id =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(FormeTypeContract.FormeTypeTable.COLUMN_NAME_ID))
                formeType.langueId =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(FormeTypeContract.FormeTypeTable.COLUMN_NAME_LANGUE_ID))
                formeType.numero =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(FormeTypeContract.FormeTypeTable.COLUMN_NAME_NUMERO))
                formeType.langue =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(FormeTypeContract.FormeTypeTable.COLUMN_NAME_LANGUE))
                formeType.distId =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(FormeTypeContract.FormeTypeTable.COLUMN_NAME_DIST_ID))
                formeType.dateMaj =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(FormeTypeContract.FormeTypeTable.COLUMN_NAME_DATE_MAJ))
            } else {
                formeType = null
            }
            mCursor.close()
            return formeType
        }

        fun findByNumber(db: SQLiteDatabase, id: Int): FormeType? {
            return findBy(db, FormeTypeContract.FormeTypeTable.COLUMN_NAME_NUMERO + " = " + id)
        }

        fun findMaxDateUpdate(db: SQLiteDatabase, langue: String): String {
            val cursor =
                db.query(
                    FormeTypeContract.FormeTypeTable.TABLE_NAME,
                    arrayOf("MAX(${FormeTypeContract.FormeTypeTable.COLUMN_NAME_DATE_MAJ}) AS MAX"),
                    FormeTypeContract.FormeTypeTable.COLUMN_NAME_LANGUE_ID + " = ?",
                    arrayOf(langue), null, null, null, null
                )
            cursor.moveToFirst()
            val index = cursor.getColumnIndex("MAX")
            val data = cursor.getString(index)
            cursor.close()
            return data ?: ""
        }

        fun where(db: SQLiteDatabase, selection: String): Cursor {
            val sortOrder = FormeTypeContract.FormeTypeTable.COLUMN_NAME_NUMERO + " ASC"
            return db.query(
                FormeTypeContract.FormeTypeTable.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                sortOrder
            )
        }

        fun majBase(
            table: JSONArray,
            db: SQLiteDatabase,
            langue: String,
            date_maj: String
        ): Triple<Int, Int, Int> {
            var nbPlus = 0
            var nbMoins = 0
            var nbModif = 0
            for (it in 1..table.length()) {
                val formeTypeJson = table.optJSONArray(it - 1)
                val distId = formeTypeJson.getInt(0)
                val supp = formeTypeJson.getString(3)
                if (supp === "t") {
                    db.execSQL(
                        "DELETE FROM " + FormeTypeContract.FormeTypeTable.TABLE_NAME +
                                " WHERE " + FormeTypeContract.FormeTypeTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue + "\" AND " +
                                FormeTypeContract.FormeTypeTable.COLUMN_NAME_DIST_ID + " = " + distId
                    )
                    nbMoins++
                } else {
                    val selection =
                        FormeTypeContract.FormeTypeTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue + "\"" +
                                " AND " + FormeTypeContract.FormeTypeTable.COLUMN_NAME_DIST_ID + " = " + distId
                    var formeType = findBy(db, selection)
                    if (formeType == null) {
                        formeType = FormeType()
                        formeType.distId = distId
                        formeType.langueId = langue
                        nbPlus++
                    } else {
                        nbModif++
                    }
                    formeType.numero = formeTypeJson.getInt(2)
                    formeType.langue = formeTypeJson.getString(1)
                    formeType.dateMaj = date_maj
                    formeType.save(db)
                }
            }
            return Triple(nbPlus, nbMoins, nbModif)
        }
    }
}
