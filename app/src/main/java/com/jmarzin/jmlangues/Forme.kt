package com.jmarzin.jmlangues

import android.content.ContentValues
import android.database.Cursor
import org.json.JSONArray
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by jacques on 24/01/15.
 */
class Forme : ItemQuestionnable() {

    var verbe: Verbe? = null
    var formeType: FormeType? = null
    var verbeId: Int = 0
    var formeTypeNumero: Int = 0

    override fun save() {
        val values = ContentValues()
        values.put(FormeContract.FormeTable.COLUMN_NAME_POIDS, poids)
        values.put(FormeContract.FormeTable.COLUMN_NAME_NB_ERR, nbErr)
        values.put(FormeContract.FormeTable.COLUMN_NAME_DATE_REV, dateRev)
        values.put(FormeContract.FormeTable.COLUMN_NAME_DATE_MAJ, dateMaj)
        values.put(FormeContract.FormeTable.COLUMN_NAME_DIST_ID, distId)
        values.put(FormeContract.FormeTable.COLUMN_NAME_FORME_TYPE_NUMERO, formeTypeNumero)
        values.put(FormeContract.FormeTable.COLUMN_NAME_LANGUE, langue)
        values.put(FormeContract.FormeTable.COLUMN_NAME_LANGUE_ID, langueId)
        values.put(FormeContract.FormeTable.COLUMN_NAME_VERBE_ID, verbeId)

        if (this.id > 0) {
            val selection = FormeContract.FormeTable.COLUMN_NAME_ID + " = " + id
            DSH.db().update(FormeContract.FormeTable.TABLE_NAME, values, selection, null)
        } else {
            this.id = DSH.db().insert(FormeContract.FormeTable.TABLE_NAME, null, values).toInt()
        }
    }

    companion object {

        private fun findBy(selection: String): Forme? {
            val mCursor = DSH.db().query(
                FormeContract.FormeTable.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                null
            )
            var forme: Forme? = null
            if (mCursor.moveToFirst()) {
                forme = Forme()
                forme.id =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(FormeContract.FormeTable.COLUMN_NAME_ID))
                forme.langueId =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(FormeContract.FormeTable.COLUMN_NAME_LANGUE_ID))
                forme.verbeId =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(FormeContract.FormeTable.COLUMN_NAME_VERBE_ID))
                forme.verbe = Verbe.findByDistId(forme.verbeId)
                forme.formeTypeNumero =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(FormeContract.FormeTable.COLUMN_NAME_FORME_TYPE_NUMERO))
                forme.formeType = FormeType.findByNumber(forme.formeTypeNumero)
                forme.langue =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(FormeContract.FormeTable.COLUMN_NAME_LANGUE))
                forme.dateRev =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(FormeContract.FormeTable.COLUMN_NAME_DATE_REV))
                forme.poids =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(FormeContract.FormeTable.COLUMN_NAME_POIDS))
                forme.nbErr =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(FormeContract.FormeTable.COLUMN_NAME_NB_ERR))
                forme.distId =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(FormeContract.FormeTable.COLUMN_NAME_DIST_ID))
                forme.dateMaj =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(FormeContract.FormeTable.COLUMN_NAME_DATE_MAJ))
            }
            mCursor.close()
            return forme
        }

        fun find(id: Int): Forme? {
            return findBy(FormeContract.FormeTable.COLUMN_NAME_ID + " = " + id)
        }

        fun findMaxDateUpdate(langue: String): String {
            val cursor =
                DSH.db().query(
                    FormeContract.FormeTable.TABLE_NAME,
                    arrayOf("MAX(${FormeContract.FormeTable.COLUMN_NAME_DATE_MAJ}) AS MAX"),
                    FormeContract.FormeTable.COLUMN_NAME_LANGUE_ID + " = ?",
                    arrayOf(langue), null, null, null, null
                )
            cursor.moveToFirst()
            val index = cursor.getColumnIndex("MAX")
            val data = cursor.getString(index)
            cursor.close()
            return data ?: ""
        }

        fun where(selection: String): Cursor {
            val sortOrder = FormeContract.FormeTable.COLUMN_NAME_VERBE_ID + "," +
                    FormeContract.FormeTable.COLUMN_NAME_FORME_TYPE_NUMERO + " ASC"
            return DSH.db().query(
                FormeContract.FormeTable.TABLE_NAME,
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
            langue: String,
            date_maj: String
        ): Triple<Int, Int, Int> {
            var nbPlus = 0
            var nbMoins = 0
            var nbModif = 0
            for (it in 1..table.length()) {
                val formeJson = table.optJSONArray(it - 1)
                val distId = formeJson.getInt(0)
                val supp = formeJson.getString(4)
                if (supp === "t") {
                    DSH.db().execSQL(
                        "DELETE FROM " + FormeContract.FormeTable.TABLE_NAME +
                                " WHERE " + FormeContract.FormeTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue + "\" AND " +
                                FormeContract.FormeTable.COLUMN_NAME_DIST_ID + " = " + distId
                    )
                    nbMoins++
                } else {
                    val selection =
                        FormeContract.FormeTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue + "\"" +
                                " AND " + FormeContract.FormeTable.COLUMN_NAME_DIST_ID + " = " + distId
                    var forme = findBy(selection)
                    if (forme == null) {
                        forme = Forme()
                        forme.distId = distId
                        forme.langueId = langue
                        nbPlus++
                    } else {
                        nbModif++
                    }
                    forme.verbeId = formeJson.getInt(1)
                    forme.formeTypeNumero = formeJson.getInt(2)
                    forme.langue = formeJson.getString(3)
                    forme.dateMaj = date_maj
                    forme.save()
                }
            }
            return Triple(nbPlus, nbMoins, nbModif)
        }

        fun selection(): Cursor {
            val date = Timestamp(System.currentTimeMillis())
            date.time = date.time - DSH.session.ageRev * 24 * 3600000
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE)
            val dateRev = sdf.format(date)
            val cond1: String = FormeContract.FormeTable.COLUMN_NAME_LANGUE_ID + " = \"" + DSH.session.langueId() + "\""
            var cond2 = ""
            var cond3 = ""
            var cond4 = ""
            var cond5 = ""
            val cond6 = ""
            if (DSH.session.ageRev != 0) cond2 =
                    """ AND ${FormeContract.FormeTable.COLUMN_NAME_DATE_REV} <= "$dateRev""""
                if (DSH.session.poidsMin > 1) cond3 =
                    """ AND ${FormeContract.FormeTable.COLUMN_NAME_POIDS} >= ${DSH.session.poidsMin}"""
                if (DSH.session.errMin > 0) cond4 =
                    """ AND ${FormeContract.FormeTable.COLUMN_NAME_NB_ERR} >= ${DSH.session.errMin}"""
                if (DSH.session.listeVerbes.isNotEmpty()) {
                    cond5 = """ AND ${FormeContract.FormeTable.COLUMN_NAME_VERBE_ID} IN ("""
                    for (i in DSH.session.listeVerbes.indices) {
                        if (i != 0) {
                            cond5 += ","
                        }
                        cond5 += DSH.session.listeVerbes[i]
                    }
                    cond5 += ")"
                }
            return where(cond1 + cond2 + cond3 + cond4 + cond5 + cond6)
        }
    }
}
