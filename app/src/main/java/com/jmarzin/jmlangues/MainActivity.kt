package com.jmarzin.jmlangues

import android.app.Activity
import android.app.SearchManager
import android.content.*
import android.database.sqlite.SQLiteDatabase
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    var db: SQLiteDatabase? = null
    private var session = Session()
    private var dejaMaj = false
    private var receiver: BroadcastReceiver? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.title = "jmLangues"
        dejaMaj = false
        val dbManager = MyDbHelper(baseContext)
        db = dbManager.writableDatabase
        setContentView(R.layout.activity_main)
        receiver = RetourServiceMaj()
        val filter = IntentFilter(ACTION_RETOUR_MAJ)
        this.registerReceiver(receiver, filter)
    }

    override fun onResume() {
        super.onResume()
        val selection = SessionContract.SessionTable.COLUMN_NAME_DERNIERE + " = 1"
        session = Session.find_by(db, selection)
        if (session._id < 1) {
            session.langue = getString(R.string.titre_langue)
        }
        val mTexteLangue = findViewById(R.id.t_langue) as TextView
        mTexteLangue.text = session.langue
        session.razCursor()
    }

    override fun onPause() {
        session.save(db)
        super.onPause()
    }

    override fun onDestroy() {
        this.unregisterReceiver(receiver)
        super.onDestroy()
    }

    fun afficheMessage(texte: String) {
        val text = texte
        val duration = Toast.LENGTH_LONG
        val message = Toast.makeText(applicationContext, text, duration)
        message.setGravity(Gravity.TOP, 0, 0)
        message.show()
    }

    fun changeLangue(langue: String) {
        val mTexteLangue = findViewById(com.jmarzin.jmlangues.R.id.t_langue) as TextView
        if (mTexteLangue.text == langue) {
            return
        } else {
            session.derniereSession = 0
            session.save(db)
            val selection = SessionContract.SessionTable.COLUMN_NAME_LANGUE + " = \"" + langue + "\""
            session = Session.find_by(db, selection)
            if (session._id == 0) {
                session = Session()
                session.langue = langue
            }
            dejaMaj = false
            mTexteLangue.text = langue
            lanceActivite(null) // pour ne faire que la mise à jour
        }
    }

    fun clickDrapeauItalien(view: View) {
        changeLangue(getString(com.jmarzin.jmlangues.R.string.Italien))
    }

    fun clickDrapeauAnglais(view: View) {
        changeLangue(getString(com.jmarzin.jmlangues.R.string.Anglais))
    }

    fun clickDrapeauEspagnol(view: View) {
        changeLangue(getString(com.jmarzin.jmlangues.R.string.Espagnol))
    }

    fun clickDrapeauPortugais(view: View) {
        changeLangue(getString(com.jmarzin.jmlangues.R.string.Portugais))
    }

    fun clickDrapeauOccitan(view: View) {
        changeLangue("Occitan")
    }

    fun clickDrapeauLingvo(view: View) {
        changeLangue(getString(com.jmarzin.jmlangues.R.string.Lingvo))
    }

    fun clickDrapeauAllemand(view: View) {
        changeLangue(getString(com.jmarzin.jmlangues.R.string.Allemand))
    }

    private fun Oklangue(): Boolean {
        //val mTexteLangue = findViewById(R.id.t_langue) as TextView

        val contenu = getString(R.string.titre_langue)
        if (this.title == contenu) {
            afficheMessage(getString(R.string.erreurChoixLangue))
            return false
        } else {
            return true
        }
    }

    fun lanceActivite(intent: Intent?) {
        if (!dejaMaj) {
            dejaMaj = true
            val connMgr = baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connMgr.getActiveNetworkInfo()
            val duration = Toast.LENGTH_LONG
            if (networkInfo != null && networkInfo!!.isConnected()) {
                val intentService = Intent(baseContext, MiseAJour::class.java)
                intentService.putExtra("langue", session.langue)
                startService(intentService)
            } else {
                afficheMessage(getString(R.string.pasDeReseau))
            }
        }
        intent?.let {
            startActivity(intent)}
    }

    fun clickThemes(view: View) {
        if (Oklangue()) {
            val intent = Intent(this, ThemesActivity::class.java)
            session.themeId = 0
            session.motId = 0
            lanceActivite(intent)
        }
    }

    fun clickMots(view: View) {
        if (Oklangue()) {
            val intent = Intent(this, MotsActivity::class.java)
            session.themeId = 0
            session.motId = 0
            lanceActivite(intent)
        }
    }

    fun clickVerbes(view: View) {
        if (Oklangue()) {
            val intent = Intent(this, VerbesActivity::class.java)
            session.verbeId = 0
            session.formeId = 0
            lanceActivite(intent)
        }
    }

    fun clickFormesTypes(view: View) {
        if (Oklangue()) {
            val intent = Intent(this, FormesTypesActivity::class.java)
            session.verbeId = 0
            session.formeId = 0
            lanceActivite(intent)
        }
    }

    fun clickFormes(view: View) {
        if (Oklangue()) {
            val intent = Intent(this, FormesActivity::class.java)
            session.verbeId = 0
            session.formeId = 0
            lanceActivite(intent)
        }
    }

    fun clickRevision(view: View) {
        if (Oklangue()) {
            val intent = null // Intent(this, RevisionActivity::class.java)
            lanceActivite(intent)
        }
    }

    fun clickStatistiques(view: View) {
        if (Oklangue()) {
            val intent = null // Intent(this, StatsActivity::class.java)
            lanceActivite(intent)
        }
    }

    fun clickParametrage(view: View) {
        if (Oklangue()) {
            val intent = null // Intent(this, ParametrageActivity::class.java)
            lanceActivite(intent)
        }
    }

    fun clickQuitter(view: View) {
        if (session.conserveStats == 0) {
            session.nbQuestions = 0
            session.nbErreurs = 0
        }
        dejaMaj = false
        finish()
    }
}
