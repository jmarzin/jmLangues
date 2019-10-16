package com.jmarzin.jmlangues

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleCursorAdapter


class FormesTypesActivity : MesActivites() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formes_types)

        setSupportActionBar(findViewById(R.id.my_toolbar))

        supportActionBar?.setIcon(DSH.session.drapeau())

        this.title = "  Temps"

        val mCursor = FormeType.listeAvecNbFormes()

        val adapter = SimpleCursorAdapter(
            this,
            R.layout.ligne_liste_avec_nombre,
            mCursor,
            arrayOf(
                FormeTypeContract.FormeTypeTable.COLUMN_NAME_NUMERO,
                FormeTypeContract.FormeTypeTable.COLUMN_NAME_LANGUE,
                "nombre"
            ),
            intArrayOf(R.id.text1, R.id.text2, R.id.nombre),
            0
        )

        supportActionBar?.subtitle = "   " + mCursor.count.toString() + " temps"

        val listView = findViewById<ListView>(R.id.listView)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, pos, _ ->
            mCursor.moveToPosition(pos)
            DSH.session.formeTypeNumero = mCursor.getInt(mCursor.getColumnIndexOrThrow(FormeTypeContract.FormeTypeTable.COLUMN_NAME_NUMERO))
            startActivity(Intent(baseContext, FormesActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menu.findItem(R.id.action_formes_types).isEnabled = false
        return true
    }
}
