package com.jmarzin.jmlangues

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleCursorAdapter


class VerbesActivity : AppCompatActivity() {

    private var db: SQLiteDatabase? = null
    private var session: Session? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verbes)
        val dbManager = MyDbHelper(baseContext)
        db = dbManager.writableDatabase
        val selection = SessionContract.SessionTable.COLUMN_NAME_DERNIERE + " = 1"
        session = Session.find_by(db, selection)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setIcon(Utilitaires.drapeau(session!!.langue))

        this.title = "  Verbes"

        val mCursor = Verbe.where(db!!, VerbeContract.VerbeTable.COLUMN_NAME_LANGUE_ID +
                " = \"" + session!!.langue!!.substring(0, 2).toLowerCase() + "\"")

        val adapter = SimpleCursorAdapter(
            this,
            R.layout.ligne_simple,
            mCursor,
            arrayOf(VerbeContract.VerbeTable.COLUMN_NAME_LANGUE),
            intArrayOf(R.id.text1),
            0
        )
        val listView = findViewById<ListView>(R.id.listView)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, pos, id ->
            mCursor.moveToPosition(pos)
            val rowId = mCursor.getInt(mCursor.getColumnIndexOrThrow(VerbeContract.VerbeTable.COLUMN_NAME_ID))
            session!!.verbeId = rowId
            val intent = Intent(baseContext, FormesActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val selection = SessionContract.SessionTable.COLUMN_NAME_DERNIERE + " = 1"
        session = Session.find_by(db, selection)
    }

    override fun onPause() {
        session!!.save(db)
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_autres, menu)
        menu.findItem(R.id.action_verbes).isEnabled = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Utilitaires.traiteMenu(item, this, session!!)

//        val intent: Intent
//        when (item.itemId) {
//            android.R.id.home -> {
//                NavUtils.navigateUpFromSameTask(this)
//                //                finish();
//                return true
//            }
//            .R.id.action_mots -> {
//                intent = Intent(this, MotsActivity::class.java)
//                session.themeId = 0
//                session.motId = 0
//                startActivity(intent)
//                finish()
//                return true
//            }
//            .R.id.action_verbes -> {
//                intent = Intent(this, VerbesActivity::class.java)
//                session.verbeId = 0
//                session.formeId = 0
//                startActivity(intent)
//                finish()
//                return true
//            }
//            .R.id.action_formes -> {
//                intent = Intent(this, FormesActivity::class.java)
//                session.verbeId = 0
//                session.formeId = 0
//                startActivity(intent)
//                finish()
//                return true
//            }
//            .R.id.action_revision -> {
//                intent = Intent(this, RevisionActivity::class.java)
//                startActivity(intent)
//                finish()
//                return true
//            }
//            .R.id.action_statistiques -> {
//                intent = Intent(this, StatsActivity::class.java)
//                startActivity(intent)
//                finish()
//                return true
//            }
//            .R.id.action_parametrage -> {
//                intent = Intent(this, ParametrageActivity::class.java)
//                startActivity(intent)
//                finish()
//                return true
//            }
//        }
        return super.onOptionsItemSelected(item)
    }
}
