package com.jmarzin.jmlangues

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

    internal abstract fun save()

    private fun majDateRev() {
        val date = Timestamp(System.currentTimeMillis())
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE)
        this.dateRev = sdf.format(date)
    }

    fun reduit(): Int {
        majDateRev()
        val nRemove: Int
        val nouveauPoids: Int
        val facteur: Int = if (this is Mot) {
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
            DSH.session.liste.remove(valeur)
        }
        if (DSH.session.errMin > 0 && nbErr > 0) {
            nbErr -= 1
        }
        poids = nouveauPoids
        this.save()
        return nouveauPoids
    }

    fun augmente(): Int {
        majDateRev()
        val facteur: Int = if (this is Mot) {
            1
        } else {
            -1
        }
        val nouveauPoids = poids * 2
        val nAjout = nouveauPoids - poids
        for (i in 0 until nAjout) {
            DSH.session.liste.add(facteur * id)
        }
        nbErr += 1
        poids = nouveauPoids
        this.save()
        return nouveauPoids
    }
}
