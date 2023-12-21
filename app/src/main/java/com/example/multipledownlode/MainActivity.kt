package com.example.multipledownlode

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.multipledownlode.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    private lateinit var adapter: DownloadAdapter

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

    private fun downloadStart() {
        val downloadLink = binding.downlodeLinkEdt.text.toString()
        val downlodetask= DoenlodeTask(System.currentTimeMillis().toDouble(),downloadLink)
//        downloadManagers.add(downlodetask)

        adapter.addDownloadTask(downlodetask)
//        val downloadPath = Environment.DIRECTORY_DOWNLOADS
//        val downloadPath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Demo").absolutePath

        binding.downlodeLinkEdt.text.clear()
    }

}
