package com.jmarzin.jmlangues

import android.content.ContentValues
import java.util.*

/**
 * Created by jacques on 27/01/15.
 */

class Session {
    var id = 0
    var langue: String? = null
    var derniereSession: Int = 1
    var modeRevision: String? = null
    var poidsMin: Int = 0
    var errMin: Int = 0
    var ageRev: Int = 0
    var nivMax: String = "1"
    var conserveStats: Int = 0
    var parleAuto: Int = 0
    var nbQuestions: Int = 0
    var nbErreurs: Int = 0
    var listeThemes = IntArray(0)
    var listeVerbes = IntArray(0)
    var themeId: Int = 0
    var motId: Int = 0
    var verbeId: Int = 0
    var formeId: Int = 0
    var formeTypeNumero: Int = 0
    var liste = ArrayList<Int>(0)

    fun save() {
        val values = ContentValues()
        values.put(SessionContract.SessionTable.COLUMN_NAME_LANGUE, this.langue)
        values.put(SessionContract.SessionTable.COLUMN_NAME_DERNIERE, this.derniereSession)
        values.put(SessionContract.SessionTable.COLUMN_NAME_MODE_REVISION, this.modeRevision)
        values.put(SessionContract.SessionTable.COLUMN_NAME_POIDS_MIN, poidsMin)
        values.put(SessionContract.SessionTable.COLUMN_NAME_ERR_MIN, errMin)
        values.put(SessionContract.SessionTable.COLUMN_NAME_NIV_MAX, nivMax)
        values.put(SessionContract.SessionTable.COLUMN_NAME_AGE_REV, ageRev)
        values.put(SessionContract.SessionTable.COLUMN_NAME_CONSERVE_STATS, conserveStats)
        values.put(SessionContract.SessionTable.COLUMN_NAME_PARLE_AUTO, parleAuto)
        values.put(SessionContract.SessionTable.COLUMN_NAME_NB_QUESTIONS, nbQuestions)
        values.put(SessionContract.SessionTable.COLUMN_NAME_NB_ERREURS, nbErreurs)
        values.put(SessionContract.SessionTable.COLUMN_NAME_LISTE_THEMES, serialize(listeThemes))
        values.put(SessionContract.SessionTable.COLUMN_NAME_LISTE_VERBES, serialize(listeVerbes))
        values.put(SessionContract.SessionTable.COLUMN_NAME_THEME_ID, themeId)
        values.put(SessionContract.SessionTable.COLUMN_NAME_MOT_ID, motId)
        values.put(SessionContract.SessionTable.COLUMN_NAME_VERBE_ID, verbeId)
        values.put(SessionContract.SessionTable.COLUMN_NAME_FORME_ID, formeId)
        values.put(SessionContract.SessionTable.COLUMN_NAME_FORME_TYPE_NUMERO, formeTypeNumero)
        val listeb = IntArray(liste.size)
        for (i in liste.indices) {
            listeb[i] = liste[i]
        }
        values.put(SessionContract.SessionTable.COLUMN_NAME_LISTE, serialize(listeb))

        if (this.id > 0) {
            val selection = SessionContract.SessionTable.COLUMN_NAME_ID + " = " + id
            DSH.db().update(SessionContract.SessionTable.TABLE_NAME, values, selection, null)
        } else {
            this.id = DSH.db().insert(SessionContract.SessionTable.TABLE_NAME, null, values).toInt()
        }
    }

    fun razCursor() {
        verbeId = 0
        formeId = 0
        formeTypeNumero = 0
        themeId = 0
        motId = 0
    }

    fun getNbTermesListe(): Int {
        val uniqueListe = HashSet<Int>(this.liste)
        return uniqueListe.size
    }

    fun langueId(): String {
        if (id < 1) {
            return ""
        }
        return langue!!.substring(
            0,
            2
        ).toLowerCase(Locale.FRANCE)
    }

    fun drapeau(): Int {
        return when (langue) {
            "Italien" -> R.drawable.italien
            "Anglais" -> R.drawable.anglais
            "Espagnol" -> R.drawable.espagnol
            "Occitan" -> R.drawable.occitan
            "Portugais" -> R.drawable.portugais
            "Allemand" -> R.drawable.allemand
            else -> R.drawable.lingvo
        }
    }

