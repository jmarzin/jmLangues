package com.jmarzin.jmlangues

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.*
import java.util.*


class ParametrageActivity : MesActivites() {
    private var mtPoidsmin: TextView? = null
    private var mtErrmin: TextView? = null
    private var mtAgemin: TextView? = null
    private var mtNivmax: TextView? = null
    private var mtConservestats: Switch? = null
    private var mtParleauto: Switch? = null
    private var mRadioVocabulaire: RadioButton? = null
    private var mRadioConjugaisons: RadioButton? = null
    private var mRadioMixte: RadioButton? = null
    private var mLayoutThemes: LinearLayout? = null
    private var mLayoutVerbes: LinearLayout? = null
    private var lThemes: ListView? = null
    private var lVerbes: ListView? = null
    private var tableauIdThemes: IntArray? = null
    private var tableauThemes: Array<String?> = arrayOfNulls(0)
    private var tableauIdVerbes: IntArray? = null
    private var tableauVerbes: Array<String?> = arrayOfNulls(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parametrage)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setIcon(DSH.session.drapeau())

        this.title = "  Paramétrage"
        DSH.session.initRevision()
        mtPoidsmin = findViewById(R.id.t_poidsMin)
        mtPoidsmin!!.text = DSH.session.poidsMin.toString()
        mtAgemin = findViewById(R.id.t_ageMin)
        mtAgemin!!.text = DSH.session.ageRev.toString()
        mtErrmin = findViewById(R.id.t_errMin)
        mtErrmin!!.text = DSH.session.errMin.toString()
        mtNivmax = findViewById(R.id.t_nivMax)
        mtNivmax!!.text = DSH.session.nivMax
        mtConservestats = findViewById(R.id.t_conserveStats)
        mtConservestats!!.isChecked = DSH.session.conserveStats == 1
        mtParleauto = findViewById(R.id.t_parleAuto)
        mtParleauto!!.isChecked = DSH.session.parleAuto == 1

        mRadioVocabulaire =
            findViewById(R.id.t_vocabulaire)
        mRadioConjugaisons =
            findViewById(R.id.t_conjugaisons)
        mRadioMixte = findViewById(R.id.t_mixte)
        when {
            DSH.session.modeRevision.equals("Vocabulaire") -> mRadioVocabulaire!!.isChecked = true
            DSH.session.modeRevision.equals("Conjugaisons") -> mRadioConjugaisons!!.isChecked = true
            else -> mRadioMixte!!.isChecked = true
        }

        onChangeChoix(mRadioVocabulaire)

        var c =
            Theme.where(
                "langue_id = \"" + DSH.session.langueId() + "\""
            )
        tableauThemes = arrayOfNulls(c.count)
        tableauIdThemes = IntArray(c.count)
        (0 until c.count).forEach { i ->
            c.moveToNext()
            tableauIdThemes!![i] =
                c.getInt(c.getColumnIndexOrThrow(ThemeContract.ThemeTable.COLUMN_NAME_DIST_ID))
            tableauThemes[i] =
                c.getString(c.getColumnIndexOrThrow(ThemeContract.ThemeTable.COLUMN_NAME_LANGUE))
        }

        c = Verbe.where(
            "langue_id = \"" + DSH.session.langueId() + "\""
        )
        tableauVerbes = arrayOfNulls(c.count)
        tableauIdVerbes = IntArray(c.count)
        (0 until c.count).forEach { i ->
            c.moveToNext()
            tableauIdVerbes!![i] =
                c.getInt(c.getColumnIndexOrThrow(VerbeContract.VerbeTable.COLUMN_NAME_DIST_ID))
            tableauVerbes[i] =
                c.getString(c.getColumnIndexOrThrow(VerbeContract.VerbeTable.COLUMN_NAME_LANGUE))
        }

        lThemes = findViewById(R.id.l_themes)
        val themesAdapter =
            ArrayAdapter(this, R.layout.choix_multiple, tableauThemes)

