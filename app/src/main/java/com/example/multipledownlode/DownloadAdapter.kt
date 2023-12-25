package com.example.multipledownlode

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.Status
import com.downloader.utils.Utils

class DownloadAdapter(private val context: Context) : RecyclerView.Adapter<DownloadAdapter.ViewHolder>() {

    var tasks = mutableListOf<DoenlodeTask>()
    fun addDownloadTask(task: DoenlodeTask) {
        // Check for duplicate file names and update accordingly
        var updatedFileName = task.fileName
        var counter = 1
        while (tasks.any { it.fileName == updatedFileName }) {
            updatedFileName = "${task.fileNameWithoutExtension()} (${counter++}).${task.fileExtension()}"
        }
        task.fileName = updatedFileName
        tasks.add(task)
        notifyItemInserted(tasks.size - 1)
    }

    private fun DoenlodeTask.fileNameWithoutExtension(): String {
        return fileName.substringBeforeLast('.', fileName)
    }

    private fun DoenlodeTask.fileExtension(): String {
        return fileName.substringAfterLast('.', "")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_downlode, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]
        holder.bindDownloadTask(task)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Views in your item_downlode layout
        private val startButton: Button = itemView.findViewById(R.id.start)
        private val cancelButton: Button = itemView.findViewById(R.id.cencel)
        private val linkTv: TextView = itemView.findViewById(R.id.textView)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

        private var downloadId: Int? = null

        init {
            // Set click listeners for start and cancel buttons
            startButton.setOnClickListener { handleStartButtonClick() }
            cancelButton.setOnClickListener { handleCancelButtonClick() }
        }

        fun bindDownloadTask(task: DoenlodeTask) {
            // Bind data and set click listeners
            downloadId = task.downloadId
            linkTv.text = task.fileName

            // Update UI based on the download status
            updateStatus()
        }

        private fun updateStatus() {
            val status = PRDownloader.getStatus(downloadId ?: 0)

            when (status) {
                Status.RUNNING -> {
                    // Download is running, show pause option
                    startButton.text = "Pause"
                    setButtonEnabledState(true)
                }
                Status.PAUSED -> {
                    // Download is paused, show resume option
                    startButton.text = "Resume"
                    setButtonEnabledState(true)
                }
                else -> {
                    // Download is not running, show start option
                    startButton.text = "Start"
                    setButtonEnabledState(true)
                }
            }
        }

        private fun handleStartButtonClick() {
            when (PRDownloader.getStatus(downloadId ?: 0)) {
                Status.RUNNING -> {
                    // Download is running, pause it
                    PRDownloader.pause(downloadId ?: 0)
                }
                Status.PAUSED -> {
                    // Download is paused, resume it
                    PRDownloader.resume(downloadId ?: 0)
                }
                else -> {
                    // Download is not running, start it
                    startDownload()
                }
            }
        }

        private fun handleCancelButtonClick() {
            // Handle cancel button click
            PRDownloader.cancel(downloadId ?: 0)
        }

        private fun startDownload() {
            // Start the download and update UI
            val task = tasks[adapterPosition]

            downloadId = PRDownloader.download(task.url, task.path, task.fileName)
                .build()
                .setOnStartOrResumeListener {

                    startButton.text = "Pause"
                    setButtonEnabledState(true)
                    Toast.makeText(context, "Downloading started", Toast.LENGTH_SHORT).show()
                }
                .setOnPauseListener {
                    startButton.text = "Resume"
                    setButtonEnabledState(true)
                    Toast.makeText(context, "Downloading Paused", Toast.LENGTH_SHORT).show()
                }
                .setOnCancelListener {
                    downloadId = 0
                    setButtonEnabledState(true)
                    progressBar.progress = 0
                    Toast.makeText(context, "Downloading Cancelled", Toast.LENGTH_SHORT).show()
                }
                .setOnProgressListener { progress ->
                    val progressPercent = (progress.currentBytes * 100 / progress.totalBytes).toInt()
                    progressBar.progress = progressPercent
                    progressBar.isIndeterminate = false
                }
                .start(object : com.downloader.OnDownloadListener {
                    override fun onDownloadComplete() {
                        setButtonEnabledState(false)
                        startButton.text = "Completed"
                        Toast.makeText(context, "Downloading Completed", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(error: Error?) {
                        setButtonEnabledState(true)
                        startButton.text = "Start"
                        progressBar.progress = 0
                        downloadId = 0
                        Log.d("TAG11",error.toString())
                        Toast.makeText(context, "Error Occurred", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        private fun setButtonEnabledState(enabled: Boolean) {
            startButton.isEnabled = enabled
            cancelButton.isEnabled = enabled
        }
    }
}
