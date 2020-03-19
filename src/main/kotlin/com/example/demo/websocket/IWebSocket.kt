package com.example.demo.websocket

interface IWebSocket {
    fun sendMessage(pianoMessage: String)

    fun getId(): String
}