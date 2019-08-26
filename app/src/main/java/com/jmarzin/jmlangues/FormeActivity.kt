package com.jmarzin.jmlangues

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.jmarzin.jmlangues.R.string.en
import java.util.*


class FormeActivity : AppCompatActivity() {

    private var db: SQLiteDatabase? = null
    private var session: Session? = null
    private var ttobj: TextToSpeech? = null
    private var locale: Locale? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forme)
        val dbManager = MyDbHelper(baseContext)
        db = dbManager.writableDatabase
        val selection = SessionContract.SessionTable.COLUMN_NAME_DERNIERE + " = 1"
        session = Session.findBy(db, selection)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setIcon(Utilitaires.drapeau(session!!.langue))

        locale = Utilitaires.setLocale(session!!.langue!!)

        this.title = "  Forme verbale"
        val forme = Forme.find(db!!, session!!.formeId)

        val tId = findViewById<TextView>(R.id.t_id)
        tId.text = session!!.formeId.toString()
        val lLangue = findViewById<TextView>(R.id.l_langue)
        lLangue.text = getString(en, session!!.langue)

        if (forme != null) {

            val tLangueId = findViewById<TextView>(R.id.t_langue_id)
            tLangueId.text = forme.langueId

            val tVerbeLangue =
                findViewById<TextView>(R.id.t_verbe_langue)
            if (forme.verbe != null) {
                tVerbeLangue.text = forme.verbe!!.langue
            } else {
                tVerbeLangue.text = ""
            }
            val tForme = findViewById<TextView>(R.id.t_forme)
            if (forme.formeType != null) {
                tForme.text = forme.formeType!!.langue
            } else {
                tForme.text = ""
            }

            val tLangue = findViewById<TextView>(R.id.t_langue)
            tLangue.text = forme.langue

            val tDateRev = findViewById<TextView>(R.id.t_date_rev)
            tDateRev.text = forme.dateRev

            val tPoids = findViewById<TextView>(R.id.t_poids)
            tPoids.text = forme.poids.toString()

            val tNberr = findViewById<TextView>(R.id.t_nberr)
            tNberr.text = forme.nbErr.toString()

            val tDistId = findViewById<TextView>(R.id.t_dist_id)
            tDistId.text = forme.distId.toString()

            val tDateMaj = findViewById<TextView>(R.id.t_date_maj)
            tDateMaj.text = forme.dateMaj

            ttobj = TextToSpeech(
                applicationContext,
                TextToSpeech.OnInitListener { status ->
                    if (status != TextToSpeech.ERROR) {
                        if (locale != null) {
                            ttobj!!.language = locale
                            ttobj!!.speak(forme.langue, TextToSpeech.QUEUE_ADD, null, forme.langue)
                        }
                    }
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        session = Session.findBy(db, SessionContract.SessionTable.COLUMN_NAME_DERNIERE + " = 1")
    }

    override fun onPause() {
        session!!.save(db)
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

