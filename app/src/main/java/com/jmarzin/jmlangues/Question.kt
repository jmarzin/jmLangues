package com.jmarzin.jmlangues

import android.database.sqlite.SQLiteDatabase
import java.util.*

/**
 * Created by jacques on 24/01/15.
 */
class Question(db: SQLiteDatabase, session: Session, aleatoire: Random) {

    var item: ItemQuestionnable? = null
    var ligne1 = ""
    var ligne2 = ""
    var prononciation = ""

    init {
        fun suppIdTire(idTire: Int) {
            @Suppress("ControlFlowWithEmptyBody")
            while (session.liste.remove(idTire)) {
            }
        }
        this.item = null
        while (session.liste.size !== 0 && this.item == null) {
            val idTire = session.liste[aleatoire.nextInt(session.liste.size)]
            when {
                idTire > 0 -> {
                    val mot = Mot.find(db, idTire)
                    if (mot != null) {
                        this.item = mot
                        if (mot.theme != null) {
                            this.ligne1 = mot.theme.langue
                        } else {
                            this.ligne1 = ""
                        }
                        this.ligne2 = mot.francais
                        this.prononciation = mot.pronunciation
                    } else {
                        suppIdTire(idTire)
                    }
                }
                else -> {
                    val forme = Forme.find(db, -idTire)
                    if (forme != null) {
                        this.item = forme
                        if (forme.verbe != null) {
                            this.ligne1 = forme.verbe!!.langue
                        } else {
                            this.ligne1 = ""
                        }
                        this.ligne2 = forme.formeType!!.langue
                    } else {
                        suppIdTire(idTire)
                    }
                }
            }
        }
    }
}
