package com.example.demo.kafka



import com.alibaba.fastjson.JSON
import com.example.demo.pojo.Press
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.PartitionInfo
import org.apache.kafka.common.TopicPartition
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*

class PianoConsumer(group: String, topic: String) {
    var props: Properties = Properties()
    var topic: String = topic
    private var consumer: KafkaConsumer<String, String>? = null

    companion object {
        var logger: Logger = LoggerFactory.getLogger(PianoConsumer::class.java)
    }

    init {
        props.put("bootstrap.servers", "127.0.0.1:9092")
        props.put("group.id", group);
        props.put("enable.auto.commit", "true")
        props.put("auto.commit.interval.ms", "10")
        props.put(
            "key.deserializer",
            "org.apache.kafka.common.serialization.StringDeserializer"
        )
        props.put(
            "value.deserializer",
            "org.apache.kafka.common.serialization.StringDeserializer"
        )


    }

    fun active(): PianoConsumer {
        if (this.consumer == null) {
            consumer = KafkaConsumer(props)
            consumer!!.subscribe(listOf(topic))
            consumer!!.poll(Duration.ofMillis(100))
        }
        return this
    }

    @Synchronized
    fun receiveMessage(): List<String>? {
        val records = consumer!!.poll(Duration.ofMillis(100))
        logger.info("receive {} messages from Kafka {}", records.count(), this)
        val replays: MutableList<String> = ArrayList()
        var temp1: Long = 0
        for (record in records) {
            val press = JSON.parseObject(record.value(), Press::class.java)
            val timestamp = press.timeStamp
            if (temp1 == 0L) {
                temp1 = timestamp
            }
            val message = record.key()
            replays.add((timestamp - temp1).toString() + "," + message)
        }
        return replays
    }

    @Synchronized
    fun seekToBegin() {
        logger.info("Seek to begin")
        var partitionInfos = consumer!!.partitionsFor(topic)
        var topicPartitions: MutableList<TopicPartition> = ArrayList()

        for (partitioninfo: PartitionInfo in partitionInfos) {
            topicPartitions.add(TopicPartition(topic, partitioninfo.partition()))
        }

        consumer!!.seekToBeginning(topicPartitions)
    }
}