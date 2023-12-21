package com.example.multipledownlode

interface OnDownloadProgressListener {
    fun percent(percent: Int)
    fun downloadStart()
    fun downloadedSuccess()
    fun downloadFail(error: String?)
    fun downloadCancel()
}