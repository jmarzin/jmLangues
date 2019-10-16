package com.jmarzin.jmlangues

import android.content.ContentValues
import android.database.Cursor
import org.json.JSONArray

/**
 * Created by jacques on 24/01/15.
 */
class Verbe : TermeBase() {

    fun save() {
        val values = ContentValues()

        values.put(VerbeContract.VerbeTable.COLUMN_NAME_DATE_MAJ, dateMaj)
        values.put(VerbeContract.VerbeTable.COLUMN_NAME_DIST_ID, distId)
        values.put(VerbeContract.VerbeTable.COLUMN_NAME_LANGUE, langue)
        values.put(VerbeContract.VerbeTable.COLUMN_NAME_LANGUE_ID, langueId)

        if (this.id > 0) {
            val selection = VerbeContract.VerbeTable.COLUMN_NAME_ID + " = " + id
            DSH.db().update(VerbeContract.VerbeTable.TABLE_NAME, values, selection, null)
        } else {
            this.id = DSH.db().insert(VerbeContract.VerbeTable.TABLE_NAME, null, values).toInt()
        }
    }

    companion object {

        private fun findBy(selection: String): Verbe? {
            val mCursor = DSH.db().query(
                VerbeContract.VerbeTable.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                null
            )
            var verbe: Verbe? = Verbe()
            if (mCursor.moveToFirst()) {
                verbe!!.id =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(VerbeContract.VerbeTable.COLUMN_NAME_ID))
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

        fun findByDistId(id: Int): Verbe? {
            val selection = VerbeContract.VerbeTable.COLUMN_NAME_DIST_ID + " = " + id
            return findBy(selection)
        }

        fun findMaxDateUpdate(langue: String): String {
            val cursor =
                DSH.db().query(
                    VerbeContract.VerbeTable.TABLE_NAME,
                    arrayOf("MAX(${VerbeContract.VerbeTable.COLUMN_NAME_DATE_MAJ}) AS MAX"),
                    VerbeContract.VerbeTable.COLUMN_NAME_LANGUE_ID + " = ?",
                    arrayOf(langue), null, null, null, null
                )
            cursor.moveToFirst()
            val index = cursor.getColumnIndex("MAX")
            val data = cursor.getString(index)
            cursor.close()
            return data ?: ""
        }

        fun where(selection: String): Cursor {
            val mCursor: Cursor
            val sortOrder = VerbeContract.VerbeTable.COLUMN_NAME_LANGUE + " ASC"
            mCursor = DSH.db().query(
                VerbeContract.VerbeTable.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                sortOrder
            )
            return mCursor
        }

        fun listeAvecNbFormes(): Cursor = DSH.db().rawQuery(
            "select verbes._id, verbes.langue, count(formes._id) as nombre from verbes " +
                    "inner join formes on formes.verbe_id = verbes.dist_id where verbes.langue_id = ? " +
                    "GROUP BY verbes.langue order by verbes.langue",
            arrayOf(DSH.session.langueId())
        )

        fun majBase(
            table: JSONArray,
            langue: String,
            date_maj: String
        ): Triple<Int, Int, Int> {
            var nbPlus = 0
            var nbMoins = 0
            var nbModif = 0
            for (it in 1..table.length()) {
                val verbeJson = table.optJSONArray(it - 1)
                val distId = verbeJson.getInt(0)
                val supp = verbeJson.getString(2)
                if (supp === "t") {
                    DSH.db().execSQL(
                        "DELETE FROM " + VerbeContract.VerbeTable.TABLE_NAME +
                                " WHERE " + VerbeContract.VerbeTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue + "\" AND " +
                                VerbeContract.VerbeTable.COLUMN_NAME_DIST_ID + " = " + distId
                    )
                    nbMoins++
                } else {
                    val selection =
                        VerbeContract.VerbeTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue + "\"" +
                                " AND " + VerbeContract.VerbeTable.COLUMN_NAME_DIST_ID + " = " + distId
                    var verbe = findBy(selection)
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
                    verbe.save()
                }
            }
            return Triple(nbPlus, nbMoins, nbModif)
        }
    }
}
