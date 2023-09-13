package com.mobilecomputing.hw2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DbConnection(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.execSQL(TABLE_CREATE_1)
            db.execSQL(TABLE_CREATE_2)
        }
        catch (e: SQLiteException){
            println("Db Error"+ e.message)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_1)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_2)
    }

    companion object {
        private const val DATABASE_NAME = "testapp.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME_1 = "signs"
        const val Sign_Column_1 = "heart_rate"
        const val Sign_Column_2 = "respiratory_rate"

        const val TABLE_NAME_2 = "symptoms"
        const val Time_Stamp_Column = "time_stamp"
        const val Symptom_Column_1 = "fever"
        const val Symptom_Column_2 = "cough"
        const val Symptom_Column_3 = "cold"
        const val Symptom_Column_4 = "flu"
        const val Symptom_Column_5 = "asthma"
        const val Symptom_Column_6 = "diarrhea"
        const val Symptom_Column_7 = "migraine"
        const val Symptom_Column_8 = "headache"
        const val Symptom_Column_9 = "stress"
        const val Symptom_Column_10 = "dizziness"
        const val Symptom_Column_11 = "arthritis"
        const val Symptom_Column_12 = "nausea"


        private const val TABLE_CREATE_1 =
            "CREATE TABLE $TABLE_NAME_1 ($Time_Stamp_Column TEXT, $Sign_Column_1 TEXT, $Sign_Column_2 TEXT);"

        private const val TABLE_CREATE_2 =
            "CREATE TABLE $TABLE_NAME_2 ($Time_Stamp_Column TEXT, $Symptom_Column_1 TEXT, $Symptom_Column_2 TEXT, $Symptom_Column_3 TEXT, $Symptom_Column_4 TEXT, $Symptom_Column_5 TEXT, $Symptom_Column_6 TEXT, $Symptom_Column_7 TEXT, $Symptom_Column_8 TEXT, $Symptom_Column_9 TEXT, $Symptom_Column_10 TEXT, $Symptom_Column_11 TEXT, $Symptom_Column_12 TEXT);"

    }
}