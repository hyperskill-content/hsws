package org.hyperskill.ws.client

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.concurrent.thread


fun main() {
    print("What's your name? ")
    val name = readLine() ?: return

    val client = OkHttpClient()
    val request =
        Request.Builder()
            .url("ws://localhost:8080/chat?name=" +
                URLEncoder.encode(name, StandardCharsets.UTF_8))
            .build()

    client.newWebSocket(
        request,
        object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                thread(isDaemon = true) {
                    while (true)
                        webSocket.send(readLine() ?: break)
                    webSocket.close(1000, null)
                    client.close()
                }
            }
            override fun onMessage(webSocket: WebSocket, text: String) {
                println(text)
            }
            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
            }
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                println("Connection closed. $code $reason")
                client.close()
            }
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                t.printStackTrace()
                client.close()
            }
        }
    )
}

private fun OkHttpClient.close() {
    dispatcher.executorService.shutdown()
    connectionPool.evictAll()
}
