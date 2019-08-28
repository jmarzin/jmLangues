package com.jmarzin.jmlangues

import android.database.sqlite.SQLiteDatabase

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by jacques on 24/01/15.
 */
abstract class ItemQuestionnable : TermeBase() {
    var dateRev = ""
    var poids = 1
    var nbErr = 0

    internal abstract fun save(db: SQLiteDatabase)

    private fun majDateRev() {
        val date = Timestamp(System.currentTimeMillis())
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE)
        this.dateRev = sdf.format(date)
    }

    fun reduit(db: SQLiteDatabase, session: Session): Int {
        majDateRev()
        val nRemove: Int
        val nouveauPoids: Int
        val facteur: Int = if (this.javaClass.name.contains("Mot")) {
            1
        } else {
            -1
        }
        if (poids == 1) {
            nouveauPoids = 1
            nRemove = 1
        } else {
            nouveauPoids = poids / 2
            nRemove = poids - nouveauPoids
        }
        var valeur: Int?
        for (i in 0 until nRemove) {
            valeur = facteur * id
            session.liste.remove(valeur)
        }
        if (session.errMin > 0 && nbErr > 0) {
            nbErr -= 1
        }
        poids = nouveauPoids
        this.save(db)
        return nouveauPoids
    }

    fun augmente(db: SQLiteDatabase, session: Session): Int {
        majDateRev()
        val facteur: Int = if (this.javaClass.name.contains("Mot")) {
            1
        } else {
            -1
        }
        val nouveauPoids = poids * 2
        val nAjout = nouveauPoids - poids
        for (i in 0 until nAjout) {
            session.liste.add(facteur * id)
        }
        nbErr += 1
        poids = nouveauPoids
        this.save(db)
        return nouveauPoids
    }
}
