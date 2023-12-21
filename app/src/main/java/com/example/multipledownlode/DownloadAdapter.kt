package com.example.multipledownlode

import DownloadManager
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class DownloadAdapter(private val context: Context):RecyclerView.Adapter<DownloadAdapter.ViewHolder>() {


    var task= mutableListOf<DoenlodeTask>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val iteamView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_downlode, parent, false)
        return ViewHolder(iteamView)
    }

    fun addDownloadTask(task: DoenlodeTask) {
       this.task.add(task)
        notifyItemInserted(this.task.size - 1)
    }
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.linkTv.text = task[position].uri.toString()
        holder.start.setOnClickListener {
            holder.start.isClickable= false

            val downloadPath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Demo").absolutePath
            val downloadDirectory = File(downloadPath)
            if (!downloadDirectory.exists()) {
                downloadDirectory.mkdirs() // Create the directory and any missing parent directories if needed
            }
            val downloadManager1 =
                downloadPath?.let { it1 ->
                    DownloadManager(task[position].uri, it1, object : OnDownloadProgressListener {
                        override fun percent(percent: Int) {
                            (context as? Activity)?.runOnUiThread {
                                holder.progress.progress = percent
                                holder.linkTv.text = "$percent%"
                                logd("downloadProgress:$percent")
                                if (percent==100){
                                    holder.linkTv.text = "download successful"
                                }
                            }
                        }
                        override fun downloadStart() {
                            // Implement if needed
                        }

                        override fun downloadedSuccess() {
                            (context as? Activity)?.runOnUiThread {
                                    holder.linkTv.text = "download successful"
                            }
                        }

                        override fun downloadFail(error: String?) {
                            // Handle download failure if needed
                        }

                        override fun downloadCancel() {

                        }
                    })
                }
            downloadManager1?.startDownload()
        }

    }

    override fun getItemCount(): Int {
        return task.size
    }


    class ViewHolder(iteamView: View) : RecyclerView.ViewHolder(iteamView) {
        var start = iteamView.findViewById<Button>(R.id.start)
        var linkTv = iteamView.findViewById<TextView>(R.id.textView)
        var progress: ProgressBar = iteamView.findViewById(R.id.progressBar)


    }


}