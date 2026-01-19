package com.heima.article.core.config;


import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.HashMap;
import java.util.Map;


@Getter
@Setter
@Configuration
@EnableKafkaStreams
@ConfigurationProperties(prefix = "kafka")
public class KafkaStreamConfig {

    private static final int MAX_MESSAGE_SIZE = 16* 1024 * 1024;
    private String hosts;
    private String group;

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration defaultKafkaStreamsConfig(){
        Map<String, Object> map = new HashMap<>();
        map.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        map.put(StreamsConfig.APPLICATION_ID_CONFIG, this.getGroup()+"-stream-v2");
        map.put(StreamsConfig.CLIENT_ID_CONFIG, this.getGroup()+"-stream");
        map.put(StreamsConfig.RETRIES_CONFIG, 10);
        map.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        map.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        map.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new KafkaStreamsConfiguration(map);
    }


}
