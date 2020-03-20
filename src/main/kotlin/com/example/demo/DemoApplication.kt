package com.example.demo

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.socket.server.standard.ServerEndpointExporter

@SpringBootApplication
open class DemoApplication {

    @Bean
    open fun serverEndpointExporter(): ServerEndpointExporter {
        return ServerEndpointExporter()
    }
}

fun main(args: Array<String>) {
    SpringApplication
        .run(DemoApplication::class.java, *args)


}