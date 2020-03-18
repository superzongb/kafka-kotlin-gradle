package com.example.demo.websocket

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class WebSocketTest{

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

    @BeforeEach
    fun setUp() {
    }


}