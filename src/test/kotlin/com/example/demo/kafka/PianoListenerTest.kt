package com.example.demo.kafka


import com.example.demo.DemoApplication
import com.example.demo.pojo.Press
import com.example.demo.websocket.IWebSocket
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTimeout
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [DemoApplication::class], webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EmbeddedKafka(topics = ["Piano"], bootstrapServersProperty = "spring.kafka.bootstrap-servers")
class PianoListenerTest {

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @Autowired
    lateinit var consumerFactory: DefaultKafkaConsumerFactory<String, Any>

    @Autowired
    lateinit var pianoListener: PianoListener

    val latch = CountDownLatch(1)

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
    @DisplayName("测试Listener")
    fun test_listener() {
        pianoListener.registerSession(object : IWebSocket {
            override fun sendMessage(pianoMessage: String) {

                Assertions.assertEquals(
                    Press.PIANO_NOTES[0],
                    pianoMessage.split(",").toTypedArray()[1]
                )
                latch.countDown()
            }

            override fun getId(): String {
                return "test"
            }
        })
        assertTimeout(Duration.ofSeconds(10)) {
            latch.await(20, TimeUnit.SECONDS)
        }
    }
}