package com.example.demo.websocket

import org.junit.Assert
import org.junit.Test

internal class WebSocketTest{

    @Test
    fun test_is_piano_note(){
        Assert.assertTrue(WebSocket.isPianoNote("[data-key=\"65\"]"))
        Assert.assertTrue(WebSocket.isPianoNote("[data-key=\"83\"]"))
        Assert.assertTrue(WebSocket.isPianoNote("[data-key=\"68\"]"))
        Assert.assertTrue(WebSocket.isPianoNote("[data-key=\"70\"]"))
        Assert.assertTrue(WebSocket.isPianoNote("[data-key=\"71\"]"))
        Assert.assertTrue(WebSocket.isPianoNote("[data-key=\"72\"]"))
        Assert.assertTrue(WebSocket.isPianoNote("[data-key=\"74\"]"))
        Assert.assertTrue(WebSocket.isPianoNote("[data-key=\"75\"]"))
        Assert.assertTrue(WebSocket.isPianoNote("[data-key=\"76\"]"))
        Assert.assertTrue(WebSocket.isPianoNote("[data-key=\"186\"]"))
        Assert.assertTrue(WebSocket.isPianoNote("[data-key=\"87\"]"))
        Assert.assertTrue(WebSocket.isPianoNote("[data-key=\"69\"]"))
        Assert.assertTrue(WebSocket.isPianoNote("[data-key=\"84\"]"))
        Assert.assertTrue(WebSocket.isPianoNote("[data-key=\"89\"]"))
        Assert.assertTrue(WebSocket.isPianoNote("[data-key=\"85\"]"))
        Assert.assertTrue(WebSocket.isPianoNote("[data-key=\"79\"]"))
        Assert.assertTrue(WebSocket.isPianoNote("[data-key=\"80\"]"))

    }
}