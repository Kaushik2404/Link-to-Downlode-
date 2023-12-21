import android.net.Uri
import com.example.multipledownlode.OnDownloadProgressListener
import java.io.*
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class DownloadManager(
    private val downloadLink: String,
    private val downloadPath: String,
    private val onDownloadProgressListener: OnDownloadProgressListener
) {

    private var total: Long = 0
    private var fail = false
    private lateinit var fileName: String
    private var currentProgress: Int = 0

    private val executor = Executors.newCachedThreadPool()

    fun startDownload() {
        var input: InputStream? = null
        var output: FileOutputStream? = null

        executor.execute {
            try {
                val dir = File(downloadPath)
                if (!dir.exists()) {
                    dir.mkdirs()
                }

                // Extract filename from the download link
                fileName = extractFileNameFromLink(downloadLink, dir)
                val outputFile = File(dir, fileName)

                val url = URL(downloadLink)
                val connection = url.openConnection()
                connection.connect()
                val lengthOfFile = connection.contentLength
                input = BufferedInputStream(url.openStream(), 8192)

                output = FileOutputStream(outputFile)

                val data = ByteArray(1024)
                var count: Int
                while (input!!.read(data).also { count = it } != -1) {
                    total += count.toLong()
                    val progress = (total * 100 / lengthOfFile).toInt()
                    currentProgress = progress
                    onDownloadProgressListener.percent(progress)
                    output!!.write(data, 0, count)
                }

                output!!.flush()
                fail = false
            } catch (e: Exception) {
                fail = true
                onDownloadProgressListener.downloadFail(e.message)
            } finally {
                try {
                    output?.close()
                    input?.close()
                } catch (e: IOException) {
                    onDownloadProgressListener.downloadFail(e.message)
                }

                if (fail) {
                    onDownloadProgressListener.downloadFail("download fail")
                } else {
                    onDownloadProgressListener.downloadedSuccess()
                }
            }
        }
    }

    private fun extractFileNameFromLink(link: String, directory: File): String {
        val uri = Uri.parse(link)
        val lastPathSegment = uri.lastPathSegment ?: "downloaded_file"

        // Extract the file extension from the last path segment of the URL
        val extension = lastPathSegment.substringAfterLast('.', "")

        // Remove file extension from the base filename
        var baseFileName = lastPathSegment.removeSuffix(".$extension")

        // Ensure the base filename has a valid extension
        val finalExtension = if (extension.isNotEmpty()) {
            ".$extension"
        } else {
            ".pdf"
        }

        // Generate a unique filename based on the current timestamp and counter
        var fileName = "$baseFileName$finalExtension"
        var counter = 1

        // Append (counter) until a unique filename is found
        while (File(directory, fileName).exists()) {
            fileName = "$baseFileName($counter)$finalExtension"
            counter++
        }

        return fileName
    }

    fun getProgress(): Int {
        return currentProgress
    }
}
