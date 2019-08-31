package com.jmarzin.jmlangues

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.TextView
import com.jmarzin.jmlangues.R.string.en


class FormeActivity : MesActivites() {

    private var ttobj: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forme)

        setSupportActionBar(findViewById(R.id.my_toolbar))

        supportActionBar?.setIcon(DSH.session.drapeau())

        this.title = "  Forme verbale"
        val forme = Forme.find(DSH.session.formeId)

        val tId = findViewById<TextView>(R.id.t_id)
        tId.text = DSH.session.formeId.toString()
        val lLangue = findViewById<TextView>(R.id.l_langue)
        lLangue.text = getString(en, DSH.session.langue)

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
                        if (DSH.session.locale() != null) {
                            ttobj!!.language = DSH.session.locale()
                            @Suppress("DEPRECATION")
                            ttobj!!.speak(forme.langue, TextToSpeech.QUEUE_ADD, null)
                        }
                    }
                }
            )
        }
    }
}

