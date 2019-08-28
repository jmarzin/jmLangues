package com.jmarzin.jmlangues

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import java.util.*


class ParametrageActivity : AppCompatActivity() {
    var db: SQLiteDatabase? = null
    var session: Session = Session()
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
        val dbManager = MyDbHelper(baseContext)
        db = dbManager.writableDatabase
        session = Session.findBy(db, SessionContract.SessionTable.COLUMN_NAME_DERNIERE + " = 1")

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setIcon(Utilitaires.drapeau(session.langue))

        this.title = "  Paramétrage"
        Utilitaires.initRevision(db!!, session)
        mtPoidsmin = findViewById(R.id.t_poidsMin)
        mtPoidsmin!!.text = session.poidsMin.toString()
        mtAgemin = findViewById(R.id.t_ageMin)
        mtAgemin!!.text = session.ageRev.toString()
        mtErrmin = findViewById(R.id.t_errMin)
        mtErrmin!!.text = session.errMin.toString()
        mtNivmax = findViewById(R.id.t_nivMax)
        mtNivmax!!.text = session.nivMax
        mtConservestats = findViewById(R.id.t_conserveStats)
        mtConservestats!!.isChecked = session.conserveStats == 1
        mtParleauto = findViewById(R.id.t_parleAuto)
        mtParleauto!!.isChecked = session.parleAuto == 1

        mRadioVocabulaire =
            findViewById(R.id.t_vocabulaire)
        mRadioConjugaisons =
            findViewById(R.id.t_conjugaisons)
        mRadioMixte = findViewById(R.id.t_mixte)
        when {
            session.modeRevision.equals("Vocabulaire") -> mRadioVocabulaire!!.isChecked = true
            session.modeRevision.equals("Conjugaisons") -> mRadioConjugaisons!!.isChecked = true
            else -> mRadioMixte!!.isChecked = true
        }

        onChangeChoix(mRadioVocabulaire)

        var c =
            Theme.where(
                db!!,
                "langue_id = \"" + session.langue!!.substring(
                    0,
                    2
                ).toLowerCase(Locale.FRANCE) + "\""
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
            db!!,
            "langue_id = \"" + session.langue!!.substring(0, 2).toLowerCase(Locale.FRANCE) + "\""
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
        val tableauIdThemesSel = session.listeThemes
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
        val tableauIdVerbesSel = session.listeVerbes
        Arrays.sort(tableauIdVerbesSel)
        for (i in tableauIdVerbes!!.indices) {
            if (Arrays.binarySearch(tableauIdVerbesSel, tableauIdVerbes!![i]) >= 0) {
                lVerbes!!.setItemChecked(i, true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        session = Session.findBy(db, SessionContract.SessionTable.COLUMN_NAME_DERNIERE + " = 1")
    }

    override fun onPause() {
        super.onPause()
        session.save(db)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_autres, menu)
        menu.findItem(R.id.action_parametrage).isEnabled = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Utilitaires.traiteMenu(item, this, session)
        return super.onOptionsItemSelected(item)
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
        session.conserveStats = 1
    } else {
        session.conserveStats = 0
    }

    fun onChangeParleAuto(view: View) = if (mtParleauto!!.isChecked) {
        session.parleAuto = 1
    } else {
        session.parleAuto = 0
    }

    fun onPrepareListe(view: View) {
        if (mtPoidsmin!!.text.toString().isEmpty()) {
            mtPoidsmin!!.text = "0"
        }
        session.poidsMin = Integer.parseInt(mtPoidsmin!!.text.toString())
        if (mtAgemin!!.text.toString().isEmpty()) {
            mtAgemin!!.text = "0"
        }
        session.ageRev = Integer.parseInt(mtAgemin!!.text.toString())
        if (mtErrmin!!.text.toString().isEmpty()) {
            mtErrmin!!.text = "0"
        }
        session.errMin = Integer.parseInt(mtErrmin!!.text.toString())
        if (mtNivmax!!.text.toString().isEmpty()) {
            mtNivmax!!.text = "1"
        }
        session.nivMax = mtNivmax!!.text.toString()
        when {
            mRadioVocabulaire!!.isChecked -> session.modeRevision = "Vocabulaire"
            mRadioConjugaisons!!.isChecked -> session.modeRevision = "Conjugaisons"
            else -> session.modeRevision = "Mixte"
        }

        val tableauIdThemesChecked = IntArray(lThemes!!.checkedItemCount)
        var j = 0
        for (i in 0 until lThemes!!.count) {
            if (lThemes!!.isItemChecked(i)) {
                tableauIdThemesChecked[j] = tableauIdThemes!![i]
                j++
            }
        }
        session.listeThemes = tableauIdThemesChecked

        val tableauIdVerbesChecked = IntArray(lVerbes!!.checkedItemCount)
        j = 0
        for (i in 0 until lVerbes!!.count) {
            if (lVerbes!!.isItemChecked(i)) {
                tableauIdVerbesChecked[j] = tableauIdVerbes!![i]
                j++
            }
        }
        session.listeVerbes = tableauIdVerbesChecked

        Utilitaires.creerListe(db!!, session)
        val sousTitre =
            "  " + session.getNbTermesListe() + " terme(s) (" + session.liste.size + ")"
        supportActionBar!!.subtitle = sousTitre
    }
}
