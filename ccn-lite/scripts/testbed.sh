#!/bin/bash
#pkt.end

current_time=$(date +%s)
echo "The current time is: $current_time"

#using script to redirect output written to /dev/tty
echo "Starting relay A..."
script -q -c '$CCNL_HOME/bin/ccn-lite-relay -v trace -s ndn2013 -u 9998 -x /tmp/mgmt-relay-a.sock' /dev/null > $CCNL_LOGS/relay-a-$current_time.log &
sleep 2s

#setup face to university of basel
echo "Setting up face..."
FACEID=`$CCNL_HOME/bin/ccn-lite-ctrl -x /tmp/mgmt-relay-a.sock newUDPface any 192.43.193.111 6363 \
  | $CCNL_HOME/bin/ccn-lite-ccnb2xml | grep FACEID | sed -e 's/^[^0-9]*\([0-9]\+\).*/\1/'`


#register prefix
echo "Registering face..."
$CCNL_HOME/bin/ccn-lite-ctrl -x /tmp/mgmt-relay-a.sock prefixreg /ndn $FACEID ndn2013 \
  | $CCNL_HOME/bin/ccn-lite-ccnb2xml


#send interest to A
echo "Adding interest to relay A..."
script -q -c '$CCNL_HOME/bin/ccn-lite-peek -s ndn2013 -u 127.0.0.1/9998 -w 10 "/ndn/edu/ucla" | $CCNL_HOME/bin/ccn-lite-pktdump' /dev/null > $CCNL_LOGS/testbed-$current_time.log &

sleep 1s
echo "Your data should be written to $CCNL_LOGS/testbed-$current_time.log"
sleep 1s

#shutdown
$CCNL_HOME/bin/ccn-lite-ctrl -x /tmp/mgmt-relay-a.sock debug halt | $CCNL_HOME/bin/ccn-lite-ccnb2xml
