package com.rpea.qlob.api

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

object Downloader {
    private val client = OkHttpClient()

    suspend fun downloadFile(context: Context, url: String, reciterId: Int, surahId: Int): File? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val file = File(context.filesDir, "surah_${reciterId}_${surahId}.mp3")
                val fos = FileOutputStream(file)
                response.body?.byteStream()?.use { input ->
                    fos.use { output ->
                        input.copyTo(output)
                    }
                }
                return@withContext file
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext null
    }
}
