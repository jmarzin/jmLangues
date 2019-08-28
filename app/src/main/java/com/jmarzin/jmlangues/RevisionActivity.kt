package com.jmarzin.jmlangues

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


class RevisionActivity : AppCompatActivity() {

    var db: SQLiteDatabase? = null
    var session = Session()
    private var question: Question? = null
    private var aleatoire: Random = Random()
    private var ttobj: TextToSpeech? = null
    private var locale: Locale? = null
    private var imSpeaker: ImageButton? = null
    private var dateCourante = ""
    private var stats: Stats? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_revision)
        val dbManager = MyDbHelper(baseContext)
        db = dbManager.writableDatabase
        session = Session.findBy(db, SessionContract.SessionTable.COLUMN_NAME_DERNIERE + " = 1")
        val date = Timestamp(System.currentTimeMillis())
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE)
        dateCourante = sdf.format(date)

        val cond =
            StatsContract.StatsTable.COLUMN_NAME_LANGUE_ID + " = \"" + session.langue!!.substring(
                0,
                2
            ).toLowerCase(Locale.FRANCE) + "\" AND " + StatsContract.StatsTable.COLUMN_NAME_DATE + " = \"" + dateCourante + "\""
        stats = Stats.findBy(db!!, cond)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setIcon(Utilitaires.drapeau(session.langue))

        locale = Utilitaires.setLocale(session.langue!!)

        this.title = "  Révision"

        Utilitaires.initRevision(db!!, session)
        poseQuestion()
    }

    override fun onResume() {
        super.onResume()
        session = Session.findBy(db, SessionContract.SessionTable.COLUMN_NAME_DERNIERE + " = 1")
        ttobj = TextToSpeech(
            applicationContext,
            TextToSpeech.OnInitListener { status ->
                if (status != TextToSpeech.ERROR) {
                    if (locale != null) {
                        ttobj!!.language = locale
                    }
                }
            }
        )
    }

    override fun onPause() {
        session.save(db)
        if (ttobj != null) {
            ttobj!!.stop()
            ttobj!!.shutdown()
        }
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_autres, menu)
        menu.findItem(R.id.action_revision).isEnabled = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Utilitaires.traiteMenu(item, this, session)
        return super.onOptionsItemSelected(item)
    }

    private fun ajusteSousTitre() {
        var sousTitre = "   "
        if (session.nbQuestions > 0) {
            sousTitre += session.nbErreurs.toString() + " E, " + session.nbQuestions +
                    " Q (" + session.nbErreurs * 100 / session.nbQuestions + " %), "
        }
        sousTitre += "R " + session.getNbTermesListe() + " (" + session.liste.size + ")"
        supportActionBar?.subtitle = sousTitre
    }

    private fun poseQuestion() {
        ajusteSousTitre()
        val mBravo = findViewById<TextView>(R.id.bravoOuEchec)
        val mligne1 = findViewById<TextView>(R.id.ligne1Question)
        val mligne2 = findViewById<TextView>(R.id.ligne2Question)
        val mBouton = findViewById<Button>(R.id.boutonVerifierAutre)
        val mtexteReponse = findViewById<TextView>(R.id.texteReponse)
        val mPrononciation = findViewById<TextView>(R.id.prononciation)
        val mReponse = findViewById<EditText>(R.id.reponse)
        val mzoneQuestion = findViewById<TableLayout>(R.id.zoneQuestion)
        imSpeaker = findViewById(R.id.im_speaker)
        question = Question(db!!, session, aleatoire)
        imSpeaker!!.visibility = View.INVISIBLE
        if (question!!.item == null) {
            mBravo.text = getString(R.string.Plus_de_questions)
            mBravo.setTextColor(-0x1000000)
            mligne1.text = getString(R.string.Recharge)
            mligne2.text = getString(R.string.Allez)
            mzoneQuestion.visibility = View.INVISIBLE
            mBouton.visibility = View.INVISIBLE
        } else {
            mBravo.text = ""
            mligne1.text = question!!.ligne1
            mligne2.text = question!!.ligne2
            mBouton.text = getString(R.string.Verifier)
            mtexteReponse.text = ""
            mPrononciation.text = ""
            mReponse.setText("")
        }
    }

    fun clickSpeaker(view: View) {
        val aPrononcer = eclate(question!!.item!!.langue)
        ttobj!!.speak(aPrononcer, TextToSpeech.QUEUE_ADD, null)
    }

    fun clickBouton(view: View) {
        val mBouton = findViewById<Button>(R.id.boutonVerifierAutre)

        if (mBouton.text == "Vérifier") {
            val mtexteReponse =
                findViewById<TextView>(R.id.texteReponse)
            val mPrononciation = findViewById<TextView>(R.id.prononciation)
            var texte = question!!.item!!.langue

            val nouveauPoids: Int
            val mReponse = findViewById<EditText>(R.id.reponse)
            session.nbQuestions++
            if (egalite(mReponse.text.toString(), question!!.item!!.langue)) {
                val mBravo = findViewById<TextView>(R.id.bravoOuEchec)
                mBravo.text = getString(R.string.Bravo)
                mBravo.setTextColor(-0x1fb34fb)
                mtexteReponse.setTextColor(-0x1fb34fb)
                nouveauPoids = question!!.item!!.reduit(db!!, session)
                majStats(true)

            } else {
                session.nbErreurs++
                val mBravo = findViewById<TextView>(R.id.bravoOuEchec)
                mBravo.text = getString(R.string.Erreur)
                mBravo.setTextColor(-0x134fbfd)
                mtexteReponse.setTextColor(-0x134fbfd)
                nouveauPoids = question!!.item!!.augmente(db!!, session)
                majStats(false)
            }
            texte += " ($nouveauPoids)"
            mtexteReponse.text = eclate(texte)
            mPrononciation.text = question!!.prononciation
            mBouton.text = getString(R.string.Autre_question)
            if (locale != null) {
                imSpeaker!!.visibility = View.VISIBLE
            }
            ajusteSousTitre()
            if (session.parleAuto == 1) {
                clickSpeaker(view)
            }
        } else {
            poseQuestion()
        }
    }

    private fun egalite(reponse: String, reponse_attendue: String): Boolean {
        for (retval in reponse_attendue.toLowerCase(Locale.FRANCE).split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            if (retval == reponse.toLowerCase(Locale.FRANCE)) {
                return true
            }
        }
        return false
    }

    private fun eclate(texte: String): String {
        var texteEclate: String
        val ou = when (question!!.item!!.langueId) {
            "it" -> "o"
            "an" -> "or"
            "es" -> "o"
            "po" -> "ou"
            "li" -> "aŭ"
            "oc" -> "o"
            "al" -> "oder"
            else -> "/"
        }
        val tableau = texte.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        texteEclate = tableau[0]
        for (i in 1 until tableau.size) {
            texteEclate += " " + ou + " " + tableau[i]
        }
        return texteEclate
    }

    private fun majStats(resultat: Boolean) {
        if (stats == null) {
            stats = Stats()
            stats!!.dateRev = dateCourante
            stats!!.langueId = session.langue!!.substring(0, 2).toLowerCase(Locale.FRANCE)
        }

        if (question!!.item is Mot) {
            stats!!.nbQuestionsMots = stats!!.nbQuestionsMots.plus(1)
            stats!!.nbErreursMots = stats!!.nbErreursMots.plus(if (resultat) 0 else 1)
        } else {
            stats!!.nbQuestionsFormes = stats!!.nbQuestionsFormes.plus(1)
            stats!!.nbErreursFormes = stats!!.nbErreursFormes.plus(if (resultat) 0 else 1)
        }

        stats!!.save(db!!)
    }
}
