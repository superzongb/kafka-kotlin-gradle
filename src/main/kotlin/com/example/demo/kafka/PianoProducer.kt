package com.example.demo.kafka

import com.alibaba.fastjson.JSON
import com.example.demo.pojo.Press
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

@Component
open class PianoProducer {
    private var props: Properties? = null
    private var producer: Producer<String, String>? = null

    companion object {
        var logger: Logger = LoggerFactory.getLogger(PianoProducer::class.java)
    }

    init {
        props = Properties()
        props!!["bootstrap.servers"] = "127.0.0.1:9092"
        props!!["acks"] = "all" // 仅当所有Replica确认后，才认为记录提交成功
        props!!["retries"] = 0 // 不重试，注意重试可能引起重复消息
        props!!["linger.ms"] = 0 // 即使缓冲区有空间，批次也可能立即被发送，此配置引入延迟
        props!!["key.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
        props!!["value.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
        producer = org.apache.kafka.clients.producer.KafkaProducer(props)
    }

    /**
     * Send msg to specific topic.
     *
     * @param topic
     * @param message
     */
    fun sendMessage(topic: String?, message: String) {
        val record = ProducerRecord(topic, message, JSON.toJSONString(Press(message, System.currentTimeMillis())))
        logger.info("Send message to kafka, {}", record)
        producer!!.send(record)
    }
}