package com.example.demo.kafka


import com.example.demo.DemoApplication
import com.example.demo.pojo.Press
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [DemoApplication::class], webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(TestEmbeddedKafkaConfiguration::class)
@EmbeddedKafka(topics = ["Piano"], bootstrapServersProperty = "spring.kafka.bootstrap-servers")
class PianoListenerTest {

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @Autowired
    lateinit var consumerFactory: DefaultKafkaConsumerFactory<String, Any>

    @BeforeEach
    internal fun setUp() {
        kafkaTemplate.send(
            "Piano", Press(
                Press.PIANO_NOTES[0],
                System.currentTimeMillis(),
                "test"
            )
        )
    }

    @Test
    fun test_listener() {
        var consumer: Consumer<String, Any> = consumerFactory.createConsumer()
        consumer.subscribe(listOf("Piano"))
        val replies: ConsumerRecords<String, Any>? = consumer.poll(10000)
        assertTrue(replies!!.count() >= 1)
    }
}