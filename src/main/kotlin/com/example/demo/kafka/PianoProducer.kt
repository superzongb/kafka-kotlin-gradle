package com.example.demo.kafka

import com.example.demo.pojo.Press
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*


@Component
open class PianoProducer {
    private var props: Properties = Properties()
    private var producer: Producer<String, Press>? = null


    companion object {
        var logger: Logger = LoggerFactory.getLogger(PianoProducer::class.java)
    }

    init {
        props["bootstrap.servers"] = "127.0.0.1:9092"
        props["acks"] = "all" // 仅当所有Replica确认后，才认为记录提交成功
        props["retries"] = 0 // 不重试，注意重试可能引起重复消息
        props["linger.ms"] = 0 // 即使缓冲区有空间，批次也可能立即被发送，此配置引入延迟
        props["key.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
        props["value.serializer"] = "io.confluent.kafka.serializers.KafkaAvroSerializer"
        props["schema.registry.url"] = "http://127.0.0.1:8081"

        producer = org.apache.kafka.clients.producer.KafkaProducer(props)
    }

    /**
     * Send msg to specific topic.
     *
     * @param topic
     * @param message
     */
    fun sendMessage(topic: String?, message: String) {
        val record = ProducerRecord(topic, message, Press(message, System.currentTimeMillis()))
        logger.info("Send message to kafka, {}", record)
        producer!!.send(record)
    }
}