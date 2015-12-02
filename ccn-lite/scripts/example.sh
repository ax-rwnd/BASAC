#!/bin/bash
current_time=$(date +%s)

#make sure that required environment vars are set
if [ $CCNL_HOME ]; then
	printf "CCNL_HOME is set to \"%s\"\n" $CCNL_HOME
else
	echo "You must CCNL_HOME to your CCN-lite root directory."
	exit
fi

if [ $CCNL_LOGS ]; then
	printf "CCNL_LOGS is set to \"%s\"\n" $CCNL_LOGS
else
	echo "You must set CCNL_LOGS to your log path."
	exit
fi

#run example
echo "Enter your input"
$CCNL_HOME/bin/ccn-lite-mkC -s ndn2013 "/ndn/test/mycontent" > $CCNL_HOME/test/ndntlv/mycontent.ndntlv

#using script to redirect output written to /dev/tty
echo "Starting relay A..."
script -q -c '$CCNL_HOME/bin/ccn-lite-relay -v trace -s ndn2013 -u 9998 -x /tmp/mgmt-relay-a.sock' /dev/null > $CCNL_LOGS/relay-a-$current_time.log &
sleep 2s

echo "Starting relay B..."
script -q -c '$CCNL_HOME/bin/ccn-lite-relay -v trace -s ndn2013 -u 9998 -x /tmp/mgmt-relay-b.sock -d $CCNL_HOME/test/ndntlv' /dev/null > $CCNL_LOGS/relay-b-$current_time.log &
sleep 2s

echo "Creating route..."
FACEID=`$CCNL_HOME/bin/ccn-lite-ctrl -x /tmp/mgmt-relay-a.sock newUDPface any 127.0.0.1 9999 \
  | $CCNL_HOME/bin/ccn-lite-ccnb2xml | grep FACEID | sed -e 's/^[^0-9]*\([0-9]\+\).*/\1/'`

echo "Setting route state..."
$CCNL_HOME/bin/ccn-lite-ctrl -x /tmp/mgmt-relay-a.sock prefixreg /ndn $FACEID ndn2013 \
  | $CCNL_HOME/bin/ccn-lite-ccnb2xml 

echo "Press any key to shutdown..."
read -n 1 -s

#shutdown
$CCNL_HOME/bin/ccn-lite-ctrl -x /tmp/mgmt-relay-a.sock debug halt | $CCNL_HOME/bin/ccn-lite-ccnb2xml
$CCNL_HOME/bin/ccn-lite-ctrl -x /tmp/mgmt-relay-b.sock debug halt | $CCNL_HOME/bin/ccn-lite-ccnb2xml
