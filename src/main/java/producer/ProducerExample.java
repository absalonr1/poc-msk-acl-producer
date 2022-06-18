/**
 * Copyright 2020 Confluent Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.Producer;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;


import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.common.errors.TopicExistsException;
import java.util.Properties;
import java.util.Collections;
import java.util.Optional;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * mvn install
 * export MY_POD_IP=localhost
 * java -jar target/simple-producer-bx-1.0.0-jar-with-dependencies.jar
 * 
 * 
 * docker build -t simple-producer-bx .
 * docker tag simple-producer-bx absalon1000rr/simple-producer-bx:v2
 * docker push absalon1000rr/simple-producer-bx:v6
 */

public class ProducerExample {

  private static final Logger log = Logger.getLogger(ProducerExample.class); 

  // Create topic in Confluent Cloud
  public static void createTopic(final String topic,
                          final Properties cloudConfig) {
      final NewTopic newTopic = new NewTopic(topic, Optional.empty(), Optional.empty());
      try (final AdminClient adminClient = AdminClient.create(cloudConfig)) {
          adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
      } catch (final InterruptedException | ExecutionException e) {
          // Ignore if TopicExistsException, which may be valid if topic exists
          if (!(e.getCause() instanceof TopicExistsException)) {
              throw new RuntimeException(e);
          }
      }
  }

  public static void main(final String[] args) throws IOException {
    
    Logger rootLogger = Logger.getRootLogger();
    rootLogger.setLevel(Level.INFO);
    
    //Define log pattern layout
    PatternLayout layout = new PatternLayout("%d{ISO8601} [%t] %-5p %c %x - %m%n");
    
    //Add console appender to root logger
    rootLogger.addAppender(new ConsoleAppender(layout));

    // Load properties from a local configuration file
    // Create the configuration file (e.g. at '$HOME/.confluent/java.config') with configuration parameters
    // to connect to your Kafka cluster, which can be on your local host, Confluent Cloud, or any other cluster.
    // Follow these instructions to create this file: https://docs.confluent.io/platform/current/tutorials/examples/clients/docs/java.html
    String topicName =  "excargonet_topic2";
      
    // username="bx.user.msk.dev"
    // password="cqGVch0Zmh3ou0UoWJRbZyq01qYcUR4I";
    String uname="user";
    String password="pass";

    String host="b-1.testingcluster.047th7.c8.kafka.us-west-2.amazonaws.com:9096,b-2.testingcluster.047th7.c8.kafka.us-west-2.amazonaws.com:9096";

    Properties props = new Properties();
    // Datos de conexion

    props.put("sasl.mechanism","SCRAM-SHA-512");
    props.put("acks","all");
    props.put("bootstrap.servers",host);
    props.put("security.protocol", "SASL_SSL");
    props.put("sasl.jaas.config","org.apache.kafka.common.security.scram.ScramLoginModule required username='"+uname+"' password='"+password+"';");
    // Define el "consumer group" ID
    props.put("group.id", "producer-test");


    // Si kafka no recive un heratbeat durante mas de
    // 50000 milisecs, entonces este consumer se dara por
    // "muerto" produciendose un rebalaning
    props.put("session.timeout.ms", "50000");


    log.info("Configuracion: "+props);
    log.info("topicName: "+topicName);

    // Create topic if needed
    // final String topic = args[1];
    // createTopic(topic, props);

    // Add additional properties.
    props.put(ProducerConfig.ACKS_CONFIG, "all");
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

    Producer<String, String> producer = new KafkaProducer<String, String>(props);

    // Produce sample data
    final Long numMessages = 10L;
    for (Long i = 0L; i < numMessages; i++) {
      String key = "alice"+i;

      System.out.printf("Producing record: %s\t%s%n", key, "record"+i);
      producer.send(new ProducerRecord<String, String>(topicName, key, "record"+i), new Callback() {
          @Override
          public void onCompletion(RecordMetadata m, Exception e) {
            if (e != null) {
              e.printStackTrace();
            } else {
              System.out.printf("Produced record to topic %s partition [%d] @ offset %d%n", m.topic(), m.partition(), m.offset());
            }
          }
      });
    }

    producer.flush();

    System.out.printf("10 messages were produced to topic %s%n", topicName);

    producer.close();
  }

}