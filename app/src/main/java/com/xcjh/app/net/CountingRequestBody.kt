package com.xcjh.app.net

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import java.io.IOException


interface ProgressListener {
    fun onProgress(bytesWritten: Long, contentLength: Long)
}

class CountingRequestBody(private val file: File, private val contentType: String, private val listener: ProgressListener) : RequestBody() {

    override fun contentType(): MediaType? {
        return MediaType.parse(contentType)
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return file.length()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val inputStream = FileInputStream(file)
        var uploaded: Long = 0

        try {
            var read: Int
            while (inputStream.read(buffer).also { read = it } != -1) {
                uploaded += read.toLong()
                sink.write(buffer, 0, read)
                listener.onProgress(uploaded, contentLength())
            }
        } finally {
            inputStream.close()
        }
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 2048
    }
}
