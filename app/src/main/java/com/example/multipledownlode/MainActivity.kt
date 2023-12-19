package com.example.multipledownlode

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.multipledownlode.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true) {
                startDownload()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
            else -> startDownload()
        }
    }
    private fun startDownload() {
        val url = binding.downlodeLinkEdt.text.toString()

        if (url.isEmpty()) {
            Toast.makeText(this, "URL is empty", Toast.LENGTH_SHORT).show()
            return
        }

        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
        )
        request.setTitle("Download")
        request.setDescription("The file is downloading.....")

        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        // Determine file type based on the file extension in the URL
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(url)
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension)

        // Use app-specific directory for downloads
        request.setDestinationInExternalFilesDir(
            this,
            Environment.DIRECTORY_DOWNLOADS,
            "${System.currentTimeMillis()}.$fileExtension"
        )

        // Set the MIME type for the download request
        mimeType?.let {
            request.setMimeType(it)
        }

        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)

        binding.downlodeLinkEdt.text.clear()
    }
}