        lThemes!!.adapter = themesAdapter
        val tableauIdThemesSel = DSH.session.listeThemes
        Arrays.sort(tableauIdThemesSel)
        for (i in tableauIdThemes!!.indices) {
            if (Arrays.binarySearch(tableauIdThemesSel, tableauIdThemes!![i]) >= 0) {
                lThemes!!.setItemChecked(i, true)
            }
        }
        lVerbes = findViewById(R.id.l_verbes)
        val verbesAdapter =
            ArrayAdapter(this, R.layout.choix_multiple, tableauVerbes)
        lVerbes!!.adapter = verbesAdapter
        val tableauIdVerbesSel = DSH.session.listeVerbes
        Arrays.sort(tableauIdVerbesSel)
        for (i in tableauIdVerbes!!.indices) {
            if (Arrays.binarySearch(tableauIdVerbesSel, tableauIdVerbes!![i]) >= 0) {
                lVerbes!!.setItemChecked(i, true)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menu.findItem(R.id.action_parametrage).isEnabled = false
        return true
    }

    fun onChangeChoix(view: View?) {
        mRadioVocabulaire =
            findViewById(R.id.t_vocabulaire)
        mRadioConjugaisons = findViewById(R.id.t_conjugaisons)
        mLayoutThemes = findViewById(R.id.layout_themes)
        mLayoutVerbes = findViewById(R.id.layout_verbes)
        when {
            mRadioVocabulaire!!.isChecked -> {
                mLayoutThemes!!.visibility = View.VISIBLE
                mLayoutVerbes!!.visibility = View.GONE
            }
            mRadioConjugaisons!!.isChecked -> {
                mLayoutThemes!!.visibility = View.GONE
                mLayoutVerbes!!.visibility = View.VISIBLE
            }
            else -> {
                mLayoutThemes!!.visibility = View.VISIBLE
                mLayoutVerbes!!.visibility = View.VISIBLE
            }
        }
    }

    fun onChangeConserveStats(view: View) = if (mtConservestats!!.isChecked) {
        DSH.session.conserveStats = 1
    } else {
        DSH.session.conserveStats = 0
    }

    fun onChangeParleAuto(view: View) = if (mtParleauto!!.isChecked) {
        DSH.session.parleAuto = 1
    } else {
        DSH.session.parleAuto = 0
    }

    fun onPrepareListe(view: View) {
        if (mtPoidsmin!!.text.toString().isEmpty()) {
            mtPoidsmin!!.text = "0"
        }
        DSH.session.poidsMin = Integer.parseInt(mtPoidsmin!!.text.toString())
        if (mtAgemin!!.text.toString().isEmpty()) {
            mtAgemin!!.text = "0"
        }
        DSH.session.ageRev = Integer.parseInt(mtAgemin!!.text.toString())
        if (mtErrmin!!.text.toString().isEmpty()) {
            mtErrmin!!.text = "0"
        }
        DSH.session.errMin = Integer.parseInt(mtErrmin!!.text.toString())
        if (mtNivmax!!.text.toString().isEmpty()) {
            mtNivmax!!.text = "1"
        }
        DSH.session.nivMax = mtNivmax!!.text.toString()
        when {
            mRadioVocabulaire!!.isChecked -> DSH.session.modeRevision = "Vocabulaire"
            mRadioConjugaisons!!.isChecked -> DSH.session.modeRevision = "Conjugaisons"
            else -> DSH.session.modeRevision = "Mixte"
        }

        val tableauIdThemesChecked = IntArray(lThemes!!.checkedItemCount)
        var j = 0
        for (i in 0 until lThemes!!.count) {
            if (lThemes!!.isItemChecked(i)) {
                tableauIdThemesChecked[j] = tableauIdThemes!![i]
                j++
            }
        }
        DSH.session.listeThemes = tableauIdThemesChecked

        val tableauIdVerbesChecked = IntArray(lVerbes!!.checkedItemCount)
        j = 0
        for (i in 0 until lVerbes!!.count) {
            if (lVerbes!!.isItemChecked(i)) {
                tableauIdVerbesChecked[j] = tableauIdVerbes!![i]
                j++
            }
        }
        DSH.session.listeVerbes = tableauIdVerbesChecked

        DSH.session.creerListe()
        val sousTitre =
            "  " + DSH.session.getNbTermesListe() + " terme(s) (" + DSH.session.liste.size + ")"
        supportActionBar!!.subtitle = sousTitre
    }
}
