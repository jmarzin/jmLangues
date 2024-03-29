package com.jmarzin.jmlangues

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

class StatsActivity : MesActivites() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setIcon(DSH.session.drapeau())

        this.title = "  Statistiques"

        val mCursor =
            Stats.where("langue_id = \"" + DSH.session.langueId() + "\"")

        val graph = findViewById<GraphView>(R.id.graph)

        val questions = LineGraphSeries<DataPoint>()
        questions.color = Color.GREEN
        val erreurs = LineGraphSeries<DataPoint>()
        erreurs.color = Color.RED
        var point: DataPoint
        var formatter = SimpleDateFormat("yyyy-MM-d", Locale.FRANCE)
        var datedebut: Date? = null
        var datefin: Date? = null
        var dateprec: Date? = null
        var nbpoints = mCursor.count
        var max = 0
        for (i in 0 until mCursor.count) {
            mCursor.moveToNext()
            val dateS =
                mCursor.getString(mCursor.getColumnIndexOrThrow(StatsContract.StatsTable.COLUMN_NAME_DATE))
            var date = Date()
            try {
                date = formatter.parse(dateS)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            if (i == 0) {
                datedebut = Date(date.time)
            } else if (i == mCursor.count - 1) {
                datefin = Date(date.time)
            }
            if (dateprec != null) {
                var j = dateprec.time + 24 * 3600000
                while (j < date.time) {
                    nbpoints++
                    dateprec.time = j
                    point = DataPoint(dateprec, 0.0)
                    questions.appendData(point, true, nbpoints)
                    erreurs.appendData(point, true, nbpoints)
                    j += (24 * 3600000).toLong()
                }
            }
            val nbQuestionsMots =
                mCursor.getInt(mCursor.getColumnIndexOrThrow(StatsContract.StatsTable.COLUMN_NAME_NB_QUESTIONS_MOTS))
            val nbErreursMots =
                mCursor.getInt(mCursor.getColumnIndexOrThrow(StatsContract.StatsTable.COLUMN_NAME_NB_ERREURS_MOTS))
            val nbQuestionsFormes =
                mCursor.getInt(mCursor.getColumnIndexOrThrow(StatsContract.StatsTable.COLUMN_NAME_NB_QUESTIONS_FORMES))
            val nbErreursFormes =
                mCursor.getInt(mCursor.getColumnIndexOrThrow(StatsContract.StatsTable.COLUMN_NAME_NB_ERREURS_FORMES))
            point = DataPoint(date, (nbQuestionsMots + nbQuestionsFormes).toDouble())
            questions.appendData(point, true, nbpoints)
            point = DataPoint(date, (nbErreursFormes + nbErreursMots).toDouble())
            erreurs.appendData(point, true, nbpoints)
            dateprec = Date(date.time)
            max = max(max, nbQuestionsFormes + nbQuestionsMots)
        }
        graph.addSeries(questions)
        graph.addSeries(erreurs)
        formatter = SimpleDateFormat("dd/MM", Locale.FRANCE)
        graph.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(baseContext, formatter)

//        val staticLabelsFormatter = StaticLabelsFormatter(graph)
        if (datedebut != null && datefin == null) datefin = Date(datedebut.time.plus(24 * 3600000))
//        if (datedebut != null) {
//            graph.viewport.isXAxisBoundsManual = true
//            graph.viewport.setMinX(datedebut!!.time.toDouble())
//            graph.viewport.setMaxX(datefin!!.time.toDouble())
//            graph.gridLabelRenderer.numHorizontalLabels = 0
//            if (nbpoints % 2 == 1) {
//                staticLabelsFormatter.setHorizontalLabels(
//                    arrayOf(
//                        formatter.format(datedebut), formatter.format(
//                            Date((datefin.time + datedebut.time) / 2)
//                        ), formatter.format(datefin)
//                    )
//                )
//            } else {
//                staticLabelsFormatter.setHorizontalLabels(
//                    arrayOf(
//                        formatter.format(datedebut),
//                        formatter.format(datefin)
//                    )
//                )
//            }
//            graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter)
//        }

        when (max) {
            in 401..449 -> {
                graph.viewport.isYAxisBoundsManual = true
                graph.viewport.setMinY(0.0)
                graph.viewport.setMaxY(450.0)
                graph.gridLabelRenderer.numVerticalLabels = 4
            }
            in 450..499 -> {
                graph.viewport.isYAxisBoundsManual = true
                graph.viewport.setMinY(0.0)
                graph.viewport.setMaxY(500.0)
                graph.gridLabelRenderer.numVerticalLabels = 5
            }
            in 500..599 -> {
                graph.viewport.isYAxisBoundsManual = true
                graph.viewport.setMinY(0.0)
                graph.viewport.setMaxY(600.0)
                graph.gridLabelRenderer.numVerticalLabels = 4
            }
            in 800..999 -> {
                graph.viewport.isYAxisBoundsManual = true
                graph.viewport.setMinY(0.0)
                graph.viewport.setMaxY(1000.0)
                graph.gridLabelRenderer.numVerticalLabels = 5
            }
            else -> {}
        }

        graph.viewport.isScalable = true
        graph.viewport.isScrollable = true

        val adapter = SimpleCursorAdapter(
            this,
            R.layout.ligne_liste_stats,
            mCursor,
            arrayOf(
                StatsContract.StatsTable.COLUMN_NAME_DATE,
                StatsContract.StatsTable.COLUMN_NAME_NB_QUESTIONS_MOTS,
                StatsContract.StatsTable.COLUMN_NAME_NB_ERREURS_MOTS,
                StatsContract.StatsTable.COLUMN_NAME_NB_QUESTIONS_FORMES,
                StatsContract.StatsTable.COLUMN_NAME_NB_ERREURS_FORMES
            ),
            intArrayOf(
                R.id.text1,
                R.id.text2,
                R.id.text3,
                R.id.text4,
                R.id.text5
            ),
            0
        )
        val listView = findViewById<ListView>(R.id.listView)
        listView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menu.findItem(R.id.action_statistiques).isEnabled = false
        return true
    }
}
