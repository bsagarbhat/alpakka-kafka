/*
 * Copyright (C) 2014 - 2016 Softwaremill <http://softwaremill.com>
 * Copyright (C) 2016 - 2018 Lightbend Inc. <http://www.lightbend.com>
 */

package akka.kafka

import java.util.Optional
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.kafka.internal._
import com.typesafe.config.Config
import org.apache.kafka.clients.consumer.{Consumer, ConsumerConfig, KafkaConsumer}
import org.apache.kafka.common.serialization.Deserializer

import scala.collection.JavaConverters._
import scala.compat.java8.OptionConverters._
import scala.concurrent.duration._

object ConsumerSettings {

  /**
   * Create settings from the default configuration
   * `akka.kafka.consumer`.
   * Key or value deserializer can be passed explicitly or retrieved from configuration.
   * using default kafkaConsumerSupplier
   */
  def apply[K, V](
      system: ActorSystem,
      keyDeserializer: Option[Deserializer[K]],
      valueDeserializer: Option[Deserializer[V]]
  ): ConsumerSettings[K, V] = {
    val config = system.settings.config.getConfig("akka.kafka.consumer")
    apply(config, keyDeserializer, valueDeserializer, new DefaultKafkaConsumerSupplier)
  }

  /**
   * Create settings from the default configuration
   * `akka.kafka.consumer`.
   * Key or value deserializer can be passed explicitly or retrieved from configuration.
   * consumerSupplier must be passed explicitly
   */
  def apply[K, V](
      system: ActorSystem,
      keyDeserializer: Option[Deserializer[K]],
      valueDeserializer: Option[Deserializer[V]],
      consumerClientFactory: KafkaConsumerSupplier
  ): ConsumerSettings[K, V] = {
    val config = system.settings.config.getConfig("akka.kafka.consumer")
    apply(config, keyDeserializer, valueDeserializer, consumerClientFactory)
  }
  /**
   * Create settings from a configuration with the same layout as
   * the default configuration `akka.kafka.consumer`.
   * Key or value deserializer can be passed explicitly or retrieved from configuration.
   * using default kafkaConsumerSupplier
   */
  def apply[K, V](
      config: Config,
      keyDeserializer: Option[Deserializer[K]],
      valueDeserializer: Option[Deserializer[V]],
  ): ConsumerSettings[K, V] = {
    apply(config, keyDeserializer, valueDeserializer, new DefaultKafkaConsumerSupplier)
  }

  /**
   * Create settings from a configuration with the same layout as
   * the default configuration `akka.kafka.consumer`.
   * Key or value deserializer can be passed explicitly or retrieved from configuration.
   * consumerSupplier must be passed explicitly
   */
  def apply[K, V](
      config: Config,
      keyDeserializer: Option[Deserializer[K]],
      valueDeserializer: Option[Deserializer[V]],
      consumerClientSupplier: KafkaConsumerSupplier
  ): ConsumerSettings[K, V] = {
    val properties = ConfigSettings.parseKafkaClientsProperties(config.getConfig("kafka-clients"))
    require(
      keyDeserializer != null &&
      (keyDeserializer.isDefined || properties.contains(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG)),
      "Key deserializer should be defined or declared in configuration"
    )
    require(
      valueDeserializer != null &&
      (valueDeserializer.isDefined || properties.contains(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG)),
      "Value deserializer should be defined or declared in configuration"
    )
    val pollInterval = config.getDuration("poll-interval", TimeUnit.MILLISECONDS).millis
    val pollTimeout = config.getDuration("poll-timeout", TimeUnit.MILLISECONDS).millis
    val stopTimeout = config.getDuration("stop-timeout", TimeUnit.MILLISECONDS).millis
    val closeTimeout = config.getDuration("close-timeout", TimeUnit.MILLISECONDS).millis
    val commitTimeout = config.getDuration("commit-timeout", TimeUnit.MILLISECONDS).millis
    val commitTimeWarning = config.getDuration("commit-time-warning", TimeUnit.MILLISECONDS).millis
    val wakeupTimeout = config.getDuration("wakeup-timeout", TimeUnit.MILLISECONDS).millis
    val maxWakeups = config.getInt("max-wakeups")
    val commitRefreshInterval = config.getPotentiallyInfiniteDuration("commit-refresh-interval")
    val dispatcher = config.getString("use-dispatcher")
    val wakeupDebug = config.getBoolean("wakeup-debug")
    val waitClosePartition = config.getDuration("wait-close-partition", TimeUnit.MILLISECONDS).millis
    new ConsumerSettings[K, V](
      properties,
      keyDeserializer,
      valueDeserializer,
      pollInterval,
      pollTimeout,
      stopTimeout,
      closeTimeout,
      commitTimeout,
      wakeupTimeout,
      maxWakeups,
      commitRefreshInterval,
      dispatcher,
      commitTimeWarning,
      wakeupDebug,
      waitClosePartition,
      consumerClientSupplier
    )
  }

