package com.example.audiototextwitapiexample.utils

import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SpeechTask : AsyncTask<ArrayList<String>, Void, String?>() {
    @Volatile
    var isFinish = false

    override fun doInBackground(vararg params: ArrayList<String>): String? {
        try {

            val x = params[0]

            val key = x[0]
            Log.e("AMBE1203", " thang huong ngu " + key + " - " + x[1] + " - " + x[2])

            val connection = URL(
                "https://api.wit.ai/speech?" + String.format(
                    "v=%s", URLEncoder.encode(
                        SimpleDateFormat("yyyyMMdd", Locale.US).format(
                            Date()
                        ), "UTF-8"
                    )
                )
            ).openConnection() as HttpURLConnection
            connection.doOutput = true
            connection.setRequestProperty("Authorization", "Bearer $key")
            connection.setRequestProperty("Content-Type", "audio/" + x[2])
            val outputStream = connection.outputStream
            println(params[1])
            val fileChannel = FileInputStream(x[1]).channel
            val byteBuffer = ByteBuffer.allocate(1024)
            Log.e("HVV1312 task ", "task me")
            while (fileChannel.read(byteBuffer) !== -1) {
                byteBuffer.flip()
                val b = ByteArray(byteBuffer.remaining())
                byteBuffer.get(b)
                outputStream.write(b)
                byteBuffer.clear()
            }
            val response = BufferedReader(InputStreamReader(connection.inputStream))
            val stringBuilder = StringBuilder()
            while (true) {
                val line = response.readLine()
                if (line == null || line == "") {
                    Log.e("AMBE12030204", JSONObject(stringBuilder.toString()).getString("_text"))
                    return JSONObject(stringBuilder.toString()).getString("_text")
                }
                stringBuilder.append(line)
                println(line)
                Log.e("HVV1312 mot luc do 2", "sss --- $line")
            }
        } catch (e2: java.lang.Exception) {
            e2.printStackTrace()
            return "filenotfoundException"
        } catch (e3: Exception) {
            e3.printStackTrace()
            return null
        }
    }

    override fun onPostExecute(result: String?) {
        this.isFinish = true
    }
}