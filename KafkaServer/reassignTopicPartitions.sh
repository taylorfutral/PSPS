#!/bin/bash

# Gets current partition replica assignment and execute the proposed assignment

KAFKA_HOME=~/kafka_2.12-2.2.0

cd "$KAFKA_HOME"
OUTPUT="topics-to-move.json"
REASSIGN="cluster-reassign.json"
ROLLBACK="rollback-topics-to-move.json"

# Generate candidate assignment config
bin/kafka-reassign-partitions.sh \
		--boostrap-server "localhost:9092" \
		--topics-to-move "$OUTPUT" \
		--brokers "0,1,2" \
		--generate >> "$OUTPUT"

awk '/Proposed partition reassignment configuration{n++}{print > "$REASSIGN" }' "$OUTPUT"

bin/kafka-reassign-partitions.sh \
		--boostrap-server "localhost:9092" \
		--reassignment-json-file "$REASSIGN" \
		--execute >> "$ROLLBACK"

# Verify rebalance/reassignment
VERIFY="verify-reassignment.txt"
bin/kafka-reassign-partitions.sh \
		--boostrap-server "localhost:9092" \
		--reassignment-json-file "$REASSIGN" \
		--verify >> "$VERIFY"

flag=0
while IFS= read -r line; 
do
	echo "$line"
	if [[ $line == *"completed sucessfully"* ]]; then 
		# do nothing
	else
		flag=1
	fi
done < "VERIFY"
if [flag == 1]; then
	echo "Failure during reassignment"
else 
	echo "Running preferred replica election tool:"
	bin/kafka-prefered-replica-election.sh \
		--boostrap-server "localhost:9092" 
	bin/kafka-topics.sh \
		--describe \
		--boostrap-server "localhost:9092" 
fi
