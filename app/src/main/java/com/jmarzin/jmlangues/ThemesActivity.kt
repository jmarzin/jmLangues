package com.jmarzin.jmlangues

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleCursorAdapter


class ThemesActivity : MesActivites() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_themes)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setIcon(DSH.session.drapeau())

        this.title = "  Thèmes"

        val mCursor = Theme.where(
            ThemeContract.ThemeTable.COLUMN_NAME_LANGUE_ID +
                    " = \"" + DSH.session.langueId() + "\""
        )

        val adapter = SimpleCursorAdapter(
            this,
            R.layout.ligne_liste,
            mCursor,
            arrayOf(
                ThemeContract.ThemeTable.COLUMN_NAME_NUMERO,
                ThemeContract.ThemeTable.COLUMN_NAME_LANGUE
            ),
            intArrayOf(R.id.text1, R.id.text2),
            0
        )
        val listView = findViewById<ListView>(R.id.listView)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, pos, _ ->
            mCursor.moveToPosition(pos)
            val rowId =
                mCursor.getInt(mCursor.getColumnIndexOrThrow(ThemeContract.ThemeTable.COLUMN_NAME_DIST_ID))
            DSH.session.themeId = rowId
            val intent = Intent(baseContext, MotsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menu.findItem(R.id.action_themes).isEnabled = false
        return true
    }
}
