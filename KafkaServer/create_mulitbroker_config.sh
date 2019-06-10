#!/bin/bash

# This script checks and creates config files for broker servers.
# Broker 1 reads from server.properties
# Broker 2 reads from server-1.properties
# Broker 3 reads from server-2.properties
# ... and so on.


KAFKA_HOME=~/kafka_2.12-2.2.0
CONFIG_PATH_BASE="$KAFKA_HOME/config/server"
CONFIG_FILE_TYPE=".properties"

cd "$KAFKA_HOME"

for i in {1..3}
do
	FILENAME="$CONFIG_PATH_BASE-$i$CONFIG_FILE_TYPE"
	if [ -f "$FILENAME" ]; then
		echo "File $FILENAME exists!"
	else
		echo "Creating File $FILENAME"
		cp "$CONFIG_PATH_BASE$CONFIG_FILE_TYPE" "$FILENAME"
		sleep 0.2
		# Set to the next id number
		while IFS= read -r line; 
		do
			if [[ $line == *"broker.id="* ]]; then 
				ID="$(echo $line | cut -d'=' -f2)"
				newID="$(echo "$(($ID + $i - 1))")"
				sleep 0.2
				sed -i '' "s/broker.id=$ID/broker.id=$newID/g" "$FILENAME"
				break
			fi
		done < "$FILENAME"
		
		# Set to next port number
		while IFS= read -r line; 
		do
			if [[ $line == *"listeners=PLAINTEXT://:"* ]]; then 
				ID="$(echo $line | cut -d':' -f3)"
				newID="$(echo "$(($ID + $i - 1))")"
				sed -i '' "s|PLAINTEXT://:$ID|PLAINTEXT://:$newID|g" "$FILENAME"
				break
			fi
		done < "$FILENAME"

		# Add balancing
		echo "controlled.shutdown.enable=true" >> "$FILENAME"
		echo "auto.leader.rebalance.enable=true" >> "$FILENAME"

		echo "File created."
	fi
done
