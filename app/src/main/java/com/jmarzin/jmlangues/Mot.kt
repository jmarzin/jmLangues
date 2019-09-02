package com.jmarzin.jmlangues

import android.content.ContentValues
import android.database.Cursor
import org.json.JSONArray
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by jacques on 23/01/15.
 */
class Mot : ItemQuestionnable() {

    var francais: String = ""
    var motDirecteur: String = ""
    var langueNiveau: String = ""
    var pronunciation: String = ""
    var theme: Theme = Theme()

    override fun save() {

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
        values.put(MotContract.MotTable.COLUMN_NAME_THEME_ID, theme.distId)

        if (this.id > 0) {
            val selection = MotContract.MotTable.COLUMN_NAME_ID + " = " + id
            DSH.db().update(MotContract.MotTable.TABLE_NAME, values, selection, null)
        } else {
            this.id = DSH.db().insert(MotContract.MotTable.TABLE_NAME, null, values).toInt()
        }
    }

    companion object {

        private fun findBy(selection: String): Mot? {
            val mCursor =
                DSH.db()
                    .query(MotContract.MotTable.TABLE_NAME, null, selection, null, null, null, null)
            var mot: Mot? = Mot()
            if (mCursor.moveToFirst()) {
                mot!!.id =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_ID))
                mot.langueId =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_LANGUE_ID))
                val themeId =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_THEME_ID))
                mot.theme = Theme.findByDistId(themeId)!!
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

        fun find(id: Int): Mot? {
            val selection = MotContract.MotTable.COLUMN_NAME_ID + " = " + id
            return findBy(selection)
        }

        fun findMaxDateUpdate(langue: String): String {
            val cursor =
                DSH.db().query(
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

        fun where(selection: String): Cursor {
            val sortOrder = MotContract.MotTable.COLUMN_NAME_MOT_DIRECTEUR + " ASC"
            return DSH.db().query(
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
                    DSH.db().execSQL(
                        "DELETE FROM " + MotContract.MotTable.TABLE_NAME +
                                " WHERE " + MotContract.MotTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue + "\" AND " +
                                MotContract.MotTable.COLUMN_NAME_DIST_ID + " = " + distId
                    )
                    nbMoins++
                } else {
                    val selection =
                        MotContract.MotTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue + "\"" +
                                " AND " + MotContract.MotTable.COLUMN_NAME_DIST_ID + " = " + distId
                    var mot = findBy(selection)
                    if (mot == null) {
                        mot = Mot()
                        mot.distId = distId
                        mot.langueId = langue
                        nbPlus++
                    } else {
                        nbModif++
                    }
                    mot.theme = Theme()
                    mot.theme.distId = motJson.getInt(1)
                    mot.francais = motJson.getString(2).trim()
                    mot.motDirecteur = motJson.getString(3).trim()
                    mot.langue = motJson.getString(4).trim()
                    mot.langueNiveau = motJson.getString(5)
                    mot.pronunciation = motJson.getString(6)
                    mot.dateMaj = date_maj
                    mot.save()
                }
            }
            return Triple(nbPlus, nbMoins, nbModif)
        }

        fun selection(): Cursor {
            val date = Timestamp(System.currentTimeMillis())
            date.time = date.time - DSH.session.ageRev * 24 * 3600000
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE)
            val dateRev = sdf.format(date)
            var cond2 = ""
            var cond3 = ""
            var cond4 = ""
            var cond5 = ""
            val cond1 =
                """${MotContract.MotTable.COLUMN_NAME_LANGUE_ID} = "${DSH.session.langueId()}""""
            val cond6 =
                """ AND ${MotContract.MotTable.COLUMN_NAME_LANGUE_NIVEAU} <= "${DSH.session.nivMax}""""
            if (DSH.session.ageRev != 0) cond2 =
                """ AND ${MotContract.MotTable.COLUMN_NAME_DATE_REV} <= "$dateRev""""
            if (DSH.session.poidsMin > 1) cond3 =
                """ AND ${MotContract.MotTable.COLUMN_NAME_POIDS} >= ${DSH.session.poidsMin}"""
            if (DSH.session.errMin > 0) cond4 =
                """ AND ${MotContract.MotTable.COLUMN_NAME_NB_ERR} >= ${DSH.session.errMin}"""
            if (DSH.session.listeThemes.isNotEmpty()) {
                cond5 = """ AND ${MotContract.MotTable.COLUMN_NAME_THEME_ID} IN ("""
                for (i in DSH.session.listeThemes.indices) {
                    if (i != 0) {
                        cond5 += ","
                    }
                    cond5 += DSH.session.listeThemes[i]
                }
                cond5 += ")"
            }
            return where(cond1 + cond2 + cond3 + cond4 + cond5 + cond6)
        }

        private fun allemand(texte: String): String {
            var regex = """\((.+?)\)""".toRegex()
            if (!texte.contains(regex)) return texte
            val matchResult = regex.find(texte)
            var entreP = matchResult!!.groupValues[1]
            var result = texte.replace(regex, "").trim()
            var aPrononcer = result
            regex = "^der |^das |^die ".toRegex()
            if (result.contains(regex)) {
                result = result.replace(regex, "")
                if (entreP.contains(",")) {
                    entreP = entreP.split(",")[0]
                }
                if (entreP.contains(" ")) {
                    val parts = entreP.split(" ")
                    val accent = parts[0]
                    entreP = parts[1]
                    val lettre = when(accent) {
                        "ä" -> "a"
                        "ö" -> "o"
                        "ü" -> "u"
                        else -> ""
                    }
                    if (lettre.isNotEmpty()) result.replaceFirst(lettre, accent)
                }
                aPrononcer += ", die " + result.replace(regex, "") + entreP
            }

            return aPrononcer
        }

        fun eclate(texte: String, a_prononcer: Boolean): String {
            var texteEclate: String
            val ou = when (DSH.session.langueId()) {
                "it" -> "o"
                "an" -> "or"
                "es" -> "o"
                "po" -> "ou"
                "li" -> "aŭ"
                "oc" -> "o"
                "al" -> "oder"
                else -> "/"
            }
            val tableau = texte.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (DSH.session.langueId() == "al" && a_prononcer) for (i in tableau.indices) {
                tableau[i] = allemand(tableau[i])
            }
            texteEclate = tableau[0]
            for (i in 1 until tableau.size) {
                texteEclate += " " + ou + " " + tableau[i]
            }
            return texteEclate
        }
    }
}
