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
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.support.v4.widget.SearchViewCompat.setOnQueryTextListener
import android.support.v4.app.SupportActivity
import android.support.v4.app.SupportActivity.ExtraData
import android.icu.lang.UCharacter.GraphemeClusterBreak.T






class MotsActivity : AppCompatActivity() {

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
            this.title = "  Mots trouvés"
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                val selection =
                    MotContract.MotTable.COLUMN_NAME_LANGUE_ID + " = \"" + session!!.langue!!.substring(
                        0,
                        2
                    ).toLowerCase() + "\"" +
                            " AND (" + MotContract.MotTable.COLUMN_NAME_FRANCAIS + " LIKE \"" + query + "\"" +
                            " OR " + MotContract.MotTable.COLUMN_NAME_LANGUE + " LIKE \"" + query + "\"" +
                            " OR " + MotContract.MotTable.COLUMN_NAME_MOT_DIRECTEUR + " LIKE \"" + query + "\")"
                mCursor = Mot.where(db!!, selection)
            }
        } else {
            this.title = "  Mots"
            if (session!!.themeId > 0) {
                val arguments = arrayOf("" + session!!.themeId)
                mCursor = Mot.where(
                    db!!,
                    MotContract.MotTable.COLUMN_NAME_THEME_ID + " = " + session!!.themeId + " and " +
                            MotContract.MotTable.COLUMN_NAME_LANGUE_ID + " = \"" + session!!.langue!!.substring(
                        0,
                        2
                    ).toLowerCase() + "\""
                )
            } else {
                mCursor = Mot.where(db!!, Utilitaires.getSelection(session!!, "Mots"))
            }
        }

        //val mCursor = Mot.where(db!!, "langue_id = \"" + session!!.langue!!.substring(0, 2).toLowerCase() + "\"")

        val adapter = SimpleCursorAdapter(
            this,
            R.layout.ligne_simple,
            mCursor,
            arrayOf(MotContract.MotTable.COLUMN_NAME_FRANCAIS),
            intArrayOf(R.id.text1),
            0
        )
        val listView = findViewById<ListView>(R.id.listView)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, pos, id ->
            mCursor!!.moveToPosition(pos)
            val rowId = mCursor!!.getInt(mCursor!!.getColumnIndexOrThrow("_id"))
            session!!.motId = rowId
            val intent = Intent(baseContext, MotActivity::class.java)
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
        menu.findItem(R.id.action_mots).isEnabled = false
        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.action_search).actionView as SearchView).apply {
            // Assumes current activity is the searchable activity
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(false) // Do not iconify the widget; expand it by default
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Utilitaires.traiteMenu(item, this, session!!)
        return true
        //return super.onOptionsItemSelected(item)
    }
}
