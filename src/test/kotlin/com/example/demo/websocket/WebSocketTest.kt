package com.example.demo.websocket

import com.example.demo.DemoApplication
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.junit.jupiter.SpringExtension
import javax.websocket.Session


@ExtendWith(MockitoExtension::class, SpringExtension::class)
@SpringBootTest(classes = [DemoApplication::class], webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EmbeddedKafka(topics = ["Piano"], bootstrapServersProperty = "spring.kafka.bootstrap-servers")
class WebSocketTest{

    @Mock
    private lateinit var mockSession: Session


    @Test
    fun test_is_piano_note(){
        assertTrue(WebSocket.isPianoNote("[data-key=\"65\"]"))
        assertTrue(WebSocket.isPianoNote("[data-key=\"83\"]"))
        assertTrue(WebSocket.isPianoNote("[data-key=\"68\"]"))
        assertTrue(WebSocket.isPianoNote("[data-key=\"70\"]"))
        assertTrue(WebSocket.isPianoNote("[data-key=\"71\"]"))
        assertTrue(WebSocket.isPianoNote("[data-key=\"72\"]"))
        assertTrue(WebSocket.isPianoNote("[data-key=\"74\"]"))
        assertTrue(WebSocket.isPianoNote("[data-key=\"75\"]"))
        assertTrue(WebSocket.isPianoNote("[data-key=\"76\"]"))
        assertTrue(WebSocket.isPianoNote("[data-key=\"186\"]"))
        assertTrue(WebSocket.isPianoNote("[data-key=\"87\"]"))
        assertTrue(WebSocket.isPianoNote("[data-key=\"69\"]"))
        assertTrue(WebSocket.isPianoNote("[data-key=\"84\"]"))
        assertTrue(WebSocket.isPianoNote("[data-key=\"89\"]"))
        assertTrue(WebSocket.isPianoNote("[data-key=\"85\"]"))
        assertTrue(WebSocket.isPianoNote("[data-key=\"79\"]"))
        assertTrue(WebSocket.isPianoNote("[data-key=\"80\"]"))
    }

    @Test
    fun test_open_and_close() {
        var webSocket = WebSocket()
        doReturn("test").`when`(mockSession).id
        webSocket.onOpen(mockSession)
        assertEquals(WebSocket.sockets["test"], webSocket)

        webSocket.onClose(mockSession)
        assertFalse(WebSocket.sockets.containsKey("test"))
    }
}