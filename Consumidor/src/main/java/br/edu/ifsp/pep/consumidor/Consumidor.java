package br.edu.ifsp.pep.consumidor;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class Consumidor {

    public static void main(String[] args) {

        String enderecoEnv = System.getenv("KAFKA_BOOTSTRAP_SERVERS");
        String ENDERECO_KAFKA = (enderecoEnv != null && !enderecoEnv.isEmpty()) ? enderecoEnv : "kafka:9092";
        String topicoEnv = System.getenv("KAFKA_TOPIC");
        String TOPICO = (topicoEnv != null && !topicoEnv.isEmpty()) ? topicoEnv : "fila-pedidos";

        Properties propriedades = new Properties();

        propriedades.put("bootstrap.servers", ENDERECO_KAFKA);
        propriedades.put("group.id", "grupo-simples");
        propriedades.put("auto.offset.reset", "earliest");

        propriedades.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");

        propriedades.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumidor =
                new KafkaConsumer<>(propriedades);

        consumidor.subscribe(Collections.singletonList(TOPICO));

        System.out.println("Consumidor esperando pedidos...");

        while (true) {

            ConsumerRecords<String, String> mensagens =
                    consumidor.poll(Duration.ofMillis(500));

            for (ConsumerRecord<String, String> mensagem : mensagens) {

                System.out.println("Recebi: " + mensagem.value());

            }
        }
    }
}