  /**
   * Create settings from the default configuration
   * `akka.kafka.consumer`.
   * Key and value serializer must be passed explicitly.
   * consumerSupplier must be passed explicitly
   */
  def apply[K, V](
      system: ActorSystem,
      keyDeserializer: Deserializer[K],
      valueDeserializer: Deserializer[V],
      consumerClientFactory: KafkaConsumerSupplier
  ): ConsumerSettings[K, V] =
    apply(system, Option(keyDeserializer), Option(valueDeserializer), consumerClientFactory)


  /**
   * Create settings from the default configuration
   * `akka.kafka.consumer`.
   * Key and value serializer must be passed explicitly.
   * using default kafkaConsumerSupplier
   */
  def apply[K, V](
      system: ActorSystem,
      keyDeserializer: Deserializer[K],
      valueDeserializer: Deserializer[V]
  ): ConsumerSettings[K, V] =
    apply(system, Option(keyDeserializer), Option(valueDeserializer), new DefaultKafkaConsumerSupplier)

  /**
   * Create settings from a configuration with the same layout as
   * the default configuration `akka.kafka.consumer`.
   * Key and value serializer must be passed explicitly.
   * consumerSupplier must be passed explicitly
   */
  def apply[K, V](
      config: Config,
      keyDeserializer: Deserializer[K],
      valueDeserializer: Deserializer[V],
      consumerSupplier: KafkaConsumerSupplier
  ): ConsumerSettings[K, V] =
    apply(config, Option(keyDeserializer), Option(valueDeserializer), consumerSupplier)

  /**
   * Create settings from a configuration with the same layout as
   * the default configuration `akka.kafka.consumer`.
   * Key and value serializer must be passed explicitly.
   * using default kafkaConsumerSupplier
   */
  def apply[K, V](
      config: Config,
      keyDeserializer: Deserializer[K],
      valueDeserializer: Deserializer[V],
  ): ConsumerSettings[K, V] =
    apply(config, Option(keyDeserializer), Option(valueDeserializer), new DefaultKafkaConsumerSupplier)

  /**
   * Java API: Create settings from the default configuration
   * `akka.kafka.consumer`.
   * Key or value deserializer can be passed explicitly or retrieved from configuration.
   * using default kafkaConsumerSupplier
   */
  def create[K, V](
      system: ActorSystem,
      keyDeserializer: Optional[Deserializer[K]],
      valueDeserializer: Optional[Deserializer[V]]
  ): ConsumerSettings[K, V] = create(system, keyDeserializer, valueDeserializer, new DefaultKafkaConsumerSupplier)

  /**
   * Java API: Create settings from the default configuration
   * `akka.kafka.consumer`.
   * Key or value deserializer can be passed explicitly or retrieved from configuration.
   * consumerSupplier must be passed explicitly
   */
  def create[K, V](
      system: ActorSystem,
      keyDeserializer: Optional[Deserializer[K]],
      valueDeserializer: Optional[Deserializer[V]],
      consumer: KafkaConsumerSupplier
  ): ConsumerSettings[K, V] =
    apply(system, keyDeserializer.asScala, valueDeserializer.asScala, consumer)

  /**
   * Java API: Create settings from a configuration with the same layout as
   * the default configuration `akka.kafka.consumer`.
   * Key or value deserializer can be passed explicitly or retrieved from configuration.
   * using default kafkaConsumerSupplier
   */
  def create[K, V](
      config: Config,
      keyDeserializer: Optional[Deserializer[K]],
      valueDeserializer: Optional[Deserializer[V]],
  ): ConsumerSettings[K, V] = create(config, keyDeserializer, valueDeserializer, new DefaultKafkaConsumerSupplier)

