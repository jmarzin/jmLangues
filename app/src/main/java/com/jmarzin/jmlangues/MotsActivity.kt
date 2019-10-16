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


class MotsActivity : MesActivites() {

    private var localMenu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mots)

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
        if (Intent.ACTION_SEARCH == intent.action) {
            this.title = "  Mots trouvés"
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                val selection =
                    MotContract.MotTable.COLUMN_NAME_LANGUE_ID + " = \"" + DSH.session.langueId() + "\"" +
                            " AND (" + MotContract.MotTable.COLUMN_NAME_FRANCAIS + " LIKE \"" + query + "\"" +
                            " OR " + MotContract.MotTable.COLUMN_NAME_LANGUE + " LIKE \"" + query + "\"" +
                            " OR " + MotContract.MotTable.COLUMN_NAME_MOT_DIRECTEUR + " LIKE \"" + query + "\")"
                mCursor = Mot.where(selection)
                val menuItem = localMenu!!.findItem(R.id.action_search)
                menuItem.collapseActionView()
            }
        } else {
            this.title = "  Mots"
            mCursor = if (DSH.session.themeId > 0) {
                Mot.where(
                    MotContract.MotTable.COLUMN_NAME_THEME_ID + " = " + DSH.session.themeId + " and " +
                            MotContract.MotTable.COLUMN_NAME_LANGUE_ID + " = \"" + DSH.session.langueId() + "\""
                )
            } else {
                Mot.selection()
            }
        }

        val adapter = SimpleCursorAdapter(
            this,
            R.layout.ligne_liste_avec_nombre,
            mCursor,
            arrayOf(MotContract.MotTable.COLUMN_NAME_FRANCAIS),
            intArrayOf(R.id.text2),
            0
        )

        supportActionBar?.subtitle = "   " + mCursor?.count.toString() + " mot(s)"

        val listView = findViewById<ListView>(R.id.listView)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener {_ , _, pos, _ ->
            mCursor!!.moveToPosition(pos)
            DSH.session.motId = mCursor!!.getInt(mCursor!!.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_ID))
            startActivity(Intent(baseContext, MotActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        localMenu = menu
        super.onCreateOptionsMenu(menu)
        menu.findItem(R.id.action_mots).isEnabled = false
        menu.findItem(R.id.action_search).isVisible = true
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.action_search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(false)
        }
        return true
    }
}
