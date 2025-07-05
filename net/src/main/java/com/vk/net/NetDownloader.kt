package com.vk.net

import com.vk.cache.Cache
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class NetDownloader(
    private val cache: Cache<String, ByteArray>
) {
    fun download(
        path: String,
        useCache: Boolean = false,
        method: String = "GET",
        readTimeout: Int = 3000
    ): ByteArray? {
        if (useCache) {
            val ba = cache.get(path)
            ba?.let {
                return it
            }
        }
        var stream: InputStream? = null
        var connection: HttpURLConnection? = null
        try {
            val url = URL(path)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = method
            connection.readTimeout = readTimeout
            connection.connect()
            stream = connection.getInputStream()
            val ba = stream?.readBytes()
            if (useCache) {
                ba?.let {
                    cache.put(path, ba)
                }
            }
            return ba
        } finally {
            stream?.close()
            connection?.disconnect()
        }
    }
}