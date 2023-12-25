package com.example.multipledownlode

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.webkit.URLUtil
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import com.downloader.PRDownloader

import com.example.multipledownlode.databinding.ActivityMainBinding
import java.io.File



class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: DownloadAdapter

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                downloadStart()
            } else {
                downloadStart()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = DownloadAdapter(this)
        binding.downlodeTaskView.layoutManager = LinearLayoutManager(this)
        binding.downlodeTaskView.adapter = adapter

        binding.downlode.setOnClickListener {
            checkPermissionsAndDownload()
        }
    }

    private fun checkPermissionsAndDownload() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                when {
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        downloadStart()
                    }
                    else -> {
                        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                }
            }
            else -> downloadStart()
        }
    }

    private fun downloadStart() {
        val downloadLink = binding.downlodeLinkEdt.text.toString()
        val fileName = URLUtil.guessFileName(downloadLink, null, null)

        val downloadPath = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "Demo"
        ).absolutePath

        val downloadId = PRDownloader.download(downloadLink, downloadPath, fileName)
            .build()
            .downloadId

        val downloadTask = DoenlodeTask(
            downloadId = downloadId,
            url = downloadLink,
            path = downloadPath.toString(),
            fileName = fileName
        )
        adapter.addDownloadTask(downloadTask)

        binding.downlodeLinkEdt.text.clear()
    }


}