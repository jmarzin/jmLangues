package com.jmarzin.jmlangues

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.view.Menu
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleCursorAdapter


class FormesActivity : MesActivites() {

    var localMenu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formes)

        setSupportActionBar(findViewById(R.id.my_toolbar))

        supportActionBar?.setIcon(DSH.session.drapeau())
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
                    FormeContract.FormeTable.COLUMN_NAME_LANGUE_ID + " = \"" + DSH.session.langueId() + "\"" +
                            " AND " + FormeContract.FormeTable.COLUMN_NAME_LANGUE + " LIKE \"" + query + "\""
                mCursor = Forme.where(selection)
                val menuItem = localMenu!!.findItem(R.id.action_search)
                menuItem.collapseActionView()
            }
        } else {
            this.title = "  Formes verbales"
            mCursor = when {
                DSH.session.verbeId > 0 -> Forme.where(
                    FormeContract.FormeTable.COLUMN_NAME_VERBE_ID + " = " + DSH.session.verbeId + " and " +
                            FormeContract.FormeTable.COLUMN_NAME_LANGUE_ID + " = \"" + DSH.session.langueId() + "\"")
                DSH.session.formeTypeNumero > 0 -> Forme.where(
                    FormeContract.FormeTable.COLUMN_NAME_FORME_TYPE_NUMERO + " = " + DSH.session.formeTypeNumero + " and " +
                            MotContract.MotTable.COLUMN_NAME_LANGUE_ID + " = \"" + DSH.session.langueId() + "\"")
                else -> Forme.selection()
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
            DSH.session.formeId =
                mCursor!!.getInt(mCursor!!.getColumnIndexOrThrow(FormeContract.FormeTable.COLUMN_NAME_ID))
            startActivity(Intent(baseContext, FormeActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        localMenu = menu
        super.onCreateOptionsMenu(menu)
        menu.findItem(R.id.action_formes).isEnabled = false
        menu.findItem(R.id.action_search).isVisible = true
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.action_search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(false)
        }
        return true
    }
}
