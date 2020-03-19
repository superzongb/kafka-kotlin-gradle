package com.example.demo.kafka

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient
import io.confluent.kafka.serializers.*
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.utils.KafkaTestUtils

@Configuration
@EnableKafka
@Profile("test")
open class TestEmbeddedKafkaConfiguration {
    @Autowired
    lateinit var embeddedKafka: EmbeddedKafkaBroker

    private val registryClient: MockSchemaRegistryClient = MockSchemaRegistryClient()

    @Bean
    open fun consumerFactory(): ConsumerFactory<String, Any> {
        val valueSerializer = KafkaAvroDeserializer(registryClient)
        valueSerializer.configure(consumerProps(), false)
        return DefaultKafkaConsumerFactory(
            consumerProps(),
            StringDeserializer(),
            valueSerializer
        )
    }

    private fun consumerProps(): Map<String, Any> {
        val consumerProps = KafkaTestUtils.consumerProps(
            "test",
            "true",
            this.embeddedKafka
        )
        consumerProps[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        consumerProps[KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG] = "true"
        consumerProps[AbstractKafkaAvroSerDeConfig.AUTO_REGISTER_SCHEMAS] = "true"
        consumerProps[KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG] = "http://127.0.0.1:8081"
        return consumerProps
    }

    //创建生产者工厂
    @Bean
    open fun producerFactory(): ProducerFactory<String, Any> {
        val valueDeserializer = KafkaAvroSerializer(registryClient)
        valueDeserializer.configure(producerProps(), false)
        return DefaultKafkaProducerFactory(producerProps(), StringSerializer(), valueDeserializer)
    }

    private fun producerProps(): Map<String, Any> {
        val producerProps =
            KafkaTestUtils.producerProps(embeddedKafka)
        producerProps[AbstractKafkaAvroSerDeConfig.AUTO_REGISTER_SCHEMAS] = "true"
        producerProps[KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG] = "http://127.0.0.1:8081"
        return producerProps
    }

    //kafkaTemplate实现了Kafka发送接收等功能
    @Bean
    open fun kafkaTemplate(): KafkaTemplate<String, Any> {
        return KafkaTemplate(producerFactory())
    }

    @Bean
    open fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Any> {
        val factory =
            ConcurrentKafkaListenerContainerFactory<String, Any>()
        factory.consumerFactory = consumerFactory()
        factory.isBatchListener = true
        return factory
    }
}