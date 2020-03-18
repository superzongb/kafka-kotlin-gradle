package com.example.demo.websocket


import com.example.demo.kafka.PianoListener
import com.example.demo.kafka.PianoProducer
import com.example.demo.kafka.PianoStream
import com.example.demo.pojo.Press
import com.example.demo.util.SpringContextUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.config.KafkaListenerEndpointRegistry
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.*
import java.util.concurrent.*
import javax.websocket.OnClose
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.server.ServerEndpoint

@ServerEndpoint("/websocket")
@Component
open class WebSocket {
    init {
        logger.info("WebSocket created. {}", this.toString())
    }

    companion object {
        var logger: Logger = LoggerFactory.getLogger(WebSocket::class.java)
        var sockets: MutableMap<String, WebSocket> = ConcurrentHashMap()
        lateinit var producer: PianoProducer

        fun isPianoNote(message: String): Boolean {
            return Press.PIANO_NOTES.contains(message)
        }
    }

    private lateinit var session: Session
    private var exec: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private var isSubscribed: Boolean = false
    private var pianoMessages: ConcurrentLinkedQueue<String> = ConcurrentLinkedQueue()
    private lateinit var pianoListener: PianoListener
    private lateinit var registry: KafkaListenerEndpointRegistry
    var id: String = ""
        get() {
            return session!!.id
        }

    @OnOpen
    fun onOpen(session: Session) {
        this.session = session
        sockets[session.id] = this

        if (producer == null) {
            producer = SpringContextUtil.getBean(PianoProducer::class) as PianoProducer
        }

        this.pianoListener = SpringContextUtil.getBean(PianoListener::class) as PianoListener

        this.registry = SpringContextUtil.getBean(KafkaListenerEndpointRegistry::class) as KafkaListenerEndpointRegistry

        logger.info("WebSocket connected. {}-{}", this, session.id)
    }

    @OnClose
    fun onClose(session: Session) {
        if (sockets.containsKey(session.id)) {
            sockets.remove(session.id)
        }
        this.pianoListener.unregisterSession(this)
        logger.info("WebSocket disconnected. ${this}-${session.id}")
    }

    @OnMessage
    fun onMessage(message: String, session: Session) {
        if (message == "replay" && !isSubscribed) {
            exec.scheduleAtFixedRate({ replay() }, 2, 10, TimeUnit.SECONDS)
            this.pianoListener.registerSession(this)
            isSubscribed = true
            return
        } else if (message == "redo" && isSubscribed) {
            toBegin()
            return
        } else if (message == "stream") {
            PianoStream.active()
        } else if (message == "query") {
            PianoStream.query()
        }

        if (isPianoNote(message)) {
            producer!!.sendMessage(
                "Piano",
                message,
                this.session!!.id
            )
        }
    }


    fun replay() {

        var timestampOfTheFirstSyllable = 0L
        while (pianoMessages.isNotEmpty()) {
            var press = pianoMessages.poll()
            if (timestampOfTheFirstSyllable == 0L) {
                timestampOfTheFirstSyllable = press.split(",").toTypedArray()[0].toLong()
            }
            val timer = Timer() // 实例化Timer类
            val delay = press.split(",").toTypedArray()[0].toLong() - timestampOfTheFirstSyllable

            timer.schedule(object : TimerTask() {
                override fun run() {
                    try {
                        session.basicRemote.sendText(press.split(",").toTypedArray()[1])
                    } catch (e: IOException) {
                        logger.error("Send message from kafka failed", e)
                    }
                }
            }, delay) // 这里百毫秒

        }
    }

    private fun toBegin() {
        //consumer!!.seekToBegin()
        registry.getListenerContainer("demo")
    }

    fun sendMessage(pianoMessage: String) {
        pianoMessages.add(pianoMessage)
    }

}