#!/bin/bash

# This script does the following:
# Gets current partition replica assignment and execute the proposed assignment
#   1. gets the current topic partition assignments
#   2. parses to get the proposed partition assignments
#   3. executes the proposed assignments
#   4. verifies the reassignments
#   5. applies the reassignments to the brokers

# Assumptions:
#   Zookeeper is online
#   All Kafka servers (brokers) in question are running
#   topics-to-move.json file already created or successfully parsed below


SERVER=localhost:9092
ZKSERVER=localhost:2181


KAFKA_HOME=~/kafka_2.12-2.2.0

cd "$KAFKA_HOME"

OUTPUT="topics-to-move.json"
REASSIGN="cluster-reassign.json"
ROLLBACK="rollback-topics-to-move.json"

touch ${OUTPUT}
touch ${REASSIGN}
touch ${ROLLBACK}

bin/kafka-topics.sh \
        --zookeeper ${ZKSERVER} \
        --list
        > all_topics

# TODO: the commands below may not work on all systems
grep -v all_topics \
        | tr '\n' ' ' \
        | jq -R 'split(" ") | reduce .[] as $topic ([]; . + [{"topic": $topic }]) | {"topics": . , "version": 1}' \
        > ${OUTPUT}


# If topics-to-move.json fails to be created, then manually create using the following format:
#    {"topics": [{"topic": "fish1"},
#                {"topic": "fish2"},
#                {"topic": "fish3"},
#                {"topic": "fish4"},
#                {"topic": "fish5"},
#                {"topic": "fish6"},
#                {"topic": "fish7"},
#                {"topic": "fish8"},
#                {"topic": "fish9"},
#                {"topic": "fish10"}],
#     "version":1
#    }


# Generate candidate assignment config
bin/kafka-reassign-partitions.sh \
		--zookeeper ${ZKSERVER} \
		--topics-to-move-json-file ${OUTPUT} \
		--broker-list "0,1,2,3,4,5,6,7,8,9" \
		--generate

cat ${OUTPUT}

# TODO: find a better way to get the output
# possible sol: if the output from generate gives the json as a single string, just parse for the last string
lineNO=$(awk '/Proposed/{ print NR; exit}' ${OUTPUT})
totalNO=$(wc -l ${OUTPUT} | awk '{print $1}')
refNO=$((totalNO-lineNO+1))
sleep 1.0
tail "-${refNO}" ${OUTPUT} > ${REASSIGN}


# Execute the reassignment
bin/kafka-reassign-partitions.sh \
		--zookeeper ${ZKSERVER} \
		--reassignment-json-file "$REASSIGN" \
		--execute >> "$ROLLBACK"

# Verify rebalance/reassignment
VERIFY="verify-reassignment.txt"
touch ${VERIFY}
bin/kafka-reassign-partitions.sh \
		--zookeeper ${ZKSERVER} \
		--reassignment-json-file "$REASSIGN" \
		--verify >> "$VERIFY"

flag=0
while IFS= read -r line;
do
	echo "$line"
	if [[ ! $line = *"completed successfully"* ]]; then
		flag=1
	fi
done < "$VERIFY"

if [[ ${flag} = 1 ]]; then
	echo "Failure during reassignment"
else
	echo "Running preferred replica election tool:"
	bin/kafka-preferred-replica-election.sh \
		--zookeeper ${ZKSERVER}
	bin/kafka-topics.sh \
		--describe \
		--bootstrap-server ${SERVER}
fi
