package com.example.demo.kafka

import com.example.demo.pojo.Press
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component


@Component
open class PianoProducer {
    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, Press>

    companion object {
        var logger: Logger = LoggerFactory.getLogger(PianoProducer::class.java)
    }

    /**
     * Send msg to specific topic.
     *
     * @param topic
     * @param message
     */
    fun sendMessage(topic: String, message: String, sessionId: String) {
        val data = Press(message, System.currentTimeMillis(), sessionId)
        logger.info("Send message to kafka, {}", data)
        kafkaTemplate.send(topic, message, data)
    }
}