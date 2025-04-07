package com.example.demo.config

import com.timgroup.statsd.NonBlockingStatsDClient
import com.timgroup.statsd.StatsDClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StatsDConfig {

    @Bean
    fun statsDClient(): StatsDClient {
        return NonBlockingStatsDClient(
            "explorandes", // prefix
            "localhost",   // or your Datadog agent host
            8125           // default DogStatsD port
        )
    }
}
