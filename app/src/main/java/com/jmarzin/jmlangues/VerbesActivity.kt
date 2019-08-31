package com.jmarzin.jmlangues

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleCursorAdapter


class VerbesActivity : MesActivites() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verbes)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setIcon(DSH.session.drapeau())

        this.title = "  Verbes"

        val mCursor = Verbe.where(
            VerbeContract.VerbeTable.COLUMN_NAME_LANGUE_ID +
                    " = \"" + DSH.session.langueId() + "\""
        )

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

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, pos, _ ->
            mCursor.moveToPosition(pos)
            DSH.session.verbeId =
                mCursor.getInt(mCursor.getColumnIndexOrThrow(VerbeContract.VerbeTable.COLUMN_NAME_DIST_ID))
            startActivity(Intent(baseContext, FormesActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menu.findItem(R.id.action_verbes).isEnabled = false
        return true
    }
}
