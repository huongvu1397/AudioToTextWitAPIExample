package com.example.audiototextwitapiexample.utils

import android.content.Context
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import java.io.File

class ConvertFile(context: Context, inputPath:String, audioPiece:Int, conversionType:String) {
    private var audioPiece:Int = 0
    private var context:Context = context
    private var conversionType:String
    private var inputPath:String = inputPath
    private var mainPath: File = context.filesDir
    private var outputFileName:String
    init{
        this.audioPiece = audioPiece
        if (conversionType == "mpeg3")
        {
            this.outputFileName = "output" + System.currentTimeMillis() + ".mp3"
        }
        else
        {
            this.outputFileName = "output" + System.currentTimeMillis() + ".wav"
        }
        this.conversionType = conversionType
    }

    fun convertFile():File {
        val outputFile = File(this.mainPath, this.outputFileName)
        try
        {
            if (!outputFile.exists())
            {
                outputFile.createNewFile()
            }
            val cmd = getCmdLine(outputFile.path)
            val myFFmpegExecuteResponseHandler = FFmpegExeResponseHandler()
            val t0 = System.currentTimeMillis()
            FFmpeg.getInstance(this.context).execute(cmd, myFFmpegExecuteResponseHandler)
            while (!myFFmpegExecuteResponseHandler.isFinish)
            {
                Thread.sleep(10)
            }
            println("Time elapsed: " + (System.currentTimeMillis() - t0))
        }
        catch (e2:Exception) {
            e2.printStackTrace()
        }
        return outputFile
    }

    private fun getCmdLine(outputPath:String):Array<String> {
        if (this.conversionType == "mpeg3")
        {
            return arrayOf("-y", "-ss", (this.audioPiece * 13).toString(), "-t", (13).toString(), "-i", this.inputPath, "-acodec", "libmp3lame", "-ar", "16000", "-ac", "1", outputPath)
        }
        return arrayOf("-y", "-ss", (this.audioPiece * 13).toString(), "-t", (13).toString(), "-i", this.inputPath, "-acodec", "pcm_s16le", "-ar", "16000", "-ac", "1", outputPath)
    }
}