package com.woo.proxypool.config;

import com.bugsnag.Bugsnag;
import com.github.jkutner.EnvKeyStore;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.*;
import static java.lang.String.format;
import static java.lang.System.getenv;
@Slf4j
@Configuration
public class KafkaConfig {

    @Autowired
    Environment environment;

    @Autowired
    Bugsnag bugsnag;

    private Map<String, Object> buildDefaults() {
        Map<String, Object> properties = new HashMap<>();
        List<String> hostPorts = new ArrayList<>();

        for (String url : Objects.requireNonNull("kafka+ssl://ec2-3-220-121-33.compute-1.amazonaws.com:9096,kafka+ssl://ec2-3-208-232-40.compute-1.amazonaws.com:9096,kafka+ssl://ec2-100-24-239-47.compute-1.amazonaws.com:9096,kafka+ssl://ec2-35-168-34-44.compute-1.amazonaws.com:9096,kafka+ssl://ec2-100-25-107-37.compute-1.amazonaws.com:9096,kafka+ssl://ec2-3-234-75-100.compute-1.amazonaws.com:9096,kafka+ssl://ec2-3-227-129-191.compute-1.amazonaws.com:9096,kafka+ssl://ec2-3-231-110-127.compute-1.amazonaws.com:9096").split(",")) {
            try {
                URI uri = new URI(url);
                hostPorts.add(format("%s:%d", uri.getHost(), uri.getPort()));

                switch (uri.getScheme()) {
                    case "kafka":
                        properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "PLAINTEXT");
                        break;
                    case "kafka+ssl":
                        properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");


                        try {
                            log.info(getenv("KAFKA_TRUSTED_CERT"));
                            EnvKeyStore envTrustStore = EnvKeyStore.createWithRandomPassword("KAFKA_TRUSTED_CERT");
                            EnvKeyStore envKeyStore = EnvKeyStore.createWithRandomPassword("KAFKA_CLIENT_CERT_KEY", "KAFKA_CLIENT_CERT");

                            File trustStore = envTrustStore.storeTemp();
                            File keyStore = envKeyStore.storeTemp();

                            properties.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, envTrustStore.type());
                            properties.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, trustStore.getAbsolutePath());
                            properties.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, envTrustStore.password());
                            properties.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, envKeyStore.type());
                            properties.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, keyStore.getAbsolutePath());
                            properties.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, envKeyStore.password());
                            properties.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");
                        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
                            bugsnag.notify(e);
                            throw new RuntimeException("There was a problem creating the Kafka key stores", e);
                        }
                        break;
                    default:
                        throw new IllegalArgumentException(format("unknown scheme; %s", uri.getScheme()));
                }
            } catch (URISyntaxException e) {
                bugsnag.notify(e);
                throw new RuntimeException(e);
            }
        }

        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, String.join(",", hostPorts));
        return properties;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> config = buildDefaults();

        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> config = buildDefaults();
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "xyz");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
