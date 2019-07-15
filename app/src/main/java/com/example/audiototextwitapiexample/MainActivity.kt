package com.example.audiototextwitapiexample

import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.audiototextwitapiexample.adapter.AudioAdapter
import com.example.audiototextwitapiexample.model.AudioSong
import com.example.audiototextwitapiexample.utils.AudioHelper
import com.example.audiototextwitapiexample.utils.ConvertFile
import com.example.audiototextwitapiexample.utils.SpeechTask
import com.example.audiototextwitapiexample.utils.TEST_AUDIO_TEXT_SpeechTask
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutionException

class MainActivity : AppCompatActivity(), AudioAdapter.IOnAudioAdapter {
    override fun actionAudioToText(position: Int) {
        Log.e("HVV1312", "lalalal ---- $position")
        val data = listAudio?.get(position)
        runNow(data?.path)
    }

    var exte: String? = null
    var path: String? = null
    var uri: Uri? = null
    var sent = 0
    var type = ""
    var outpath = ""
    var outputFile: File? = null
    private var listAudio: ArrayList<AudioSong>? = null
    private var adapter: AudioAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listAudio = arrayListOf()
        LoadTask().execute()
        try {
            loadFFMpegBinary()
        } catch (e: FFmpegNotSupportedException) {
            e.printStackTrace()
        }

    }

    private fun runNow(path: String?) {
        val file = File(path)
        this.path = path
        uri = Uri.fromFile(file)
        exte = AudioHelper.getFileExtension(file)
        sent = 1
        type = "audio/$exte"
        convert()
    }

    // convert time ----
    private fun convert() {
        outpath = ""
        if (uri == null) {
            //show(this.context, "audio uri null")
            //updateTvResultEmpty("audio uri null")
        } else if (sent == 1) {
            outpath = getOutputPathByUri(uri!!, exte!!)
            sent = 0
        } else if (type == "audio/ogg; codecs=opus" || type == AudioHelper.AUDIO_OPUS) {
            outpath = getOutputPathByUri(uri!!, "opus")
        } else if (type == "audio/ogg") {
            outpath = getOutputPathByUri(uri!!, "ogg")
        } else if (type == "audio/aac") {
            outpath = getOutputPathByUri(uri!!, "wav")
        } else if (type == AudioHelper.AUDIO_MP4) {
            outpath = getOutputPathByUri(uri!!, "m4a")
        } else if (type == AudioHelper.AUDIO_MPEG) {
            outpath = getOutputPathByUri(uri!!, "mp3")
        } else if (type == "audio/amr") {
            outpath = getOutputPathByUri(uri!!, "amr")
        } else if (uri?.path!!.endsWith("wav") || uri?.path!!.endsWith("aac") || uri?.path!!.endsWith(
                "mp3"
            ) || uri?.path!!.endsWith("m4a") || uri?.path!!.endsWith("ogg") || uri?.path!!.endsWith(
                "flac"
            ) || uri?.path!!.endsWith("amr") || uri?.path!!.endsWith("opus")
        ) {
            this.outpath = uri?.path!!
        } else {
            //show(this, "Invalid File")
            //updateTvResultEmpty("Invalid File")
        }
        if (outpath != "") {
            object : Thread() {
                override fun run() {
                    var key = "M577T3DVL7MXLWHFZP7LY6ZA6GREFHVL" // for en/us
                    var quality = "wav"
                    var i = 0
                    while (i < 4) {
                        var piece = i
                        Log.e("HVV1312 oupaht", "-----$outpath")
                        outputFile = ConvertFile(this@MainActivity, outpath, piece, quality).convertFile()
                        var text: String? = null
                        try {
                            var task = SpeechTask()
                            Log.e("HVV1312 outputfile", "-----${outputFile?.path}")

                            val list: ArrayList<String> = ArrayList()
                            // (key, outputFile?.path!!, quality)
                            list.add(key)
                            list.add(outputFile?.path!!)
                            list.add(quality)
                            text = task.execute(list).get()
                            while (!task.isFinish) {
                                sleep(10)
                            }
                        } catch (e: InterruptedException) {
                            if (text == null) {
                                if (text.equals("filenotfoundexception", false)) {
                                    Log.e("HVV1312 text ", "$text")
                                    Toast.makeText(this@MainActivity, "File not found", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this@MainActivity, "Mute Audio", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this@MainActivity, "Interet Error", Toast.LENGTH_SHORT).show()
                            }
                            outputFile?.delete()
                            i++
                        } catch (e2: ExecutionException) {
                            if (text == null) {
                                Toast.makeText(this@MainActivity, "Interet Error", Toast.LENGTH_SHORT).show()
                            } else {
                                if (text.equals("filenotfoundexception", false)) {
                                    //                          Toast.makeText(this@MainActivity, "Mute Audio", Toast.LENGTH_SHORT).show()
                                } else {
                                    Log.e("HVV1312 text ", "$text")
                                }
                            }

                            outputFile?.delete()
                            i++
                        }
                        if (text == null) {
                            //Toast.makeText(this@MainActivity,"Interet Error",Toast.LENGTH_SHORT).show()
                        } else {
                            if (text.equals("filenotfoundexception", false)) {
                                //                           Toast.makeText(this@MainActivity, "Mute Audio", Toast.LENGTH_SHORT).show()
                            } else {
                                Log.e("HVV1312 text ", "$text")
                            }
                            // if(i != apiece){
                            // }
                        }
                        try {
                            outputFile!!.delete()
                        } catch (e4: Exception) {
                            e4.printStackTrace()
                        }
                        i++
                    }
                    if (outpath.contains("audioUri")) {
                        try {
                            File(outpath).delete()
                        } catch (e42: Exception) {
                            e42.printStackTrace()
                        }
                    }
                }
            }.start()
        }
    }


    private fun getOutputPathByUri(audioUri: Uri, audioFormat: String): String {
        val file = File(baseContext.filesDir, "aUri" + System.currentTimeMillis() + "." + audioFormat)
        try {
            val fIn = contentResolver.openInputStream(audioUri)
            val buffer = ByteArray(fIn!!.available())
            fIn.read(buffer)
            fIn.close()
            if (!file.exists()) {
                file.createNewFile()
            }
            val save = FileOutputStream(file.path)
            save.write(buffer)
            save.flush()
            save.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file.path
    }

    @Throws(FFmpegNotSupportedException::class)
    fun loadFFMpegBinary() {
        try {
            FFmpeg.getInstance(baseContext).loadBinary(object : FFmpegLoadBinaryResponseHandler {
                override fun onStart() {}

                override fun onSuccess() {
                    Log.e("HVV1312", "success ffmpeg")
                }

                override fun onFailure() {
                    Log.e("HVV1312", "failure")
                }

                override fun onFinish() {}
            })
        } catch (e: FFmpegNotSupportedException) {
            e.printStackTrace()
        }

    }

    inner class LoadTask : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                listAudio = AudioHelper.getAllAudios(this@MainActivity)
                adapter = AudioAdapter(listAudio!!, this@MainActivity)
                adapter?.setListener(this@MainActivity)
                rcl_audio.adapter = adapter
            } catch (e: Exception) {

            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            Toast.makeText(this@MainActivity, "-heilo - -0000---- ${listAudio?.size}", Toast.LENGTH_LONG).show()
            rcl_audio.adapter?.notifyDataSetChanged()
        }

        override fun onPreExecute() {
            super.onPreExecute()
            Log.e("HVV1312", "ok pre execute")
        }
    }
}
