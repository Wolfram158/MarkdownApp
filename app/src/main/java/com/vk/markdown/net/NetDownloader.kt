package com.vk.markdown.net

import com.vk.markdown.cache.Cache
import java.io.InputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection

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
        var connection: HttpsURLConnection? = null
        try {
            val url = URL(path)
            connection = url.openConnection() as HttpsURLConnection
            connection.setRequestMethod(method)
            connection.setReadTimeout(readTimeout)
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
