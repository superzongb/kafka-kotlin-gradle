package com.example.demo

import com.example.demo.util.SpringContextUtil
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ImportResource
import org.springframework.web.socket.server.standard.ServerEndpointExporter

@SpringBootApplication
@ImportResource(locations = arrayOf("classpath:spring/consumers.xml"))
open class DemoApplication {

    @Bean
    open fun serverEndpointExporter(): ServerEndpointExporter {
        return ServerEndpointExporter()
    }
}

fun main(args: Array<String>) {
    SpringContextUtil
        .setApplicationContext(SpringApplication
            .run(DemoApplication::class.java, *args))


}