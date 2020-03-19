package com.example.demo.kafka

import com.example.demo.pojo.Press
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import io.confluent.kafka.serializers.KafkaAvroSerializer
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import java.util.*


@Configuration
@EnableKafka
@Profile("prod")
open class KafkaConfiguration {

    //ConcurrentKafkaListenerContainerFactory为创建Kafka监听器的工程类，这里只配置了消费者
    @Bean
    open fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Press>? {
        val factory =
            ConcurrentKafkaListenerContainerFactory<String, Press>()
        factory.consumerFactory = consumerFactory()
        factory.isBatchListener = true
        return factory
    }

    //创建消费者工厂
    @Bean
    open fun consumerFactory(): ConsumerFactory<String, Press>? {
        return DefaultKafkaConsumerFactory(consumerProps())
    }

    //创建生产者工厂
    @Bean
    open fun producerFactory(): ProducerFactory<String, Press> {
        return DefaultKafkaProducerFactory(producerProps())
    }

    //kafkaTemplate实现了Kafka发送接收等功能
    @Bean
    open fun kafkaTemplate(): KafkaTemplate<String, Press> {
        return KafkaTemplate(producerFactory())
    }

    //消费者配置参数
    private fun consumerProps(): MutableMap<String, Any> {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        props[ConsumerConfig.GROUP_ID_CONFIG] = "bootKafka"
        props[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = true
        props[ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG] = "100"
        props[ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG] = "15000"
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = KafkaAvroDeserializer::class.java
        props[KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG] = "http://127.0.0.1:8081"
        props[KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG] = "true"
        return props
    }

    //生产者配置
    private fun producerProps(): MutableMap<String, Any> {
        val props: MutableMap<String, Any> = HashMap()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        props[ProducerConfig.RETRIES_CONFIG] = 0
        props[ProducerConfig.ACKS_CONFIG] = "all"
        props[ProducerConfig.BATCH_SIZE_CONFIG] = 16384
        props[ProducerConfig.LINGER_MS_CONFIG] = 0
        props[ProducerConfig.BUFFER_MEMORY_CONFIG] = 1024000
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaAvroSerializer::class.java
        props[KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG] = "http://127.0.0.1:8081"
        return props
    }

}