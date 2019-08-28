package com.jmarzin.jmlangues

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import java.util.*


class MotActivity : AppCompatActivity() {

    private var db: SQLiteDatabase? = null
    private var session: Session? = null
    private var ttobj: TextToSpeech? = null
    private var locale: Locale? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mot)

        val dbManager = MyDbHelper(baseContext)
        db = dbManager.writableDatabase
        val selection = SessionContract.SessionTable.COLUMN_NAME_DERNIERE + " = 1"
        session = Session.findBy(db, selection)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setIcon(Utilitaires.drapeau(session!!.langue))

        locale = Utilitaires.setLocale(session!!.langue!!)

        this.title = "  Mot"
        val mot = Mot.find(db!!, session!!.motId)

        val tId = findViewById<TextView>(R.id.t_id)
        tId.text = session!!.motId.toString()
        val lLangue = findViewById<TextView>(R.id.l_langue)
        lLangue.text = getString(R.string.en, session!!.langue)

        if (mot != null) {

            val tLangueId = findViewById<TextView>(R.id.t_langue_id)
            tLangueId.text = mot.langueId

            val tThemeLangue =
                findViewById<TextView>(R.id.t_theme_langue)
            if (mot.theme.id == 0) {
                tThemeLangue.text = mot.theme.langue
            } else {
                tThemeLangue.text = ""
            }

            val tFrancais = findViewById<TextView>(R.id.t_francais)
            tFrancais.text = mot.francais

            val tDirecteur = findViewById<TextView>(R.id.t_directeur)
            tDirecteur.text = mot.motDirecteur

            val tLangue = findViewById<TextView>(R.id.t_langue)
            tLangue.text = mot.langue

            val tPronunciation =
                findViewById<TextView>(R.id.t_pronunciation)
            tPronunciation.text = mot.pronunciation

            val tLangueNiveau =
                findViewById<TextView>(R.id.t_langue_niveau)
            tLangueNiveau.text = mot.langueNiveau

            val tDateRev = findViewById<TextView>(R.id.t_date_rev)
            tDateRev.text = mot.dateRev

            val tPoids = findViewById<TextView>(R.id.t_poids)
            tPoids.text = mot.poids.toString()

            val tNberr = findViewById<TextView>(R.id.t_nberr)
            tNberr.text = mot.nbErr.toString()

            val tDistId = findViewById<TextView>(R.id.t_dist_id)
            tDistId.text = mot.distId.toString()

            val tDateMaj = findViewById<TextView>(R.id.t_date_maj)
            tDateMaj.text = mot.dateMaj

            ttobj = TextToSpeech(
                applicationContext,
                TextToSpeech.OnInitListener { status ->
                    if (status != TextToSpeech.ERROR) {
                        if (locale != null) {
                            ttobj!!.language = locale
                            ttobj!!.speak(mot.langue, TextToSpeech.QUEUE_ADD, null)
                        }
                    }
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        val selection = SessionContract.SessionTable.COLUMN_NAME_DERNIERE + " = 1"
        session = Session.findBy(db, selection)
    }

    override fun onPause() {
        session!!.save(db)
        if (ttobj != null) {
            ttobj!!.stop()
            ttobj!!.shutdown()
        }
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_autres, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Utilitaires.traiteMenu(item, this, session!!)
        return super.onOptionsItemSelected(item)
    }
}

