package com.example.demo.kafka


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
open class PianoConsumers {
    @Autowired
    lateinit var consumers: MutableList<PianoConsumer>

    fun retreveConsumer(id: Int): PianoConsumer {
        return consumers[id%2].active()
    }
}