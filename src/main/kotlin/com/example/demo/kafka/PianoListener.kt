package com.example.demo.kafka

import com.example.demo.pojo.Press
import com.example.demo.websocket.IWebSocket
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap


@Component
open class PianoListener {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(PianoListener::class.java)
    }

    private var sessions: ConcurrentHashMap<String, IWebSocket> = ConcurrentHashMap()

    fun registerSession(socket: IWebSocket) {
        sessions[socket.getId()] = socket
    }

    fun unregisterSession(socket: IWebSocket) {
        sessions.remove(socket.getId())
    }

    @KafkaListener(id = "demo", topics = ["Piano"])
    fun listen(records: List<Press>) {
        records.stream().forEach { press ->
            sendToRegistedSocket(translateMsg(press))
        }
    }

    private fun sendToRegistedSocket(message: String) {
        sessions.values.stream().forEach { session ->
            session.sendMessage(message)
        }
    }

    private fun translateMsg(record: Press): String {
        logger.info("handle {}", record.toString())
        return record.timeStamp.toString() + "," + record.data
    }
}