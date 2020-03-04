package com.example.demo.kafka

import com.example.demo.kafka.serdes.PressDeserializer
import com.example.demo.kafka.serdes.PressSerializer
import com.example.demo.pojo.Press
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Produced


import java.util.*
import kotlin.collections.HashMap

class PianoStream {

    //var stateStore: StoreBuilder<KeyValueStore<String, Press>>

    companion object {
        var started: Boolean = false
        fun active() {
            if (started)
                return

            started = true


            var prop: Properties = Properties()


            prop[StreamsConfig.APPLICATION_ID_CONFIG] = "names-counter-application"
            prop[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
            prop[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
            prop[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = SpecificAvroSerde::class.java.name
            prop[StreamsConfig.STATE_DIR_CONFIG] = "stream-state-store"
            prop[KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG] = "http://localhost:8081"
            prop[KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG] = "true"

            var valueSerdes = Serdes.serdeFrom(PressSerializer(), PressDeserializer())
            var serdeConfig: MutableMap<String, String> = HashMap()
            serdeConfig[KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG] = "http://localhost:8081"
            serdeConfig[KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG] = "true"
            valueSerdes.configure(serdeConfig, false)


            var builder: StreamsBuilder = StreamsBuilder()

            var simpleStream: KStream<String, Press> = builder.stream(
                "Piano",
                Consumed.with(Serdes.String(), valueSerdes)
            )

            simpleStream
                .mapValues { value: Press? -> value!!.timeStamp }
                .to(
                    "Piano-timestamp",
                    Produced.with(
                        Serdes.String(),
                        Serdes.Long()))

            var kafkaStreams: KafkaStreams = KafkaStreams(builder.build(), prop)
            kafkaStreams.start()
        }
    }

}