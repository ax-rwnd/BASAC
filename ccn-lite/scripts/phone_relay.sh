#!/bin/bash
#Use this for demonstrating ccn-lite for android

current_time=$(date +%s)

echo "Starting relay A..."
script -q -c '$CCNL_HOME/bin/ccn-lite-relay -v warning -s ndn2013' /dev/null > $CCNL_LOGS/android-$current_time.log &
sleep 2s

#get IP from user
read -p "Enter android IP: " address
#echo "Got address: $address"

echo "Creating route..."
FACEID=`$CCNL_HOME/bin/ccn-lite-ctrl newUDPface any $address 6363 \
  | $CCNL_HOME/bin/ccn-lite-ccnb2xml | grep FACEID | sed -e 's/^[^0-9]*\([0-9]\+\).*/\1/'`

#echo "FACEID was $FACEID"

echo "Setting route state..."
$CCNL_HOME/bin/ccn-lite-ctrl prefixreg /ndn $FACEID ndn2013 \
  | $CCNL_HOME/bin/ccn-lite-ccnb2xml 

echo "Press any key to shutdown..."
read -n 1 -s

#shutdown
$CCNL_HOME/bin/ccn-lite-ctrl debug halt | $CCNL_HOME/bin/ccn-lite-ccnb2xml
