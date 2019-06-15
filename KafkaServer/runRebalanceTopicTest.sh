#!/usr/bin/env bash

# This script does the following:
#   1. create topics fish1 to fish10 with 5 partitions and 3 replics
#   2. push 10 (identical) images to each of the newly created topics
#   3. check the current balance of topic partitions
#   4. simulate network failure by killing 25% of brokers (1 of 4)
#   5. check the auto rebalance of leadership for topics
#   6. call and execute the reassignTopicPartitions script
#   7. checks the results of rebalancing

# Assumptions:
#   Zookeeper is online
#   3 Kafka servers (brokers) are running
#   4th config file already created


# Set defaults
SERVER=localhost:9092
NUM_REPLICATES=3
NUM_PARTITIONS=5
#topicName="fish"

# get current dir to access other scripts
SCRIPTS_DIR=$( pwd )
PRODUCER_DIR=~/cs237/PhotoCloud/KafkaProducer
IMG_DIR=~/pic
KAFKA_HOME=~/kafka_2.12-2.2.0


# Starts a 4th Kafka server (to be killed later)
# TODO: send it to the background so it doesnt terminate yet
cd ${KAFKA_HOME}
bin/kafka-server-start.sh config/server-3.properties &
PID=$(echo $!)
echo ${PID}

# Create 10 topics
# For each topic, push a image 10 times
for i in {1..10}
#for i in 3
do
    topicName="fish${i}"
    echo ${topicName}

    # TODO: check topic doesn't already exists

    cd ${KAFKA_HOME}
    bin/kafka-topics.sh --create \
            --bootstrap-server ${SERVER} \
            --replication-factor ${NUM_REPLICATES} \
            --partitions ${NUM_PARTITIONS} \
            --topic ${topicName}

    cd ${PRODUCER_DIR}
    java -jar ${PRODUCER_DIR}/build/libs/KafkaProducer.jar --topic ${topicName} --dir ${IMG_DIR}

done

# Check the topic partition balance
cd ${KAFKA_HOME}
bin/kafka-topics.sh --describe \
		--bootstrap-server ${SERVER}

# Kill 1 server (25% broker failure)
kill -9 ${PID}

# Check the topic partition balance
bin/kafka-topics.sh --describe \
		--bootstrap-server ${SERVER}

# Start back up 4th Kafka server
# TODO: send it to the background so it doesnt terminate yet
bin/kafka-server-start.sh config/server-3.properties &
PID=$(echo $!)

# Reassign and rebalance partitions
cd ${SCRIPTS_DIR}
time sh reassignTopicPartitions.sh

# Check the topic partition balance after
cd ${KAFKA_HOME}
bin/kafka-topics.sh --describe \
		--bootstrap-server ${SERVER}

