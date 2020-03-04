package com.example.demo.websocket


import com.example.demo.kafka.PianoConsumer
import com.example.demo.kafka.PianoConsumers
import com.example.demo.kafka.PianoProducer
import com.example.demo.kafka.PianoStream
import com.example.demo.pojo.Press
import com.example.demo.util.SpringContextUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.websocket.OnClose
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.server.ServerEndpoint

@ServerEndpoint("/websocket")
@Component
open class WebSocket {
    init {
        logger.info("WebSocket created. {}", this)
    }

    companion object {
        var logger: Logger = LoggerFactory.getLogger(WebSocket::class.java)
        var sockets: MutableMap<String, WebSocket> = ConcurrentHashMap()
        var producer: PianoProducer? = null

        fun isPianoNote(message: String): Boolean {
            return Press.PIANO_NOTES.contains(message)
        }
    }

    private var session: Session? = null
    private var consumer: PianoConsumer? = null
    private var exec: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private var isSubscribed: Boolean = false

    @OnOpen
    fun onOpen(session: Session) {
        this.session = session
        sockets[session.id] = this

        if (producer == null) {
            producer = SpringContextUtil.getBean(PianoProducer::class) as PianoProducer
        }

        var kafkaConsumers = SpringContextUtil.getBean(PianoConsumers::class) as PianoConsumers
        this.consumer = kafkaConsumers.retreveConsumer(session.id.toInt())

        logger.info("WebSocket connected. {}-{}", this, session.id)
    }

    @OnClose
    fun onClose(session: Session) {
        if (sockets.containsKey(session.id)) {
            sockets.remove(session.id)
        }
        logger.info("WebSocket disconnected. {}-{}", this, session.id)
    }

    @OnMessage
    fun onMessage(message: String, session: Session) {
        if (message == "replay" && !isSubscribed) {
            exec.scheduleAtFixedRate({ replay(session) }, 2, 10, TimeUnit.SECONDS)
            isSubscribed = true
            return
        } else if (message == "redo" && isSubscribed) {
            toBegin()
            return
        } else if (message == "stream") {
            PianoStream.active()
        }

        if (isPianoNote(message)) {
            producer!!.sendMessage("Piano", message)
        }
    }


    private fun replay(session: Session) {
        var delay = 0
        for (press in consumer!!.receiveMessage()!!) {
            val timer = Timer() // 实例化Timer类
            if (delay != 0) {
                delay = press.split(",").toTypedArray()[0].toInt()
            }
            timer.schedule(object : TimerTask() {
                override fun run() {
                    try {
                        session.basicRemote.sendText(press.split(",").toTypedArray()[1])
                    } catch (e: IOException) {
                        logger.error("Send message from kafka failed", e)
                    }
                }
            }, delay.toLong()) // 这里百毫秒
            delay = 1
        }
    }

    private fun toBegin() {
        consumer!!.seekToBegin()
    }

}