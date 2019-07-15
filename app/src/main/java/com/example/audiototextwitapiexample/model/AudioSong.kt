package com.example.audiototextwitapiexample.model


data class AudioSong(
    var album: String,
    var artist: String,
    var id: Long = 0,
    var path: String,
    var title: String,
    var year: String
)