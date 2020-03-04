package com.example.demo.kafka


import com.example.demo.pojo.Press
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.PartitionInfo
import org.apache.kafka.common.TopicPartition
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*
import org.apache.avro.generic.GenericRecord

class PianoConsumer(group: String, topic: String) {
    var props: Properties = Properties()
    var topic: String = topic
    private var consumer: KafkaConsumer<String, Press>? = null

    companion object {
        var logger: Logger = LoggerFactory.getLogger(PianoConsumer::class.java)
    }

    init {
        props["bootstrap.servers"] = "127.0.0.1:9092"
        props["group.id"] = group;
        props["enable.auto.commit"] = "true"
        props["auto.commit.interval.ms"] = "10"
        props["key.deserializer"] = "org.apache.kafka.common.serialization.StringDeserializer"
        props["value.deserializer"] = "io.confluent.kafka.serializers.KafkaAvroDeserializer"
        props[KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG] = "http://localhost:8081"
        props[KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG] = "true"

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
        val records: ConsumerRecords<String, Press>? = consumer!!.poll(Duration.ofMillis(100))
        val replays: MutableList<String> = ArrayList()
        if (records != null) {
            var startMonment: Long = 0
            logger.info("receive {} messages from Kafka {}", records.count(), this)
            for (record: ConsumerRecord<String, Press> in records) {
                logger.info("handle {} from Kafka {}", record.value().toString(), this)
                val press = record.value()
                val timestamp = press.timeStamp
                if (startMonment == 0L) {
                    startMonment = timestamp
                }
                val message = record.key()
                replays.add((timestamp - startMonment).toString() + "," + message)
            }
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