package com.jmarzin.jmlangues

import android.content.Context
import android.database.sqlite.SQLiteDatabase

/*

    cet objet gère la base de données et la session, communes à toutes les activités

 */

object DSH {
    var db: SQLiteDatabase? = null
    var session: Session = Session()

    init {
    }

    fun ouvreSession(context: Context) {
        val dbManager = MyDbHelper(context)
        db = dbManager.writableDatabase
        session = Session.findBy(SessionContract.SessionTable.COLUMN_NAME_DERNIERE + " = 1")
        if (session.id < 1) {
            session.langue = context.getString(R.string.titre_langue)
        }
    }

    fun fermeSession() = db!!.close()

    fun db(): SQLiteDatabase = db!! // pour éviter les !!

}