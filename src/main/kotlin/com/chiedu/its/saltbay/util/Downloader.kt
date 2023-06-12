package com.chiedu.its.saltbay.util

import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object Downloader {
    /**
     * Download a file and return bytes read
     */
    fun download(urlString: String, destination: File): Int {
        val url = URL(urlString)
        var connection = url.openConnection() as HttpURLConnection
        connection.instanceFollowRedirects = false

        println("Downloading: $urlString to $destination" )
        // Follow redirects
        while (connection.responseCode / 100 == 3) {
            val redirectUrl = connection.getHeaderField("Location")
            val redirectConnection = URL(redirectUrl).openConnection() as HttpURLConnection
            connection.disconnect()
            connection = redirectConnection
        }

        // Download the file
        val inputStream = BufferedInputStream(connection.inputStream)
        val outputStream = FileOutputStream(destination)
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }

        // Clean up
        outputStream.close()
        inputStream.close()
        connection.disconnect()
        return bytesRead
    }
}