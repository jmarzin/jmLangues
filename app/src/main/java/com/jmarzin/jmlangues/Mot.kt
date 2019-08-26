package com.jmarzin.jmlangues

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import org.json.JSONArray

/**
 * Created by jacques on 23/01/15.
 */
class Mot : ItemQuestionnable() {

    var francais: String = ""
    var motDirecteur: String = ""
    var langueNiveau: String = ""
    var pronunciation: String = ""
    var theme: Theme = Theme()

    override fun save(db: SQLiteDatabase) {

        val values = ContentValues()
        values.put(MotContract.MotTable.COLUMN_NAME_POIDS, poids)
        values.put(MotContract.MotTable.COLUMN_NAME_NB_ERR, nbErr)
        values.put(MotContract.MotTable.COLUMN_NAME_DATE_REV, dateRev)
        values.put(MotContract.MotTable.COLUMN_NAME_DATE_MAJ, dateMaj)
        values.put(MotContract.MotTable.COLUMN_NAME_DIST_ID, distId)
        values.put(MotContract.MotTable.COLUMN_NAME_FRANCAIS, francais)
        values.put(MotContract.MotTable.COLUMN_NAME_LANGUE, langue)
        values.put(MotContract.MotTable.COLUMN_NAME_LANGUE_ID, langueId)
        values.put(MotContract.MotTable.COLUMN_NAME_MOT_DIRECTEUR, motDirecteur)
        values.put(MotContract.MotTable.COLUMN_NAME_LANGUE_NIVEAU, langueNiveau)
        values.put(MotContract.MotTable.COLUMN_NAME_PRONUNCIATION, pronunciation)
        values.put(MotContract.MotTable.COLUMN_NAME_THEME_ID, theme.id)

        if (this.id > 0) {
            val selection = MotContract.MotTable.COLUMN_NAME_ID + " = " + id
            db.update(MotContract.MotTable.TABLE_NAME, values, selection, null)
        } else {
            this.id = db.insert(MotContract.MotTable.TABLE_NAME, null, values).toInt()
        }
    }

    companion object {

        private fun findBy(db: SQLiteDatabase, selection: String): Mot? {
            val mCursor =
                db.query(MotContract.MotTable.TABLE_NAME, null, selection, null, null, null, null)
            var mot: Mot? = Mot()
            if (mCursor.moveToFirst()) {
                mot!!.id =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_ID))
                mot.langueId =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_LANGUE_ID))
                val themeId =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_THEME_ID))
                mot.theme = Theme.find(db, themeId)!!
                mot.francais =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_FRANCAIS))
                        .trim()
                mot.langueNiveau =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_LANGUE_NIVEAU))
                mot.pronunciation =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_PRONUNCIATION))
                mot.motDirecteur =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_MOT_DIRECTEUR))
                mot.langue =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_LANGUE))
                mot.dateRev =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_DATE_REV))
                mot.poids =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_POIDS))
                mot.nbErr =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_NB_ERR))
                mot.distId =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_DIST_ID))
                mot.dateMaj =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_DATE_MAJ))
            } else {
                mot = null
            }
            mCursor.close()
            return mot
        }

        fun find(db: SQLiteDatabase, id: Int): Mot? {
            val selection = MotContract.MotTable.COLUMN_NAME_ID + " = " + id
            return findBy(db, selection)
        }

        fun findMaxDateUpdate(db: SQLiteDatabase, langue: String): String {
            val cursor =
                db.query(
                    MotContract.MotTable.TABLE_NAME,
                    arrayOf("MAX(${MotContract.MotTable.COLUMN_NAME_DATE_MAJ}) AS MAX"),
                    MotContract.MotTable.COLUMN_NAME_LANGUE_ID + " = ?",
                    arrayOf(langue), null, null, null, null
                )
            cursor.moveToFirst()
            val index = cursor.getColumnIndex("MAX")
            val data = cursor.getString(index)
            cursor.close()
            return data ?: ""
        }

        fun where(db: SQLiteDatabase, selection: String): Cursor {
            val sortOrder = MotContract.MotTable.COLUMN_NAME_MOT_DIRECTEUR + " ASC"
            return db.query(
                MotContract.MotTable.TABLE_NAME,
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
                val motJson = table.optJSONArray(it - 1)
                val distId = motJson.getInt(0)
                val supp = motJson.getString(7)
                if (supp === "t") {
                    db.execSQL(
                        "DELETE FROM " + MotContract.MotTable.TABLE_NAME +
                                " WHERE " + MotContract.MotTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue + "\" AND " +
                                MotContract.MotTable.COLUMN_NAME_DIST_ID + " = " + distId
                    )
                    nbMoins++
                } else {
                    val selection =
                        MotContract.MotTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue + "\"" +
                                " AND " + MotContract.MotTable.COLUMN_NAME_DIST_ID + " = " + distId
                    var mot = findBy(db, selection)
                    if (mot == null) {
                        mot = Mot()
                        mot.distId = distId
                        mot.langueId = langue
                        nbPlus++
                    } else {
                        nbModif++
                    }
                    mot.theme.id = motJson.getInt(1)
                    mot.francais = motJson.getString(2).trim()
                    mot.motDirecteur = motJson.getString(3).trim()
                    mot.langue = motJson.getString(4).trim()
                    mot.langueNiveau = motJson.getString(5)
                    mot.pronunciation = motJson.getString(6)
                    mot.dateMaj = date_maj
                    mot.save(db)
                }
            }
            return Triple(nbPlus, nbMoins, nbModif)
        }
    }
}
