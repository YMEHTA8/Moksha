package com.mobilecomputing.hw2

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RatingBar
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import java.time.Instant


class SymptomActivity : AppCompatActivity() {
    private lateinit var dbHelper: DbConnection;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symptom)
        val btn = findViewById(R.id.page_sign) as Button
        btn.setOnClickListener {
            val myIntent = Intent(this@SymptomActivity, SignActivity::class.java)
            this@SymptomActivity.startActivity(myIntent)
        }


        val list = findViewById(R.id.spinner) as Spinner

        val rb = findViewById(R.id.ratingBar) as RatingBar
        val keyValueMap: MutableMap<String, String> = HashMap()
        keyValueMap[DbConnection.Symptom_Column_1] = "0"
        keyValueMap[DbConnection.Symptom_Column_2] = "0"
        keyValueMap[DbConnection.Symptom_Column_3] = "0"
        keyValueMap[DbConnection.Symptom_Column_4] = "0"
        keyValueMap[DbConnection.Symptom_Column_5] = "0"
        keyValueMap[DbConnection.Symptom_Column_6] = "0"
        keyValueMap[DbConnection.Symptom_Column_7] = "0"
        keyValueMap[DbConnection.Symptom_Column_8] = "0"
        keyValueMap[DbConnection.Symptom_Column_9] = "0"
        keyValueMap[DbConnection.Symptom_Column_10] = "0"
        keyValueMap[DbConnection.Symptom_Column_11] = "0"
        keyValueMap[DbConnection.Symptom_Column_12] = "0"

        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, keyValueMap.keys.toList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        list.adapter = adapter
        var selectedItem = ""
        list.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedItem = parent.getItemAtPosition(position).toString()
                rb.rating = keyValueMap.get(selectedItem).toString().toFloat()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        rb.setOnRatingBarChangeListener { ratingBar, fl, b ->
            keyValueMap.set(selectedItem, fl.toString())
        }

        val upload = findViewById(R.id.btn_upload) as Button
        upload.setOnClickListener {
            dbHelper = DbConnection(this);
            var db = dbHelper.writableDatabase
            val projection = arrayOf(DbConnection.Time_Stamp_Column, DbConnection.Symptom_Column_1, DbConnection.Symptom_Column_2, DbConnection.Symptom_Column_3, DbConnection.Symptom_Column_4, DbConnection.Symptom_Column_5, DbConnection.Symptom_Column_6, DbConnection.Symptom_Column_7, DbConnection.Symptom_Column_8, DbConnection.Symptom_Column_9, DbConnection.Symptom_Column_10, DbConnection.Symptom_Column_11, DbConnection.Symptom_Column_12)
            val cursor = db.query(DbConnection.TABLE_NAME_2, projection, null, null, null, null, null)
            cursor?.use {

                val time = Instant.now().toString()

                val contentValues = ContentValues()
                contentValues.put(DbConnection.Time_Stamp_Column, time)
                contentValues.put(DbConnection.Symptom_Column_1, keyValueMap[DbConnection.Symptom_Column_1].toString())
                contentValues.put(DbConnection.Symptom_Column_2, keyValueMap[DbConnection.Symptom_Column_2].toString())
                contentValues.put(DbConnection.Symptom_Column_3, keyValueMap[DbConnection.Symptom_Column_3].toString())
                contentValues.put(DbConnection.Symptom_Column_4, keyValueMap[DbConnection.Symptom_Column_4].toString())
                contentValues.put(DbConnection.Symptom_Column_5, keyValueMap[DbConnection.Symptom_Column_5].toString())
                contentValues.put(DbConnection.Symptom_Column_6, keyValueMap[DbConnection.Symptom_Column_6].toString())
                contentValues.put(DbConnection.Symptom_Column_7, keyValueMap[DbConnection.Symptom_Column_7].toString())
                contentValues.put(DbConnection.Symptom_Column_8, keyValueMap[DbConnection.Symptom_Column_8].toString())
                contentValues.put(DbConnection.Symptom_Column_9, keyValueMap[DbConnection.Symptom_Column_9].toString())
                contentValues.put(DbConnection.Symptom_Column_10, keyValueMap[DbConnection.Symptom_Column_10].toString())
                contentValues.put(DbConnection.Symptom_Column_11, keyValueMap[DbConnection.Symptom_Column_11].toString())
                contentValues.put(DbConnection.Symptom_Column_12, keyValueMap[DbConnection.Symptom_Column_12].toString())

                db.insertOrThrow(DbConnection.TABLE_NAME_2, "", contentValues)
            }
            db.close()
        }
    }
}