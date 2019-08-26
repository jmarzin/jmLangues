package com.jmarzin.jmlangues

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView

import java.util.Locale


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
        session = Session.find_by(db, selection)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setIcon(Utilitaires.drapeau(session!!.langue))

        locale = Utilitaires.setLocale(session!!.langue!!)

        this.title = "  Mot"
        val mot = Mot.find(db!!, session!!.motId)

        val t_id = findViewById(R.id.t_id) as TextView
        t_id.text = "" + session!!.motId
        val l_langue = findViewById(R.id.l_langue) as TextView
        l_langue.text = "en " + session!!.langue

        if (mot != null) {

            val t_langue_id = findViewById(R.id.t_langue_id) as TextView
            t_langue_id.setText(mot.langueId)

            val t_theme_langue =
                findViewById(R.id.t_theme_langue) as TextView
            if (mot.theme != null) {
                t_theme_langue.text = mot.theme.langue
            } else {
                t_theme_langue.text = ""
            }

            val t_francais = findViewById(R.id.t_francais) as TextView
            t_francais.text = mot.francais

            val t_directeur = findViewById(R.id.t_directeur) as TextView
            t_directeur.text = mot.mot_directeur

            val t_langue = findViewById(R.id.t_langue) as TextView
            t_langue.text = mot.langue

            val t_pronunciation =
                findViewById(R.id.t_pronunciation) as TextView
            t_pronunciation.text = mot.pronunciation

            val t_langue_niveau =
                findViewById(R.id.t_langue_niveau) as TextView
            t_langue_niveau.text = mot.langue_niveau

            val t_date_rev = findViewById(R.id.t_date_rev) as TextView
            t_date_rev.setText(mot.dateRev)

            val t_poids = findViewById(R.id.t_poids) as TextView
            t_poids.text = "" + mot.poids

            val t_nberr = findViewById(R.id.t_nberr) as TextView
            t_nberr.text = "" + mot.nbErr

            val t_dist_id = findViewById(R.id.t_dist_id) as TextView
            t_dist_id.text = "" + mot.distId

            val t_date_maj = findViewById(R.id.t_date_maj) as TextView
            t_date_maj.setText(mot.dateMaj)

            ttobj = TextToSpeech(
                applicationContext,
                TextToSpeech.OnInitListener { status ->
                    if (status != TextToSpeech.ERROR) {
                        if (locale != null) {
                            ttobj!!.language = locale
                            ttobj!!.speak(mot.langue, TextToSpeech.QUEUE_FLUSH, null)
                        }
                    }
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        val selection = SessionContract.SessionTable.COLUMN_NAME_DERNIERE + " = 1"
        session = Session.find_by(db, selection)
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

