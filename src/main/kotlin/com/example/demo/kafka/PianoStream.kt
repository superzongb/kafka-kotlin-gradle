package com.example.demo.kafka


import com.example.demo.kafka.serdes.PressDeserializer
import com.example.demo.kafka.serdes.PressSerializer
import com.example.demo.pojo.Press
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import java.util.*
import kotlin.collections.HashMap


class PianoStream {


    companion object {
        var started: Boolean = false
        private var valueSerdes: Serde<Press>? = null
        var pianoKeyValueStore: ReadOnlyKeyValueStore<String, Long>? = null
        var kafkaStreams: KafkaStreams? = null

        fun active() {
            if (started)
                return

            started = true

            valueSerdes = createArvoSerde()

            var builder = createStreamsBuilder(valueSerdes)

            kafkaStreams = createKafkaStreams(builder)

            kafkaStreams!!.start()


        }

        fun query() {
            pianoKeyValueStore = this.kafkaStreams!!.store("press-count", QueryableStoreTypes.keyValueStore())
            println((pianoKeyValueStore)?.get("sss"))
        }


        private fun createKafkaStreams(builder: StreamsBuilder): KafkaStreams {
            var prop = Properties()
            prop[StreamsConfig.APPLICATION_ID_CONFIG] = "names-counter-application"
            prop[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
            prop[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
            prop[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = SpecificAvroSerde::class.java.name
            prop[StreamsConfig.STATE_DIR_CONFIG] = "stream-state-store"
            prop[KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG] = "http://localhost:8081"
            //This configuration make Avro to deserializer object as the specified type.
            prop[KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG] = "true"
            return KafkaStreams(builder.build(), prop)
        }


        private fun createStreamsBuilder(valueSerdes: Serde<Press>?): StreamsBuilder {
            var builder = StreamsBuilder()
            var simpleStream: KStream<String, Press> = builder.stream(
                "Piano"
            )

            var simpleTable: KTable<String, Long> = simpleStream
                .groupBy { key, _ -> key }
                .count(
                    Materialized.`as`("press-count")
                )

            return builder
        }

        private fun createArvoSerde(): Serde<Press>? {
            var valueSerdes = Serdes.serdeFrom(PressSerializer(), PressDeserializer())
            var serdeConfig: MutableMap<String, String> = HashMap()
            serdeConfig[KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG] = "http://localhost:8081"
            serdeConfig[KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG] = "true"
            valueSerdes.configure(serdeConfig, false)
            return valueSerdes
        }
    }

}