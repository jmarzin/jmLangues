package com.jmarzin.jmlangues

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.TextView


class MotActivity : MesActivites() {

    private var ttobj: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mot)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setIcon(DSH.session.drapeau())

        this.title = "  Mot"
        val mot = Mot.find(DSH.session.motId)

        val tId = findViewById<TextView>(R.id.t_id)
        tId.text = DSH.session.motId.toString()
        val lLangue = findViewById<TextView>(R.id.l_langue)
        lLangue.text = getString(R.string.en, DSH.session.langue)

        if (mot != null) {

            val tLangueId = findViewById<TextView>(R.id.t_langue_id)
            tLangueId.text = mot.langueId

            val tThemeLangue =
                findViewById<TextView>(R.id.t_theme_langue)
            if (mot.theme.id > 0) {
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
                        if (DSH.session.locale() != null) {
                            ttobj!!.language = DSH.session.locale()
                            @Suppress("DEPRECATION")
                            ttobj!!.speak(Mot.eclate(mot.langue, true), TextToSpeech.QUEUE_ADD, null)
                        }
                    }
                }
            )
        }
    }
}

