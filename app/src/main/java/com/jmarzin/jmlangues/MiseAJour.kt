package com.jmarzin.jmlangues

import android.app.IntentService
import android.content.Intent
import android.content.IntentFilter
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.net.ConnectivityManager
import android.support.v4.content.LocalBroadcastManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray


// Defines a custom Intent action
const val BROADCAST_ACTION = "com.example.android.threadsample.BROADCAST"
// Defines the key for the status "extra" in an Intent

val ACTION_MAJ = "com.jmarzin.jmlangues.action.MAJ"
val ACTION_RETOUR_MAJ = "com.jmarzin.jmlangues.action.RETOUR_MAJ"



/**
 * A constructor is required, and must call the super [android.app.IntentService.IntentService]
 * constructor with a name for the worker thread.
 */

class MiseAJour : IntentService("MiseAJour") {

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */

    private fun envoiMessage(message: String) {
        /*
     * Creates a new Intent containing a Uri object
     * BROADCAST_ACTION is a custom Intent action
     */
        val localIntent = Intent(ACTION_RETOUR_MAJ).apply {
            // Puts the status into the Intent
            putExtra("message", message)
        }
        // Broadcasts the Intent to receivers in this app.
        //LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent)
        applicationContext.sendBroadcast(localIntent)
    }

    override fun onHandleIntent(intent: Intent?) {

        val langue = intent!!.getStringExtra("langue").toLowerCase()
        val langue_id = langue.substring(0,2)
        val text = "Mise à jour " + langue
        val debutHttpNouv = "http://languesapi.jmarzin.fr/" + langue.substring(0, 2) + "/api/"
        var nbTermes = 0
        val dbManager = MyDbHelper(baseContext)
        val db = dbManager.writableDatabase
        val queue = Volley.newRequestQueue(this)
        // Request a string response from the provided URL.

        fun miseAJourObjets(nom: String,
                            url: String,
                            find_max_update : () -> String?,
                            maj_base : (table: JSONArray) -> Triple<Int, Int, Int>) {
            var max: String? = null
            val m = find_max_update()
            if (m != null) {
                max = m
            } else {
                envoiMessage("Je ne sais pas reprendre les $nom")
                return
            }
            var jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url+max, null,
                Response.Listener { response ->
                    val table = response
                    if (table.length() == 0) {
                        envoiMessage("Les $nom sont à jour")
                    } else {
                        val (nbAdd, nbSupp, nbUp) = maj_base(table)
                        envoiMessage("Les $nom sont à jour : +$nbAdd ,-$nbSupp, ≠$nbUp")
                    }
                },
                Response.ErrorListener {
                    envoiMessage("Problème réseau : reprise des $nom impossible")
                })
            queue.add(jsonArrayRequest)
        }

        val url_1 = "${debutHttpNouv}v1/date_maj"
        var stringRequest = StringRequest(Request.Method.GET, url_1,
            Response.Listener { response ->
                val dateMaj = response
                miseAJourObjets("thèmes", "${debutHttpNouv}v2/themes?date=",
                    { -> Theme.find_max_date_update(db, langue_id) },
                    { t: JSONArray -> Theme.maj_base(t, db, langue_id, dateMaj)})
                miseAJourObjets("mots", "${debutHttpNouv}v4/mots?date=",
                    { -> Mot.findMaxDateUpdate(db, langue_id) },
                    { t: JSONArray -> Mot.majBase(t, db, langue_id, dateMaj)})
                miseAJourObjets("verbes", "${debutHttpNouv}v2/verbes?date=",
                    { -> Verbe.find_max_date_update(db, langue_id) },
                    { t: JSONArray -> Verbe.maj_base(t, db, langue_id, dateMaj)})
                miseAJourObjets("temps", "${debutHttpNouv}v1/formestypes?date=",
                    { -> FormeType.findMaxDateUpdate(db, langue_id) },
                    { t: JSONArray -> FormeType.majBase(t, db, langue_id, dateMaj)})
                miseAJourObjets("conjugaisons", "${debutHttpNouv}v2/formes?date=",
                    { -> Forme.findMaxDateUpdate(db, langue_id) },
                    { t: JSONArray -> Forme.majBase(t, db, langue_id, dateMaj)})
            },
            Response.ErrorListener {
                envoiMessage("Problème réseau : mise à jour impossible")
            })
        queue.add(stringRequest)



    }
}