package com.example.demo.kafka.serdes

import com.example.demo.pojo.Press
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import org.apache.kafka.common.serialization.Deserializer

class PressDeserializer : Deserializer<Press> {
    private var kafkaAvroDeserializer: KafkaAvroDeserializer = KafkaAvroDeserializer()

    override fun deserialize(topic: String?, data: ByteArray?): Press {
        return kafkaAvroDeserializer.deserialize(topic, data) as Press
    }

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {
        kafkaAvroDeserializer.configure(configs, isKey)
    }
}