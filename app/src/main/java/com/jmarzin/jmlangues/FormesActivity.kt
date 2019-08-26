package com.jmarzin.jmlangues

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import java.util.*


class FormesActivity : AppCompatActivity() {

    private var db: SQLiteDatabase? = null
    private var session: Session? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formes)
        val dbManager = MyDbHelper(baseContext)
        db = dbManager.writableDatabase
        val selection = SessionContract.SessionTable.COLUMN_NAME_DERNIERE + " = 1"
        session = Session.findBy(db, selection)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setIcon(Utilitaires.drapeau(session!!.langue))
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        var mCursor: Cursor? = null
        // Verify the action and get the query
        if (Intent.ACTION_SEARCH == intent.action) {
            this.title = "  Formes trouvées"
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                val selection =
                    FormeContract.FormeTable.COLUMN_NAME_LANGUE_ID + " = \"" + session!!.langue!!.substring(
                        0,
                        2
                    ).toLowerCase(Locale.FRANCE) + "\"" +
                            " AND " + FormeContract.FormeTable.COLUMN_NAME_LANGUE + " LIKE \"" + query + "\""
                mCursor = Mot.where(db!!, selection)
            }
        } else {
            this.title = "  Formes verbales"
            mCursor = when {
                session!!.verbeId > 0 -> Forme.where(
                    db!!,
                    FormeContract.FormeTable.COLUMN_NAME_VERBE_ID + " = " + session!!.verbeId + " and " +
                            FormeContract.FormeTable.COLUMN_NAME_LANGUE_ID + " = \"" + session!!.langue!!.substring(
                        0, 2).toLowerCase(Locale.FRANCE) + "\"")
                session!!.formeTypeNumero > 0 -> Forme.where(
                    db!!,
                    FormeContract.FormeTable.COLUMN_NAME_FORME_TYPE_NUMERO + " = " + session!!.formeTypeNumero + " and " +
                            MotContract.MotTable.COLUMN_NAME_LANGUE_ID + " = \"" + session!!.langue!!.substring(
                        0, 2).toLowerCase(Locale.FRANCE) + "\"")
                else -> Forme.where(db!!, Utilitaires.getSelection(session!!, "Formes"))
            }
        }

        val adapter = SimpleCursorAdapter(
            this,
            R.layout.ligne_simple,
            mCursor,
            arrayOf(FormeContract.FormeTable.COLUMN_NAME_LANGUE),
            intArrayOf(R.id.text1),
            0
        )
        val listView = findViewById<ListView>(R.id.listView)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, pos, _ ->
            mCursor!!.moveToPosition(pos)
            session!!.formeId = mCursor!!.getInt(mCursor!!.getColumnIndexOrThrow(FormeContract.FormeTable.COLUMN_NAME_ID))
            startActivity(Intent(baseContext, FormeActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        val selection = SessionContract.SessionTable.COLUMN_NAME_DERNIERE + " = 1"
        session = Session.findBy(db, selection)
    }

    override fun onPause() {
        session!!.save(db)
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_autres, menu)
        menu.findItem(R.id.action_formes).isEnabled = false
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.action_search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(false)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Utilitaires.traiteMenu(item, this, session!!)
        return super.onOptionsItemSelected(item)
    }
}
