package com.example.multipledownlode

import android.Manifest
import android.R.attr.path
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.webkit.URLUtil
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.downloader.PRDownloader

import com.example.multipledownlode.databinding.ActivityMainBinding
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    private lateinit var adapter: DownloadAdapter

    private var downloadId:Int  ?= null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true) {
                downloadStart()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        adapter = DownloadAdapter(this)
        binding.downlodeTaskView.layoutManager=LinearLayoutManager(this)
        binding.downlodeTaskView.adapter=adapter

        binding.downlode.setOnClickListener {
            checkPermissionsAndDownload()
        }
    }

    private fun checkPermissionsAndDownload() {
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                requestPermissionLauncher.launch(permissions)
            }
            else -> downloadStart()
        }
    }

//    private fun downloadStart() {
//        val fileName = URLUtil.guessFileName(binding.downlodeLinkEdt.text.toString(), null, null)
////        val path = if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
////            // External storage is available
////            val file = ContextCompat.getExternalFilesDirs(this, null)[0]
////            file.absolutePath
////        } else {
////            // External storage is not available, use internal storage
////            this.filesDir.absolutePath
////        }
//
//
//        val externalPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath
//
//
//        var downloadId = PRDownloader.download(binding.downlodeLinkEdt.text.toString(), externalPath, fileName)
//            .build()
//            .downloadId
//
//        val downlodetask= DoenlodeTask(downloadId= downloadId ,url= binding.downlodeLinkEdt.text.toString(),
//            path= externalPath.toString(),
//            fileName = fileName)
//
////        val downloadLink = binding.downlodeLinkEdt.text.toString()
////        val downlodetask= DoenlodeTask(System.currentTimeMillis().toDouble(),downloadLink)
//////        downloadManagers.add(downlodetask)
//
//        adapter.addDownloadTask(downlodetask)
////        val downloadPath = Environment.DIRECTORY_DOWNLOADS
////        val downloadPath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Demo").absolutePath
//
//        binding.downlodeLinkEdt.text.clear()
//    }


    private fun downloadStart() {
        val downloadLink = binding.downlodeLinkEdt.text.toString()
        val fileName = URLUtil.guessFileName(downloadLink, null, null)

//        val downloadPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath

        val downloadPath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Demo").absolutePath
//
        val downloadId = PRDownloader.download(downloadLink, downloadPath, fileName)
            .build()
            .downloadId

        val downloadTask = DoenlodeTask(downloadId = downloadId, url = downloadLink, path = downloadPath.toString(), fileName = fileName)
        adapter.addDownloadTask(downloadTask)

        binding.downlodeLinkEdt.text.clear()
    }

}
