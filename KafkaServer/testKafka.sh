for i in 1 2 5 10 25 50 100
do
    output="$(bin/kafka-topics.sh --zookeeper 0.0.0.0:9092 --alter --topic testTopic1 --partitions $i)"
    echo "${output}"
    echo "altering to $i partitions"
    read -p "type 'quit' to quit, other press enter to continue " input;
    if [ "$input" = "quit" ]
    then 
	break
    fi
done
