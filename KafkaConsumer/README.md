### Run Instructions:

- Start the ZooKeeper server:

	$ cd kafka
	$ bin/zookeeper-server-start.sh config/zookeeper.properties

- Start the Kafka server:

	$ bin/kafka-server-start.sh config/server.properties
	
- Clean and build Producer and Consumer:

	$ ./gradlew clean build
	
- Run a Producer given a topic name:

	$ java -jar build/libs/KafkaProducer.CatProducer.java "topic name"

- Run a Consumer with a topic name:
	
	$ java -jar build/libs/KafkaProducer.CatProducer.GeneralConsumer.java "topic name"
				
- [OPTIONAL] Ask for existing topic names:

	$ java -jar build/libs/KafkaProducer.CatProducer.GeneralConsumer.java "--list"
