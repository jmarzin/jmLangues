package com.jmarzin.jmlangues

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by jacques on 01/01/15.
 */
class MyDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        with(db) {
            execSQL(SQL_CREATE_THEMES)
            execSQL(SQL_CREATE_VERBES)
            execSQL(SQL_CREATE_MOTS)
            execSQL(SQL_CREATE_FORMES)
            execSQL(SQL_CREATE_SESSIONS)
            execSQL(SQL_CREATE_STATS)
            execSQL(SQL_CREATE_FORMES_TYPES)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        with(db) {
            execSQL(SQL_DELETE_THEMES)
            execSQL(SQL_DELETE_VERBES)
            execSQL(SQL_DELETE_MOTS)
            execSQL(SQL_DELETE_FORMES)
            execSQL(SQL_DELETE_SESSIONS)
            execSQL(SQL_DELETE_STATS)
            execSQL(SQL_DELETE_FORMES_TYPES)
            onCreate(this)
        }
    }

    companion object {

        const val DATABASE_VERSION = 11
        const val DATABASE_NAME = "RevLangues.db"
        private const val TEXT_TYPE = " TEXT"
        private const val INTEGER_TYPE = " INTEGER"
        private const val COMMA_SEP = ","
        private const val SQL_CREATE_THEMES =
            "CREATE TABLE " + ThemeContract.ThemeTable.TABLE_NAME + " (" +
                    ThemeContract.ThemeTable.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    ThemeContract.ThemeTable.COLUMN_NAME_NUMERO + INTEGER_TYPE + COMMA_SEP +
                    ThemeContract.ThemeTable.COLUMN_NAME_LANGUE_ID + TEXT_TYPE + COMMA_SEP +
                    ThemeContract.ThemeTable.COLUMN_NAME_DIST_ID + INTEGER_TYPE + COMMA_SEP +
                    ThemeContract.ThemeTable.COLUMN_NAME_LANGUE + TEXT_TYPE + COMMA_SEP +
                    ThemeContract.ThemeTable.COLUMN_NAME_DATE_MAJ + TEXT_TYPE +
                    " )"
        private const val SQL_DELETE_THEMES =
            "DROP TABLE IF EXISTS " + ThemeContract.ThemeTable.TABLE_NAME


        private const val SQL_CREATE_VERBES =
            "CREATE TABLE " + VerbeContract.VerbeTable.TABLE_NAME + " (" +
                    VerbeContract.VerbeTable.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    VerbeContract.VerbeTable.COLUMN_NAME_LANGUE_ID + TEXT_TYPE + COMMA_SEP +
                    VerbeContract.VerbeTable.COLUMN_NAME_DIST_ID + INTEGER_TYPE + COMMA_SEP +
                    VerbeContract.VerbeTable.COLUMN_NAME_LANGUE + TEXT_TYPE + COMMA_SEP +
                    VerbeContract.VerbeTable.COLUMN_NAME_DATE_MAJ + TEXT_TYPE +
                    " )"
        private const val SQL_DELETE_VERBES =
            "DROP TABLE IF EXISTS " + VerbeContract.VerbeTable.TABLE_NAME

        private const val SQL_CREATE_MOTS = "CREATE TABLE " + MotContract.MotTable.TABLE_NAME + " (" +
                MotContract.MotTable.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                MotContract.MotTable.COLUMN_NAME_THEME_ID + INTEGER_TYPE + COMMA_SEP +
                MotContract.MotTable.COLUMN_NAME_DIST_ID + INTEGER_TYPE + COMMA_SEP +
                MotContract.MotTable.COLUMN_NAME_LANGUE_NIVEAU + TEXT_TYPE + COMMA_SEP +
                MotContract.MotTable.COLUMN_NAME_FRANCAIS + TEXT_TYPE + COMMA_SEP +
                MotContract.MotTable.COLUMN_NAME_MOT_DIRECTEUR + TEXT_TYPE + COMMA_SEP +
                MotContract.MotTable.COLUMN_NAME_LANGUE_ID + TEXT_TYPE + COMMA_SEP +
                MotContract.MotTable.COLUMN_NAME_LANGUE + TEXT_TYPE + COMMA_SEP +
                MotContract.MotTable.COLUMN_NAME_PRONUNCIATION + TEXT_TYPE + COMMA_SEP +
                MotContract.MotTable.COLUMN_NAME_DATE_MAJ + TEXT_TYPE + COMMA_SEP +
                MotContract.MotTable.COLUMN_NAME_DATE_REV + TEXT_TYPE + COMMA_SEP +
                MotContract.MotTable.COLUMN_NAME_POIDS + INTEGER_TYPE + COMMA_SEP +
                MotContract.MotTable.COLUMN_NAME_NB_ERR + INTEGER_TYPE + COMMA_SEP +
                "FOREIGN KEY(" + MotContract.MotTable.COLUMN_NAME_THEME_ID + ") REFERENCES " +
                ThemeContract.ThemeTable.TABLE_NAME + "(" + ThemeContract.ThemeTable.COLUMN_NAME_ID + ")" +
                " )"
        private const val SQL_DELETE_MOTS = "DROP TABLE IF EXISTS " + MotContract.MotTable.TABLE_NAME

        private const val SQL_CREATE_FORMES_TYPES =
            "CREATE TABLE " + FormeTypeContract.FormeTypeTable.TABLE_NAME + " (" +
                    FormeTypeContract.FormeTypeTable.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    ThemeContract.ThemeTable.COLUMN_NAME_NUMERO + INTEGER_TYPE + COMMA_SEP +
                    FormeTypeContract.FormeTypeTable.COLUMN_NAME_LANGUE_ID + TEXT_TYPE + COMMA_SEP +
                    FormeTypeContract.FormeTypeTable.COLUMN_NAME_DIST_ID + INTEGER_TYPE + COMMA_SEP +
                    FormeTypeContract.FormeTypeTable.COLUMN_NAME_LANGUE + TEXT_TYPE + COMMA_SEP +
                    FormeTypeContract.FormeTypeTable.COLUMN_NAME_DATE_MAJ + TEXT_TYPE +
                    " )"
        private const val SQL_DELETE_FORMES_TYPES =
            "DROP TABLE IF EXISTS " + FormeTypeContract.FormeTypeTable.TABLE_NAME

        private const val SQL_CREATE_FORMES =
            "CREATE TABLE " + FormeContract.FormeTable.TABLE_NAME + " (" +
                    FormeContract.FormeTable.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    FormeContract.FormeTable.COLUMN_NAME_VERBE_ID + INTEGER_TYPE + COMMA_SEP +
                    FormeContract.FormeTable.COLUMN_NAME_DIST_ID + INTEGER_TYPE + COMMA_SEP +
                    FormeContract.FormeTable.COLUMN_NAME_FORME_TYPE_NUMERO + INTEGER_TYPE + COMMA_SEP +
                    FormeContract.FormeTable.COLUMN_NAME_LANGUE_ID + TEXT_TYPE + COMMA_SEP +
                    FormeContract.FormeTable.COLUMN_NAME_LANGUE + TEXT_TYPE + COMMA_SEP +
                    FormeContract.FormeTable.COLUMN_NAME_DATE_MAJ + TEXT_TYPE + COMMA_SEP +
                    FormeContract.FormeTable.COLUMN_NAME_DATE_REV + TEXT_TYPE + COMMA_SEP +
                    FormeContract.FormeTable.COLUMN_NAME_POIDS + INTEGER_TYPE + COMMA_SEP +
                    FormeContract.FormeTable.COLUMN_NAME_NB_ERR + INTEGER_TYPE + COMMA_SEP +
                    "FOREIGN KEY(" + FormeContract.FormeTable.COLUMN_NAME_VERBE_ID + ") REFERENCES " +
                    ThemeContract.ThemeTable.TABLE_NAME + "(" + VerbeContract.VerbeTable.COLUMN_NAME_ID + ")" +
                    " )"
        private const val SQL_DELETE_FORMES =
            "DROP TABLE IF EXISTS " + FormeContract.FormeTable.TABLE_NAME

        private const val SQL_CREATE_SESSIONS =
            "CREATE TABLE " + SessionContract.SessionTable.TABLE_NAME + " (" +
                    SessionContract.SessionTable.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    SessionContract.SessionTable.COLUMN_NAME_LANGUE + TEXT_TYPE + COMMA_SEP +
                    SessionContract.SessionTable.COLUMN_NAME_DERNIERE + INTEGER_TYPE + COMMA_SEP +
                    SessionContract.SessionTable.COLUMN_NAME_MODE_REVISION + TEXT_TYPE + COMMA_SEP +
                    SessionContract.SessionTable.COLUMN_NAME_POIDS_MIN + INTEGER_TYPE + COMMA_SEP +
                    SessionContract.SessionTable.COLUMN_NAME_ERR_MIN + INTEGER_TYPE + COMMA_SEP +
                    SessionContract.SessionTable.COLUMN_NAME_AGE_REV + INTEGER_TYPE + COMMA_SEP +
                    SessionContract.SessionTable.COLUMN_NAME_NIV_MAX + TEXT_TYPE + COMMA_SEP +
                    SessionContract.SessionTable.COLUMN_NAME_CONSERVE_STATS + INTEGER_TYPE + COMMA_SEP +
                    SessionContract.SessionTable.COLUMN_NAME_PARLE_AUTO + INTEGER_TYPE + COMMA_SEP +
                    SessionContract.SessionTable.COLUMN_NAME_NB_QUESTIONS + INTEGER_TYPE + COMMA_SEP +
                    SessionContract.SessionTable.COLUMN_NAME_NB_ERREURS + INTEGER_TYPE + COMMA_SEP +
                    SessionContract.SessionTable.COLUMN_NAME_LISTE_THEMES + TEXT_TYPE + COMMA_SEP +
                    SessionContract.SessionTable.COLUMN_NAME_LISTE_VERBES + TEXT_TYPE + COMMA_SEP +
                    SessionContract.SessionTable.COLUMN_NAME_LISTE + TEXT_TYPE + COMMA_SEP +
                    SessionContract.SessionTable.COLUMN_NAME_THEME_ID + INTEGER_TYPE + COMMA_SEP +
                    SessionContract.SessionTable.COLUMN_NAME_MOT_ID + INTEGER_TYPE + COMMA_SEP +
                    SessionContract.SessionTable.COLUMN_NAME_VERBE_ID + INTEGER_TYPE + COMMA_SEP +
                    SessionContract.SessionTable.COLUMN_NAME_FORME_ID + INTEGER_TYPE + COMMA_SEP +
                    SessionContract.SessionTable.COLUMN_NAME_FORME_TYPE_NUMERO + INTEGER_TYPE +
                    " )"

        private const val SQL_DELETE_SESSIONS =
            "DROP TABLE IF EXISTS " + SessionContract.SessionTable.TABLE_NAME

        private const val SQL_CREATE_STATS =
            "CREATE TABLE " + StatsContract.StatsTable.TABLE_NAME + " (" +
                    StatsContract.StatsTable.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    StatsContract.StatsTable.COLUMN_NAME_LANGUE_ID + TEXT_TYPE + COMMA_SEP +
                    StatsContract.StatsTable.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
                    StatsContract.StatsTable.COLUMN_NAME_NB_QUESTIONS_MOTS + INTEGER_TYPE + COMMA_SEP +
                    StatsContract.StatsTable.COLUMN_NAME_NB_ERREURS_MOTS + INTEGER_TYPE + COMMA_SEP +
                    StatsContract.StatsTable.COLUMN_NAME_NB_QUESTIONS_FORMES + INTEGER_TYPE + COMMA_SEP +
                    StatsContract.StatsTable.COLUMN_NAME_NB_ERREURS_FORMES + INTEGER_TYPE +
                    " )"

        private const val SQL_DELETE_STATS = "DROP TABLE IF EXISTS " + StatsContract.StatsTable.TABLE_NAME

    }
}

