package com.jmarzin.jmlangues

import android.content.ContentValues
import android.database.Cursor
import org.json.JSONArray

/**
 * Created by jacques on 24/01/15.
 */
class Theme : TermeBase() {
    var numero: Int = 0

    fun save() {
        val values = ContentValues()

        values.put(ThemeContract.ThemeTable.COLUMN_NAME_DATE_MAJ, dateMaj)
        values.put(ThemeContract.ThemeTable.COLUMN_NAME_DIST_ID, distId)
        values.put(ThemeContract.ThemeTable.COLUMN_NAME_LANGUE, langue)
        values.put(ThemeContract.ThemeTable.COLUMN_NAME_LANGUE_ID, langueId)
        values.put(ThemeContract.ThemeTable.COLUMN_NAME_NUMERO, numero)

        if (this.id > 0) {
            val selection = ThemeContract.ThemeTable.COLUMN_NAME_ID + " = " + id
            DSH.db().update(ThemeContract.ThemeTable.TABLE_NAME, values, selection, null)
        } else {
            this.id = DSH.db().insert(ThemeContract.ThemeTable.TABLE_NAME, null, values).toInt()
        }
    }

    companion object {

        fun findBy(selection: String): Theme? {
            val mCursor = DSH.db().query(
                ThemeContract.ThemeTable.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                null
            )
            var theme: Theme? = Theme()
            if (mCursor.moveToFirst()) {
                theme!!.id =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(ThemeContract.ThemeTable.COLUMN_NAME_ID))
                theme.langueId =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(ThemeContract.ThemeTable.COLUMN_NAME_LANGUE_ID))
                theme.numero =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(ThemeContract.ThemeTable.COLUMN_NAME_NUMERO))
                theme.langue =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(ThemeContract.ThemeTable.COLUMN_NAME_LANGUE))
                theme.distId =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(ThemeContract.ThemeTable.COLUMN_NAME_DIST_ID))
                theme.dateMaj =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(ThemeContract.ThemeTable.COLUMN_NAME_DATE_MAJ))
            } else {
                theme = null
            }
            mCursor.close()
            return theme
        }

        fun findByDistId(id: Int): Theme? {
            val selection = ThemeContract.ThemeTable.COLUMN_NAME_DIST_ID + " = " + id
            return findBy(selection)
        }

        fun findMaxDateUpdate(langue: String): String {
            val cursor =
                DSH.db().query(
                    ThemeContract.ThemeTable.TABLE_NAME,
                    arrayOf("MAX(${ThemeContract.ThemeTable.COLUMN_NAME_DATE_MAJ}) AS MAX"),
                    ThemeContract.ThemeTable.COLUMN_NAME_LANGUE_ID + " = ?",
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
            val sortOrder = ThemeContract.ThemeTable.COLUMN_NAME_NUMERO + " ASC"
            mCursor = DSH.db().query(
                ThemeContract.ThemeTable.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                sortOrder
            )
            return mCursor
        }

        fun majBase(
            table: JSONArray,
            langue: String,
            date_maj: String
        ): Triple<Int, Int, Int> {
            var nbPlus = 0
            var nbMoins = 0
            var nbModif = 0
            for (it in 1..table.length()) {
                val themeJson = table.optJSONArray(it - 1)
                val distId = themeJson.getInt(0)
                val supp = themeJson.getString(3)
                if (supp === "t") {
                    DSH.db().execSQL(
                        "DELETE FROM " + ThemeContract.ThemeTable.TABLE_NAME +
                                " WHERE " + ThemeContract.ThemeTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue + "\" AND " +
                                ThemeContract.ThemeTable.COLUMN_NAME_DIST_ID + " = " + distId
                    )
                    nbMoins++
                } else {
                    val selection =
                        ThemeContract.ThemeTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue + "\"" +
                                " AND " + ThemeContract.ThemeTable.COLUMN_NAME_DIST_ID + " = " + distId
                    var theme = findBy(selection)
                    if (theme == null) {
                        theme = Theme()
                        theme.distId = distId
                        theme.langueId = langue
                        nbPlus++
                    } else {
                        nbModif++
                    }
                    theme.numero = themeJson.getInt(1)
                    theme.langue = themeJson.getString(2)
                    theme.dateMaj = date_maj
                    theme.save()
                }
            }
            return Triple(nbPlus, nbMoins, nbModif)
        }
    }
}
