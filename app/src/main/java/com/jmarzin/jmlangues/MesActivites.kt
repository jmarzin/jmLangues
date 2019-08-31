package com.jmarzin.jmlangues

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

open class MesActivites: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(DSH.session.id == 0) {
            DSH.ouvreSession(applicationContext)
        }
    }

    override fun onPause() {
        DSH.session.save()
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_autres, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        fun start(intent: Intent) {
            startActivity(intent)
            finish()
        }

        fun razStart(intent: Intent) {
            DSH.session.razCursor()
            start(intent)
        }
        when (item.itemId) {
            android.R.id.home -> {
                when {
                    DSH.session.themeId != 0 && DSH.session.motId == 0 -> start(
                        Intent(
                            this,
                            ThemesActivity::class.java
                        )
                    )
                    DSH.session.formeTypeNumero != 0 && DSH.session.formeId == 0 -> start(
                        Intent(
                            this,
                            FormesTypesActivity::class.java
                        )
                    )
                    DSH.session.verbeId != 0 && DSH.session.formeId == 0 -> start(
                        Intent(
                            this,
                            VerbesActivity::class.java
                        )
                    )
                    else -> {
                        DSH.session.motId = 0
                        DSH.session.formeId = 0
                        NavUtils.navigateUpFromSameTask(this)
                    }
                }
            }
            R.id.action_themes -> {
                razStart(Intent(this, ThemesActivity::class.java))
            }
            R.id.action_mots -> {
                razStart(Intent(this, MotsActivity::class.java))
            }
            R.id.action_verbes -> {
                razStart(Intent(this, VerbesActivity::class.java))
            }
            R.id.action_formes_types -> {
                razStart(Intent(this, FormesTypesActivity::class.java))
            }
            R.id.action_formes -> {
                razStart(Intent(this, FormesActivity::class.java))
            }
            R.id.action_revision -> {
                razStart(Intent(this, RevisionActivity::class.java))
            }
            R.id.action_statistiques -> {
                razStart(Intent(this, StatsActivity::class.java))
            }
            R.id.action_parametrage -> {
                razStart(Intent(this, ParametrageActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}