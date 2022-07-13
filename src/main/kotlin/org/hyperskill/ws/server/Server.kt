package org.hyperskill.ws.server

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.Map
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach
import kotlin.collections.set


fun main() {
    embeddedServer(Netty, host = "localhost", port = 8080) {
        install(WebSockets)
        routing {
            val clients = ConcurrentHashMap<WebSocketSession, String>()
            suspend fun broadcast(clients: Map<WebSocketSession, *>, text: String) {
                clients.forEach { (client, _) ->
                    client.send(Frame.Text(text))
                }
            }

            webSocket("/chat") {
                val name = call.parameters["name"]?.takeIf(String::isNotBlank)
                    ?: return@webSocket close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "no nickname"))
                try {
                    clients[this] = name
                    broadcast(clients, "$name joined!")

                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val text = frame.readText()
                            broadcast(clients, "$name: $text")
                        }
                    }
                } finally {
                    clients.remove(this)
                    broadcast(clients, "$name left.")
                }
            }
        }
    }.start(true)
}
