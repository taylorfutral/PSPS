
(* This script does each ofthe following in a new tab in Mac Terminal:
 	1. Start ZooKeeper
	2. Start Broker 0
	3. Start Broker 1
 	4. Start Broker 2

This assumes that the respective server configs have been modified appropriately
(e.g. broker-id, listener-port, balancers set to true, ...)
*)

set cmd1 to "cd ~/kafka_2.12-2.2.0"
set startZK to "bin/zookeeper-server-start.sh config/zookeeper.properties"
set startBR1 to "bin/kafka-server-start.sh config/server.properties"
set startBR2 to "bin/kafka-server-start.sh config/server-1.properties"
set startBR3 to "bin/kafka-server-start.sh config/server-2.properties"
set win1 to 1
set win2 to 2
set win3 to 3
set win4 to 4

tell application "Terminal"
	activate
	my makeTab()
	set custom title of window win1 to "win1-ZK"
	my makeTab()
	set custom title of window win2 to "win2-B1"
	my makeTab()
	set custom title of window win3 to "win3-B2"
	my makeTab()
	set custom title of window win4 to "win4-B3"

	do script cmd1 in tab 1 of the window win1
	do script startZK in tab 1 of the window win1
	do script cmd1 in tab 1 of the window win2
	do script startBK1 in tab 1 of the window win2
	do script cmd1 in tab 1 of the window win3
	do script startBK2 in tab 1 of the window win3
	do script cmd1 in tab 1 of the window win4
	do script startBK3 in tab 1 of the window win4

end tell

on makeTab()
	tell application "System Events" to tell process "Terminal" to keystroke "t" using command down 
	delay 0.2
end makeTab

(*
-- To run directly in ther terminal instead, follow this format:

osascript -e 'tell application "Terminal" to activate'
	-e 'tell application "System Events" to tell process "Terminal" to keystroke "t" using command down' 
	-e 'tell application "Terminal" to do script "echo hello" in selected tab of the front window'
*)