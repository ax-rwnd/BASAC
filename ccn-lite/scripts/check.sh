#!/bin/bash
#use this to check the content for basic_relay

$CCNL_HOME/bin/ccn-lite-peek -s ndn2013 -u 127.0.0.1/9998 "/ndn/test/mycontent" \
  | $CCNL_HOME/bin/ccn-lite-pktdump -f 2
