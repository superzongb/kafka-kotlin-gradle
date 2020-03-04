package com.example.demo.kafka.serdes

import com.example.demo.pojo.Press
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.common.serialization.Serializer

class PressSerializer: Serializer<Press> {
    private val kafkaAvroSerializer: KafkaAvroSerializer = KafkaAvroSerializer()

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {
        kafkaAvroSerializer.configure(configs, isKey)
    }

    override fun close() {
        kafkaAvroSerializer.close()
    }

    override fun serialize(topic: String?, data: Press?): ByteArray {
        return kafkaAvroSerializer.serialize(topic, data)
    }
}