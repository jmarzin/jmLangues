package com.jmarzin.jmlangues

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.KeyEvent
import android.view.Menu
import android.view.View
import android.widget.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*




class RevisionActivity : MesActivites() {

    private var question: Question? = null
    private var aleatoire: Random = Random()
    private var ttobj: TextToSpeech? = null
    private var imSpeaker: ImageButton? = null
    private var dateCourante = ""
    private var stats: Stats? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_revision)
        val date = Timestamp(System.currentTimeMillis())
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE)
        dateCourante = sdf.format(date)

        val cond =
            StatsContract.StatsTable.COLUMN_NAME_LANGUE_ID + " = \"" + DSH.session.langueId() + "\" AND " +
                    StatsContract.StatsTable.COLUMN_NAME_DATE + " = \"" + dateCourante + "\""
        stats = Stats.findBy(cond)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setIcon(DSH.session.drapeau())

        this.title = "  Révision"

        DSH.session.initRevision()

        val mReponse = findViewById<EditText>(R.id.reponse)
        mReponse.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                val mBouton = findViewById<Button>(R.id.boutonVerifierAutre)
                mBouton.callOnClick()
                return@OnKeyListener true //return@OnKeyListener true
            }
            false
        })

        poseQuestion()
    }

    override fun onResume() {
        super.onResume()
        ttobj = TextToSpeech(
            applicationContext,
            TextToSpeech.OnInitListener { status ->
                if (status != TextToSpeech.ERROR) {
                    if (DSH.session.locale() != null) {
                        ttobj!!.language = DSH.session.locale()
                    }
                }
            }
        )
    }

    override fun onPause() {
        if (ttobj != null) {
            ttobj!!.stop()
            ttobj!!.shutdown()
        }
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menu.findItem(R.id.action_revision).isEnabled = false
        return true
    }

    private fun ajusteSousTitre() {
        var sousTitre = "   "
        if (DSH.session.nbQuestions > 0) {
            sousTitre += DSH.session.nbErreurs.toString() + " E, " + DSH.session.nbQuestions +
                    " Q (" + DSH.session.nbErreurs * 100 / DSH.session.nbQuestions + " %), "
        }
        sousTitre += "R " + DSH.session.getNbTermesListe() + " (" + DSH.session.liste.size + ")"
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
        question = Question(aleatoire)
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
        val aPrononcer = Mot.eclate(question!!.item!!.langue, true)
        @Suppress("DEPRECATION")
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
            DSH.session.nbQuestions++
            if (egalite(mReponse.text.toString().trim(), question!!.item!!.langue)) {
                val mBravo = findViewById<TextView>(R.id.bravoOuEchec)
                mBravo.text = getString(R.string.Bravo)
                mBravo.setTextColor(-0x1fb34fb)
                mtexteReponse.setTextColor(-0x1fb34fb)
                nouveauPoids = question!!.item!!.reduit()
                majStats(true)

            } else {
                DSH.session.nbErreurs++
                val mBravo = findViewById<TextView>(R.id.bravoOuEchec)
                mBravo.text = getString(R.string.Erreur)
                mBravo.setTextColor(-0x134fbfd)
                mtexteReponse.setTextColor(-0x134fbfd)
                nouveauPoids = question!!.item!!.augmente()
                majStats(false)
            }
            texte += " ($nouveauPoids)"
            mtexteReponse.text = Mot.eclate(texte, false)
            mPrononciation.text = question!!.prononciation
            mBouton.text = getString(R.string.Autre_question)
            if (DSH.session.locale() != null) {
                imSpeaker!!.visibility = View.VISIBLE
            }
            ajusteSousTitre()
            if (DSH.session.parleAuto == 1) {
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

    private fun majStats(resultat: Boolean) {
        if (stats == null) {
            stats = Stats()
            stats!!.dateRev = dateCourante
            stats!!.langueId = DSH.session.langueId()
        }

        if (question!!.item is Mot) {
            stats!!.nbQuestionsMots = stats!!.nbQuestionsMots.plus(1)
            stats!!.nbErreursMots = stats!!.nbErreursMots.plus(if (resultat) 0 else 1)
        } else {
            stats!!.nbQuestionsFormes = stats!!.nbQuestionsFormes.plus(1)
            stats!!.nbErreursFormes = stats!!.nbErreursFormes.plus(if (resultat) 0 else 1)
        }

        stats!!.save()
    }
}
