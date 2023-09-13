package com.mobilecomputing.hw2

import android.R.attr.value
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.lang.Math.abs
import java.time.Instant


class SignActivity : AppCompatActivity() {
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var dbHelper: DbConnection;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)

        val keyValueMap: MutableMap<String, String> = HashMap()
        keyValueMap[DbConnection.Sign_Column_1] = "0"
        keyValueMap[DbConnection.Sign_Column_2] = "0"

        val btn = findViewById(R.id.page_symptom) as Button
        btn.setOnClickListener {
            val myIntent = Intent(this@SignActivity, SymptomActivity::class.java)
            myIntent.putExtra("key", value)
            this@SignActivity.startActivity(myIntent)
        }

        val heart_btn = findViewById(R.id.btn_heart_rate) as Button
        heart_btn.setOnClickListener {
            val cd_txt = findViewById(R.id.countdownText) as TextView
            if (cd_txt != null && cd_txt.text != "45") {

            } else {
                val video_view = findViewById(R.id.video) as VideoView
                val video_path = "android.resource://" + getPackageName() + "/raw/red"
                val video =
                    Uri.parse(video_path)
                video_view.setVideoURI(video)

                val vp = loadVideo()
                val s = SlowTask()
                val hr = s.execute(vp).get();

                video_view.setOnPreparedListener(OnPreparedListener { mp ->
                    mp.isLooping = false
                    video_view.start()
                })

                val txt_hr = findViewById(R.id.txt_heart_rate) as TextView
                val countDownMilis = 45 * 1000

                countDownTimer = object : CountDownTimer(countDownMilis.toLong(), 1000) {
                    override fun onTick(milisecondsLeft: Long) {
                        val cd_val = milisecondsLeft / 1000
                        cd_txt.text = "$cd_val"
                    }

                    override fun onFinish() {
                        cd_txt.text = "45"
                        txt_hr.text = "$hr"
                        keyValueMap[DbConnection.Sign_Column_1] = "$hr"
                        video_view.stopPlayback()
                        video_view.setVideoURI(null)
                    }
                }

                countDownTimer.start()
            }
        }

        val respiratory_btn = findViewById(R.id.btn_respiratory_rate) as Button
        respiratory_btn.setOnClickListener {
            val cd_txt = findViewById(R.id.countdownText) as TextView
            if (cd_txt != null && cd_txt.text != "45") {

            } else {
                val txt_rr = findViewById(R.id.txt_respiratory_rate) as TextView

                val rr = callRespiratoryCalculator()
                val countDownMilis = 45 * 1000

                countDownTimer = object : CountDownTimer(countDownMilis.toLong(), 1000) {
                    override fun onTick(milisecondsLeft: Long) {
                        val cd_val = milisecondsLeft / 1000
                        cd_txt.text = "$cd_val"
                    }

                    override fun onFinish() {
                        cd_txt.text = "45"
                        txt_rr.text = "$rr"
                        keyValueMap[DbConnection.Sign_Column_2] = "$rr"
                    }
                }

                countDownTimer.start()
            }
        }

        val upload = findViewById(R.id.btn_upload_sign) as Button
        upload.setOnClickListener {
            dbHelper = DbConnection(this);
            var db = dbHelper.writableDatabase
            val projection = arrayOf(
                DbConnection.Time_Stamp_Column,
                DbConnection.Sign_Column_1,
                DbConnection.Sign_Column_2
            )
            val cursor =
                db.query(DbConnection.TABLE_NAME_1, projection, null, null, null, null, null)
            cursor?.use {
                val time = Instant.now().toString()
                val contentValues = ContentValues()
                contentValues.put(DbConnection.Time_Stamp_Column, time)
                contentValues.put(DbConnection.Sign_Column_1, keyValueMap[DbConnection.Sign_Column_1].toString())
                contentValues.put(DbConnection.Sign_Column_2, keyValueMap[DbConnection.Sign_Column_2].toString())

                db.insertOrThrow(DbConnection.TABLE_NAME_1, "", contentValues)
            }
            db.close()
        }
    }

    private fun readCSVFromAssets(context: Context, fileName: String): List<String> {
        val csvData = mutableListOf<String>()

        try {
            val inputStream = context.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))

            var line: String? = reader.readLine()
            while (line != null) {
                csvData.add(line)
                line = reader.readLine()
            }

            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return csvData
    }

    private fun callRespiratoryCalculator(): Int {
        var accelValuesZ = Array(1281) { "" }
        val csvFileName = "CSVBreathe19.csv"
        val csvData = readCSVFromAssets(this, csvFileName)
        var count = 0
        for (line in csvData) {
            if (count > 1280) {
                break;
            } else {
                accelValuesZ[count++] = line
            }
        }

        var previousValue = 10f
        var currentValue = 0f
        var k = 0
        for (i in 1..1280) {
            currentValue = accelValuesZ[i].toDouble().toFloat()
            if (abs(previousValue - currentValue) > 0.15) {
                k++
            }
            previousValue = currentValue
        }
        val ret = (k / 45.00)

        return (ret * 30).toInt()
    }

    private fun loadVideo(): String {
        val f = File("$cacheDir/red.mp4")
        if (!f.exists()) try {
            val a = assets.open("red.mp4")
            val size = a.available()
            val buffer = ByteArray(size)
            a.read(buffer)
            a.close()
            val fos = FileOutputStream(f)
            fos.write(buffer)
            fos.close()
        } catch (e: java.lang.Exception) {
            throw RuntimeException(e)
        }
        return "$cacheDir/red.mp4"
    }

}

open class SlowTask
    : AsyncTask<String, String, String?>() {
    override fun doInBackground(vararg params: String?): String? {
        var m_bitmap: Bitmap? = null
        var retriever = MediaMetadataRetriever()
        var frameList = ArrayList<Bitmap>()
        try {
            retriever.setDataSource(params[0])
            var i = 0
            while (i < 1000) {
                val bitmap = retriever.getFrameAtIndex(i)
                frameList.add(bitmap!!)
                i += 75
            }
        } catch (m_e: Exception) {
            println(m_e)
        } finally {
            retriever?.release()
            var redBucket: Long = 0
            var pixelCount: Long = 0
            val a = mutableListOf<Long>()
            for (i in frameList) {
                redBucket = 0
                for (y in 225 until 250) {
                    for (x in 225 until 250) {
                        val c: Int = i.getPixel(x, y)
                        pixelCount++
                        redBucket += Color.red(c) + Color.blue(c) + Color.green(c)
                    }
                }
                a.add(redBucket)
            }
            val b = mutableListOf<Long>()
            for (i in 0 until a.lastIndex - 5) {
                var temp =
                    (a.elementAt(i) + a.elementAt(i + 1) + a.elementAt(i + 2) + a.elementAt(
                        i + 3
                    ) + a.elementAt(
                        i + 4
                    )) / 4
                b.add(temp)
            }
            var x = b.elementAt(0)
            var count = 0
            for (i in 1 until b.lastIndex) {
                var p = b.elementAt(i.toInt())
                if ((p - x)  < 0) {
                    count = count + 1
                }
                x = b.elementAt(i.toInt())
            }
            var rate = ((count.toFloat() / 5) * 60).toInt()
            return (rate * 2).toString()
        }
    }
}



