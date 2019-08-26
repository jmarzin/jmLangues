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
import java.util.*


class ThemesActivity : AppCompatActivity() {

    private var db: SQLiteDatabase? = null
    private var session: Session? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_themes)
        val dbManager = MyDbHelper(baseContext)
        db = dbManager.writableDatabase
        session = Session.findBy(db, SessionContract.SessionTable.COLUMN_NAME_DERNIERE + " = 1")

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setIcon(Utilitaires.drapeau(session!!.langue))

        this.title = "  Thèmes"

        val mCursor = Theme.where(
            db!!, ThemeContract.ThemeTable.COLUMN_NAME_LANGUE_ID +
                    " = \"" + session!!.langue!!.substring(0, 2).toLowerCase(Locale.FRANCE) + "\""
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
            session!!.themeId = rowId
            val intent = Intent(baseContext, MotsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        session = Session.findBy(db, SessionContract.SessionTable.COLUMN_NAME_DERNIERE + " = 1")
    }

    override fun onPause() {
        session!!.save(db)
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_autres, menu)
        menu.findItem(R.id.action_themes).isEnabled = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Utilitaires.traiteMenu(item, this, session!!)
        return super.onOptionsItemSelected(item)
    }
}
