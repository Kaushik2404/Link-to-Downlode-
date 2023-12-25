package com.example.multipledownlode

data class DoenlodeTask(
    var downloadId: Int ? = null,
    val url: String,
    val path: String,
    var fileName: String,
    var counter: Int = 0
)