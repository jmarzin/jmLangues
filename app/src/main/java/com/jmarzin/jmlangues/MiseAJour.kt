package com.jmarzin.jmlangues

import android.app.IntentService
import android.content.Intent
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import java.util.*

const val ACTION_RETOUR_MAJ = "com.jmarzin.jmlangues.action.RETOUR_MAJ"


class MiseAJour : IntentService("MiseAJour") {

    private fun envoiMessage(message: String) {

        val localIntent = Intent(ACTION_RETOUR_MAJ).apply {
            putExtra("message", message)
        }
        applicationContext.sendBroadcast(localIntent)
    }

    override fun onHandleIntent(intent: Intent?) {

        val langue = intent!!.getStringExtra("langue").toLowerCase(Locale.FRANCE)
        val langueId = langue.substring(0, 2)
        val debutHttpNouv = "http://languesapi.jmarzin.fr/" + langue.substring(0, 2) + "/api/"
        val dbManager = MyDbHelper(baseContext)
        val db = dbManager.writableDatabase
        val queue = Volley.newRequestQueue(this)

        fun miseAJourObjets(
            nom: String,
            url: String,
            find_max_update: () -> String?,
            maj_base: (table: JSONArray) -> Triple<Int, Int, Int>
        ) {
            val max = find_max_update()
            val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url + max, null,
                Response.Listener { response ->
                    if (response.length() == 0) {
                        envoiMessage("Les $nom sont à jour")
                    } else {
                        val (nbAdd, nbSupp, nbUp) = maj_base(response)
                        envoiMessage("Les $nom sont à jour : +$nbAdd ,-$nbSupp, ≠$nbUp")
                    }
                },
                Response.ErrorListener {
                    envoiMessage("Problème réseau : reprise des $nom impossible")
                })
            queue.add(jsonArrayRequest)
        }

        val url1 = "${debutHttpNouv}v1/date_maj"
        val stringRequest = StringRequest(Request.Method.GET, url1,
            Response.Listener { response ->
                miseAJourObjets("thèmes", "${debutHttpNouv}v2/themes?date=",
                    { Theme.findMaxDateUpdate(db, langueId) },
                    { t: JSONArray -> Theme.majBase(t, db, langueId, response) })
                miseAJourObjets("mots", "${debutHttpNouv}v4/mots?date=",
                    { Mot.findMaxDateUpdate(db, langueId) },
                    { t: JSONArray -> Mot.majBase(t, db, langueId, response) })
                miseAJourObjets("verbes", "${debutHttpNouv}v2/verbes?date=",
                    { Verbe.findMaxDateUpdate(db, langueId) },
                    { t: JSONArray -> Verbe.majBase(t, db, langueId, response) })
                miseAJourObjets("temps", "${debutHttpNouv}v1/formestypes?date=",
                    { FormeType.findMaxDateUpdate(db, langueId) },
                    { t: JSONArray -> FormeType.majBase(t, db, langueId, response) })
                miseAJourObjets("conjugaisons", "${debutHttpNouv}v2/formes?date=",
                    { Forme.findMaxDateUpdate(db, langueId) },
                    { t: JSONArray -> Forme.majBase(t, db, langueId, response) })
            },
            Response.ErrorListener {
                envoiMessage("Problème réseau : mise à jour impossible")
            })
        queue.add(stringRequest)


    }
}