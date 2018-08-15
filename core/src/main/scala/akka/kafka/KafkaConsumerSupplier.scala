package akka.kafka

import java.util.Properties

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.Deserializer

trait KafkaConsumerSupplier {
  def get[K, V](properties: Properties, keyDeserializer: Deserializer[K], valueDeserializer: Deserializer[V]): KafkaConsumer[K, V]
}

class DefaultKafkaConsumerSupplier extends KafkaConsumerSupplier {
  def get[K, V](properties: Properties, keyDeserializer: Deserializer[K], valueDeserializer: Deserializer[V]): KafkaConsumer[K, V] = {
    new KafkaConsumer[K, V](properties, keyDeserializer, valueDeserializer)
  }
}
