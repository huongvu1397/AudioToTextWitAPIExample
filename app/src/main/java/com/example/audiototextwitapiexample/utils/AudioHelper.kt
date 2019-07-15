package com.example.audiototextwitapiexample.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.MediaStore
import com.example.audiototextwitapiexample.model.AudioSong
import java.io.File

@SuppressLint("StaticFieldLeak")
object AudioHelper {

    const val BASE_TYPE_AUDIO = "audio"
    const val AUDIO_MP4 = "$BASE_TYPE_AUDIO/mp4"
    const val AUDIO_MPEG = "$BASE_TYPE_AUDIO/mpeg"
    const val AUDIO_OPUS = "$BASE_TYPE_AUDIO/opus"

    fun getAllAudios(c: Context): ArrayList<AudioSong> {
        val audios = ArrayList<AudioSong>()
        val projection = arrayOf(MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.Media.DISPLAY_NAME)
        val cursor = c.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null)
        try {
            cursor!!.moveToFirst()
            do {
                val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                val album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                val artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val iid = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)).toLong()
                val audioSong =
                    AudioSong(album = album, artist = artist, path = path, title = name, id = iid, year = "")
                audios.add(audioSong)
            } while (cursor.moveToNext())
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return audios
    }


    fun deleteAppFolder(context: Context) {
        val directory = context.filesDir
//        directory.deleteRecursively()
        val listFiles = directory.listFiles()
        if (listFiles != null && listFiles.isNotEmpty()) {
            for (file in listFiles) {
                file.deleteRecursively()
            }
        }
    }

    fun getFileExtension(file: File): String {
        val name = file.name
        try {
            return name.substring(name.lastIndexOf(".") + 1)
        } catch (e: Exception) {
            return ""
        }

    }

}