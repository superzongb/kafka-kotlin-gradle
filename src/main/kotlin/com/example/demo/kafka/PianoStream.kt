package com.example.demo.kafka

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import java.util.*

class PianoStream {
    var prop: Properties = Properties()

    init {
        prop[StreamsConfig.APPLICATION_ID_CONFIG] = "names-counter-application"
        prop[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        prop[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass
        prop[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.Long().javaClass
        var builder: StreamsBuilder = StreamsBuilder()
        var pianoPress: KStream<String, String> = builder.stream("Piano")
        var pianoPressCount: KTable<String, Long> = pianoPress.groupBy { key, value -> key }.count()

        var streams = KafkaStreams(builder.build(), prop)
        streams.start()


    }
}