  /**
   * Java API: Create settings from a configuration with the same layout as
   * the default configuration `akka.kafka.consumer`.
   * Key or value deserializer can be passed explicitly or retrieved from configuration.
   * consumerSupplier must be passed explicitly
   */
  def create[K, V](
      config: Config,
      keyDeserializer: Optional[Deserializer[K]],
      valueDeserializer: Optional[Deserializer[V]],
      consumerSupplier: KafkaConsumerSupplier
  ): ConsumerSettings[K, V] =
    apply(config, keyDeserializer.asScala, valueDeserializer.asScala, consumerSupplier)

  /**
   * Java API: Create settings from the default configuration
   * `akka.kafka.consumer`.
   * Key and value serializer must be passed explicitly.
   * using default kafkaConsumerSupplier
   */
  def create[K, V](
      system: ActorSystem,
      keyDeserializer: Deserializer[K],
      valueDeserializer: Deserializer[V]
  ): ConsumerSettings[K, V] = create(system, keyDeserializer, valueDeserializer, new DefaultKafkaConsumerSupplier)

  /**
   * Java API: Create settings from the default configuration
   * `akka.kafka.consumer`.
   * Key and value serializer must be passed explicitly.
   * using default kafkaConsumerSupplier
   */
  def create[K, V](
      system: ActorSystem,
      keyDeserializer: Deserializer[K],
      valueDeserializer: Deserializer[V],
      consumer: KafkaConsumerSupplier
  ): ConsumerSettings[K, V] =
    apply(system, keyDeserializer, valueDeserializer, consumer)

  /**
   * Java API: Create settings from a configuration with the same layout as
   * the default configuration `akka.kafka.consumer`.
   * Key and value serializer must be passed explicitly.
   * using default kafkaConsumerSupplier
   */
  def create[K, V](
      config: Config,
      keyDeserializer: Deserializer[K],
      valueDeserializer: Deserializer[V]
  ): ConsumerSettings[K, V] = create(config, keyDeserializer, valueDeserializer, new DefaultKafkaConsumerSupplier)

  /**
   * Java API: Create settings from a configuration with the same layout as
   * the default configuration `akka.kafka.consumer`.
   * Key and value serializer must be passed explicitly.
   * consumerSupplier must be passed explicitly
   */
  def create[K, V](
      config: Config,
      keyDeserializer: Deserializer[K],
      valueDeserializer: Deserializer[V],
      consumer: KafkaConsumerSupplier
  ): ConsumerSettings[K, V] =
    apply(config, keyDeserializer, valueDeserializer, consumer)
}

/**
 * Settings for consumers. See `akka.kafka.consumer` section in
 * reference.conf. Note that the [[ConsumerSettings companion]] object provides
 * `apply` and `create` functions for convenient construction of the settings, together with
 * the `with` methods.
 */
