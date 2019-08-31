package com.jmarzin.jmlangues

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.sqlite.SQLiteDatabase
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    var db2: SQLiteDatabase? = null
    private var dejaMaj = false
    private var receiver: BroadcastReceiver? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.title = "jmLangues"
        dejaMaj = false

        DSH.ouvreSession(applicationContext)
        setContentView(R.layout.activity_main)
        receiver = RetourServiceMaj()
        val filter = IntentFilter(ACTION_RETOUR_MAJ)
        this.registerReceiver(receiver, filter)
    }

    override fun onResume() {
        super.onResume()
        val mTexteLangue = findViewById<TextView>(R.id.t_langue)
        mTexteLangue.text = DSH.session.langue
        DSH.session.razCursor()
    }

    override fun onPause() {
        DSH.session.save()
        super.onPause()
    }

    override fun onDestroy() {
        this.unregisterReceiver(receiver)
        DSH.fermeSession()
        super.onDestroy()
    }

    private fun afficheMessage(texte: String) {
        val duration = Toast.LENGTH_LONG
        val message = Toast.makeText(applicationContext, texte, duration)
        message.setGravity(Gravity.TOP, 0, 0)
        message.show()
    }

    private fun changeLangue(langue: String) {
        val mTexteLangue = findViewById<TextView>(R.id.t_langue)
        if (mTexteLangue.text == langue) {
            return
        } else {
            DSH.session.derniereSession = 0
            DSH.session.save()
            val selection =
                SessionContract.SessionTable.COLUMN_NAME_LANGUE + " = \"" + langue + "\""
            DSH.session = Session.findBy(selection)
            if (DSH.session.id == 0) {
                DSH.session = Session()
                DSH.session.langue = langue
            }
            dejaMaj = false
            mTexteLangue.text = langue
            lanceActivite(null) // pour ne faire que la mise à jour
        }
    }

    fun clickDrapeauItalien(view: View) = changeLangue(getString(R.string.Italien))

    fun clickDrapeauAnglais(view: View) = changeLangue(getString(R.string.Anglais))

    fun clickDrapeauEspagnol(view: View) = changeLangue(getString(R.string.Espagnol))

    fun clickDrapeauPortugais(view: View) = changeLangue(getString(R.string.Portugais))

    fun clickDrapeauOccitan(view: View) = changeLangue(getString(R.string.Occitan))

    fun clickDrapeauLingvo(view: View) = changeLangue(getString(R.string.Lingvo))

    fun clickDrapeauAllemand(view: View) = changeLangue(getString(R.string.Allemand))

    private fun okLangue(): Boolean {
        return if (this.title == getString(R.string.titre_langue)) {
            afficheMessage(getString(R.string.erreurChoixLangue))
            false
        } else {
            true
        }
    }

    private fun lanceActivite(intent: Intent?) {
        if (!dejaMaj) {
            dejaMaj = true
            val connMgr =
                baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo
            if (networkInfo != null && networkInfo.isConnected) {
                val intentService = Intent(baseContext, MiseAJour::class.java)
                intentService.putExtra("langue", DSH.session.langue)
                startService(intentService)
            } else {
                afficheMessage(getString(R.string.pasDeReseau))
            }
        }
        intent?.let {
            startActivity(intent)
        }
    }

    fun clickThemes(view: View) {
        if (okLangue()) lanceActivite(Intent(this, ThemesActivity::class.java))
    }

    fun clickMots(view: View) {
        if (okLangue()) lanceActivite(Intent(this, MotsActivity::class.java))
    }

    fun clickVerbes(view: View) {
        if (okLangue()) lanceActivite(Intent(this, VerbesActivity::class.java))
    }

    fun clickFormesTypes(view: View) {
        if (okLangue()) lanceActivite(Intent(this, FormesTypesActivity::class.java))
    }

    fun clickFormes(view: View) {
        if (okLangue()) lanceActivite(Intent(this, FormesActivity::class.java))
    }

    fun clickRevision(view: View) {
        if (okLangue()) lanceActivite(Intent(this, RevisionActivity::class.java))
    }

    fun clickStatistiques(view: View) {
        if (okLangue()) lanceActivite(Intent(this, StatsActivity::class.java))
    }

    fun clickParametrage(view: View) {
        if (okLangue()) lanceActivite(Intent(this, ParametrageActivity::class.java))
    }

    fun clickQuitter(view: View) {
        if (DSH.session.conserveStats == 0) {
            DSH.session.nbQuestions = 0
            DSH.session.nbErreurs = 0
        }
        dejaMaj = false
        finish()
    }
}