    fun locale(): Locale? {
        return when (langue) {
            "Italien" -> Locale.ITALIAN
            "Anglais" -> Locale.ENGLISH
            "Espagnol" -> Locale("es", "ES")
            "Occitan" -> null
            "Portugais" -> Locale("pt", "PT")
            "Allemand" -> Locale.GERMANY
            else -> null
        }
    }

    fun creerListe() {

        liste = ArrayList()
        if (modeRevision.equals("Vocabulaire") || modeRevision.equals("Mixte")) {
            val c = Mot.selection()
            val id = MotContract.MotTable.COLUMN_NAME_ID
            val poids = MotContract.MotTable.COLUMN_NAME_POIDS
            for (i in 0 until c.count) {
                c.moveToNext()
                val element = c.getInt(c.getColumnIndexOrThrow(id))
                val nb = c.getInt(c.getColumnIndexOrThrow(poids))
                for (j in 1..nb) {
                    DSH.session.liste.add(element)
                }
            }
            c.close()
        }
        if (modeRevision.equals("Conjugaisons") || modeRevision.equals("Mixte")) {
            val c = Forme.selection()
            val id = FormeContract.FormeTable.COLUMN_NAME_ID
            val poids = FormeContract.FormeTable.COLUMN_NAME_POIDS
            for (i in 0 until c.count) {
                c.moveToNext()
                val element = c.getInt(c.getColumnIndexOrThrow(id))
                val nb = c.getInt(c.getColumnIndexOrThrow(poids))
                for (j in 1..nb) {
                    DSH.session.liste.add(-element)
                }
            }
            c.close()
        }
    }

    fun initRevision() {
        if (modeRevision == null) {
            modeRevision = "Vocabulaire"
            creerListe()
            save()
        }
    }

    companion object {

        private fun serialize(content: IntArray): String {
            return content.contentToString()
        }

        private fun myEqualsString(s1: String, s2: String): Boolean {
            if (s1.length != s2.length) {
                return false
            } else {
                for (i in s1.indices) {
                    if (s1[i] != s2[i]) {
                        return false
                    }
                }
            }
            return true
        }

        private fun deserialize(content: String?): IntArray {
            return if (content == null || myEqualsString(content, "[]") || myEqualsString(content, "")) {
                IntArray(0)
            } else {
                val tableauString =
                    content.substring(1, content.length - 1).split(",".toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                val tableauInt = IntArray(tableauString.size)
                for (i in tableauString.indices) {
                    tableauInt[i] = Integer.parseInt(tableauString[i].trim { it <= ' ' })
                }
                tableauInt
            }
        }

        fun findBy(selection: String): Session {
            val mCursor = DSH.db().query(
                SessionContract.SessionTable.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                null
            )
            val session = Session()
            if (mCursor.moveToFirst()) {
                session.id =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_ID))
                session.langue =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_LANGUE))
                session.modeRevision =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_MODE_REVISION))
                session.poidsMin =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_POIDS_MIN))
                session.errMin =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_ERR_MIN))
                session.nivMax =
                    mCursor.getString(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_NIV_MAX))
                session.ageRev =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_AGE_REV))
                session.conserveStats =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_CONSERVE_STATS))
                session.parleAuto =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_PARLE_AUTO))
                session.nbQuestions =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_NB_QUESTIONS))
                session.nbErreurs =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_NB_ERREURS))
                session.listeVerbes =
                    deserialize(mCursor.getString(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_LISTE_VERBES)))
                session.listeThemes =
                    deserialize(mCursor.getString(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_LISTE_THEMES)))
                val listeb =
                    deserialize(mCursor.getString(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_LISTE)))
                session.liste = ArrayList()
                for (i in listeb.indices) {
                    session.liste.add(listeb[i])
                }
                session.themeId =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_THEME_ID))
                session.motId =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_MOT_ID))
                session.verbeId =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_VERBE_ID))
                session.formeId =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_FORME_ID))
                session.formeTypeNumero =
                    mCursor.getInt(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_FORME_TYPE_NUMERO))
            } else {
                session.id = 0
            }
            mCursor.close()
            return session
        }
    }
}