class ConsumerSettings[K, V](
    val properties: Map[String, String],
    val keyDeserializerOpt: Option[Deserializer[K]],
    val valueDeserializerOpt: Option[Deserializer[V]],
    val pollInterval: FiniteDuration,
    val pollTimeout: FiniteDuration,
    val stopTimeout: FiniteDuration,
    val closeTimeout: FiniteDuration,
    val commitTimeout: FiniteDuration,
    val wakeupTimeout: FiniteDuration,
    val maxWakeups: Int,
    val commitRefreshInterval: Duration,
    val dispatcher: String,
    val commitTimeWarning: FiniteDuration = 1.second,
    val wakeupDebug: Boolean = true,
    val waitClosePartition: FiniteDuration,
    val consumerSupplier: KafkaConsumerSupplier
) {

  def withBootstrapServers(bootstrapServers: String): ConsumerSettings[K, V] =
    withProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)

  def withClientId(clientId: String): ConsumerSettings[K, V] =
    withProperty(ConsumerConfig.CLIENT_ID_CONFIG, clientId)

  def withGroupId(groupId: String): ConsumerSettings[K, V] =
    withProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId)

  /**
   * The raw properties of the kafka-clients driver, see constants in
   * `org.apache.kafka.clients.consumer.ConsumerConfig`.
   */
  def withProperties(properties: Map[String, String]): ConsumerSettings[K, V] =
    copy(properties = this.properties ++ properties)

  /**
   * The raw properties of the kafka-clients driver, see constants in
   * `org.apache.kafka.clients.consumer.ConsumerConfig`.
   */
  def withProperties(properties: (String, String)*): ConsumerSettings[K, V] =
    copy(properties = this.properties ++ properties.toMap)

  /**
   * The raw properties of the kafka-clients driver, see constants in
   * `org.apache.kafka.clients.consumer.ConsumerConfig`.
   */
  def withProperties(properties: java.util.Map[String, String]): ConsumerSettings[K, V] =
    copy(properties = this.properties ++ properties.asScala)

  /**
   * The raw properties of the kafka-clients driver, see constants in
   * `org.apache.kafka.clients.consumer.ConsumerConfig`.
   */
  def withProperty(key: String, value: String): ConsumerSettings[K, V] =
    copy(properties = properties.updated(key, value))

  /**
   * Java API: Get a raw property. `null` if it is not defined.
   */
  def getProperty(key: String): String = properties.getOrElse(key, null)

  def withPollTimeout(pollTimeout: FiniteDuration): ConsumerSettings[K, V] =
    copy(pollTimeout = pollTimeout)

  def withPollInterval(pollInterval: FiniteDuration): ConsumerSettings[K, V] =
    copy(pollInterval = pollInterval)

  def withStopTimeout(stopTimeout: FiniteDuration): ConsumerSettings[K, V] =
    copy(stopTimeout = stopTimeout)

  def withCloseTimeout(closeTimeout: FiniteDuration): ConsumerSettings[K, V] =
    copy(closeTimeout = closeTimeout)

  def withCommitTimeout(commitTimeout: FiniteDuration): ConsumerSettings[K, V] =
    copy(commitTimeout = commitTimeout)

  def withCommitWarning(commitTimeWarning: FiniteDuration): ConsumerSettings[K, V] =
    copy(commitTimeWarning = commitTimeWarning)

  def withWakeupTimeout(wakeupTimeout: FiniteDuration): ConsumerSettings[K, V] =
    copy(wakeupTimeout = wakeupTimeout)

  def withDispatcher(dispatcher: String): ConsumerSettings[K, V] =
    copy(dispatcher = dispatcher)

  def withMaxWakeups(maxWakeups: Int): ConsumerSettings[K, V] =
    copy(maxWakeups = maxWakeups)

  def withCommitRefreshInterval(commitRefreshInterval: Duration): ConsumerSettings[K, V] =
    copy(commitRefreshInterval = commitRefreshInterval)

  def withWakeupDebug(wakeupDebug: Boolean): ConsumerSettings[K, V] =
    copy(wakeupDebug = wakeupDebug)

  def withWaitClosePartition(waitClosePartition: FiniteDuration): ConsumerSettings[K, V] =
    copy(waitClosePartition = waitClosePartition)

  private def copy(
      properties: Map[String, String] = properties,
      keyDeserializer: Option[Deserializer[K]] = keyDeserializerOpt,
      valueDeserializer: Option[Deserializer[V]] = valueDeserializerOpt,
      pollInterval: FiniteDuration = pollInterval,
      pollTimeout: FiniteDuration = pollTimeout,
      stopTimeout: FiniteDuration = stopTimeout,
      closeTimeout: FiniteDuration = closeTimeout,
      commitTimeout: FiniteDuration = commitTimeout,
      commitTimeWarning: FiniteDuration = commitTimeWarning,
      wakeupTimeout: FiniteDuration = wakeupTimeout,
      maxWakeups: Int = maxWakeups,
      commitRefreshInterval: Duration = commitRefreshInterval,
      dispatcher: String = dispatcher,
      wakeupDebug: Boolean = wakeupDebug,
      waitClosePartition: FiniteDuration = waitClosePartition,
      consumerClientFactory: KafkaConsumerSupplier = consumerSupplier
  ): ConsumerSettings[K, V] =
    new ConsumerSettings[K, V](
      properties,
      keyDeserializer,
      valueDeserializer,
      pollInterval,
      pollTimeout,
      stopTimeout,
      closeTimeout,
      commitTimeout,
      wakeupTimeout,
      maxWakeups,
      commitRefreshInterval,
      dispatcher,
      commitTimeWarning,
      wakeupDebug,
      waitClosePartition,
      consumerClientFactory
    )

  /**
   * Create a `KafkaConsumer` instance from the settings.
   */
  def createKafkaConsumer(): Consumer[K, V] = {
    val javaProps = properties.foldLeft(new java.util.Properties) {
      case (p, (k, v)) => p.put(k, v); p
    }
    consumerSupplier.get(javaProps, keyDeserializerOpt.orNull, valueDeserializerOpt.orNull)
  }
}
