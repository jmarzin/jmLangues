package com.jmarzin.jmlangues

import android.app.Activity
import android.content.Intent
import android.support.v4.app.NavUtils
import android.view.MenuItem
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by jacques on 28/01/15.
 */
object Utilitaires {

    fun traiteMenu(item: MenuItem, activity: Activity, session: Session): Boolean {
        fun start(intent: Intent) {
            activity.startActivity(intent)
            activity.finish()
        }

        fun razStart(intent: Intent) {
            session.razCursor()
            start(intent)
        }
        when (item.itemId) {
            android.R.id.home -> {
                when {
                    session.themeId != 0 && session.motId == 0 -> start(
                        Intent(
                            activity,
                            ThemesActivity::class.java
                        )
                    )
                    session.formeTypeNumero != 0 && session.formeId == 0 -> start(
                        Intent(
                            activity,
                            FormesTypesActivity::class.java
                        )
                    )
                    session.verbeId != 0 && session.formeId == 0 -> start(
                        Intent(
                            activity,
                            VerbesActivity::class.java
                        )
                    )
                    else -> {
                        session.motId = 0
                        session.formeId = 0
                        NavUtils.navigateUpFromSameTask(activity)
                    }
                }
            }
            R.id.action_themes -> {
                razStart(Intent(activity, ThemesActivity::class.java))
            }
            R.id.action_mots -> {
                razStart(Intent(activity, MotsActivity::class.java))
            }
            R.id.action_verbes -> {
                razStart(Intent(activity, VerbesActivity::class.java))
            }
            R.id.action_formes_types -> {
                razStart(Intent(activity, FormesTypesActivity::class.java))
            }
            R.id.action_formes -> {
                razStart(Intent(activity, FormesActivity::class.java))
            }
            R.id.action_revision -> {
//                intent = Intent(this, RevisionActivity::class.java)
//                startActivity(intent)
//                finish()
            }
            R.id.action_statistiques -> {
//                intent = Intent(this, StatsActivity::class.java)
//                startActivity(intent)
//                finish()
            }
            R.id.action_parametrage -> {
//                intent = Intent(this, ParametrageActivity::class.java)
//                startActivity(intent)
//                finish()
            }
        }
        return true
    }

    fun drapeau(langue: String?): Int {
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

    //
    fun setLocale(langue: String): Locale? {
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

    //
    fun getSelection(session: Session, objets: String): String {
        val date = Timestamp(System.currentTimeMillis())
        date.time = date.time - session.ageRev * 24 * 3600000
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE)
        val dateRev = sdf.format(date)
        val cond1: String
        var cond2 = ""
        var cond3 = ""
        var cond4 = ""
        var cond5 = ""
        var cond6 = ""
        if (objets === "Mots") {
            cond1 =
                MotContract.MotTable.COLUMN_NAME_LANGUE_ID + " = \"" + session.langue!!.substring(
                    0,
                    2
                ).toLowerCase(Locale.FRANCE) + "\""
            cond6 =
                " AND " + MotContract.MotTable.COLUMN_NAME_LANGUE_NIVEAU + " <= \"" + session.nivMax + "\""
            if (session.ageRev != 0) cond2 =
                " AND " + MotContract.MotTable.COLUMN_NAME_DATE_REV + " <= \"" + dateRev + "\""
            if (session.poidsMin > 1) cond3 =
                " AND " + MotContract.MotTable.COLUMN_NAME_POIDS + " >= " + session.poidsMin
            if (session.errMin > 0) cond4 =
                " AND " + MotContract.MotTable.COLUMN_NAME_NB_ERR + " >= " + session.errMin
            if (session.listeThemes.isNotEmpty()) {
                cond5 = " AND " + MotContract.MotTable.COLUMN_NAME_THEME_ID + " IN ("
                for (i in session.listeThemes.indices) {
                    if (i != 0) {
                        cond5 += ","
                    }
                    cond5 += session.listeThemes[i]
                }
                cond5 += ")"
            }
        } else {
            cond1 =
                FormeContract.FormeTable.COLUMN_NAME_LANGUE_ID + " = \"" + session.langue!!.substring(
                    0, 2
                ).toLowerCase(Locale.FRANCE) + "\""
            if (session.ageRev != 0) cond2 =
                " AND " + FormeContract.FormeTable.COLUMN_NAME_DATE_REV + " <= \"" + dateRev + "\""
            if (session.poidsMin > 1) cond3 =
                " AND " + FormeContract.FormeTable.COLUMN_NAME_POIDS + " >= " + session.poidsMin
            if (session.errMin > 0) cond4 =
                " AND " + FormeContract.FormeTable.COLUMN_NAME_NB_ERR + " >= " + session.errMin
            if (session.listeVerbes.isNotEmpty()) {
                cond5 = " AND " + FormeContract.FormeTable.COLUMN_NAME_VERBE_ID + " IN ("
                for (i in session.listeVerbes.indices) {
                    if (i != 0) {
                        cond5 += ","
                    }
                    cond5 += session.listeVerbes[i]
                }
                cond5 += ")"
            }
        }
        return cond1 + cond2 + cond3 + cond4 + cond5 + cond6
    }
//
//    fun creerListe(db: SQLiteDatabase, session: Session) {
//
//        var id: String
//        var poids: String
//        var c: Cursor
//        session.liste = ArrayList()
//        if (session.modeRevision.equals("Vocabulaire") || session.modeRevision.equals("Mixte")) {
//            c = Mot.where(db, getSelection(session, "Mots"))
//            id = MotContract.MotTable.COLUMN_NAME_ID
//            poids = MotContract.MotTable.COLUMN_NAME_POIDS
//            for (i in 0 until c.count) {
//                c.moveToNext()
//                val element = c.getInt(c.getColumnIndexOrThrow(id))
//                val nb = c.getInt(c.getColumnIndexOrThrow(poids))
//                for (j in 1..nb) {
//                    session.liste.add(element)
//                }
//            }
//            c.close()
//        }
//        if (session.modeRevision.equals("Conjugaisons") || session.modeRevision.equals("Mixte")) {
//            c = Forme.where(db, getSelection(session, "Formes"))
//            id = FormeContract.FormeTable.COLUMN_NAME_ID
//            poids = FormeContract.FormeTable.COLUMN_NAME_POIDS
//            for (i in 0 until c.count) {
//                c.moveToNext()
//                val element = c.getInt(c.getColumnIndexOrThrow(id))
//                val nb = c.getInt(c.getColumnIndexOrThrow(poids))
//                for (j in 1..nb) {
//                    session.liste.add(-element)
//                }
//            }
//            c.close()
//        }
//    }
//
//    fun initRevision(db: SQLiteDatabase, session: Session) {
//        if (session.modeRevision == null) {
//            session.modeRevision = "Vocabulaire"
//            creerListe(db, session)
//            session.save(db)
//        }
//    }
}
