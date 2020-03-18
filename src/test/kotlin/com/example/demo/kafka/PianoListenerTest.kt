package com.example.demo.kafka


import com.example.demo.DemoApplication
import com.example.demo.pojo.Press
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient
import io.confluent.kafka.serializers.*
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [DemoApplication::class], webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext
@EmbeddedKafka(topics = ["Piano"], bootstrapServersProperty = "spring.kafka.bootstrap-servers")
class PianoListenerTest {

    @Autowired
    lateinit var embeddedKafka: EmbeddedKafkaBroker



    val registryClient: MockSchemaRegistryClient = MockSchemaRegistryClient()

    @BeforeEach
    internal fun setUp() {
        var producerProps =
            KafkaTestUtils.producerProps(embeddedKafka)
        producerProps[AbstractKafkaAvroSerDeConfig.AUTO_REGISTER_SCHEMAS] = "true"
        producerProps[KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG] = "http://127.0.0.1:8081"
        var keyDeserializer = StringSerializer()
        var valueDeserializer = KafkaAvroSerializer(registryClient)
        valueDeserializer.configure(producerProps, false)
        val pf: DefaultKafkaProducerFactory<String, Any> =
            DefaultKafkaProducerFactory(producerProps, keyDeserializer, valueDeserializer)
        var template = KafkaTemplate(pf)


        template.send(
            "Piano", Press(
                Press.PIANO_NOTES[0],
                System.currentTimeMillis(),
                "test"
            )
        )
    }

    @Test
    fun test_listener() {
        var consumerProps = KafkaTestUtils.consumerProps(
            "test",
            "true",
            this.embeddedKafka
        )
        consumerProps[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        consumerProps[KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG] = "true"
        consumerProps[AbstractKafkaAvroSerDeConfig.AUTO_REGISTER_SCHEMAS] = "true"
        consumerProps[KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG] = "http://127.0.0.1:8081"
        var keyDeserializer = StringDeserializer()
        var valueDeserializer = KafkaAvroDeserializer(registryClient)
        valueDeserializer.configure(consumerProps, false)

        var consumer: Consumer<String, Any> = DefaultKafkaConsumerFactory(
            consumerProps,
            keyDeserializer,
            valueDeserializer
        ).createConsumer()

        this.embeddedKafka.consumeFromEmbeddedTopics(consumer, "Piano")
        val replies: ConsumerRecords<String, Any>? =
            KafkaTestUtils.getRecords(consumer)
        assertTrue(replies!!.count() >= 1)
    }
}