#!/bin/sh
#-------------------------------------------------------------------------------
# Copyright (C) 2017 Create-Net / FBK.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License 2.0
# which accompanies this distribution, and is available at
# https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0
# 
# Contributors:
#     Create-Net / FBK - initial API and implementation
#-------------------------------------------------------------------------------

MODULE=${1:-all}

TOEXPORT=""

if [ ! -z "$DISPLAY" ]; then
  echo ">> DISPLAY available, reusing current display"
else
  export DISPLAY=:0
  TOEXPORT="\n$TOEXPORT\nexport DISPLAY=$DISPLAY"
fi

ME=`whoami`

if [ ! -z "$DBUS_SESSION_BUS_ADDRESS" ]; then
  echo ">> DBUS_SESSION_BUS_ADDRESS available, reusing current instance"
else

  if [ `pgrep -U $ME dbus-daemon -c` -gt 0 ]; then

    echo ">> DBus session available"

    MID=`sed "s/\n//" /var/lib/dbus/machine-id`
    DISPLAYID=`echo $DISPLAY | sed "s/://"`
    SESSFILEPATH="/home/$ME/.dbus/session-bus/$MID-$DISPLAYID"

    if [ -e $SESSFILEPATH ]; then
      echo ">> Loading DBus session instance address from local file"
      echo ">> Source: $SESSFILEPATH"
      . "$SESSFILEPATH"
    else
      echo "Cannot get Dbus session address. Panic!"
    fi

  else
    export `dbus-launch`
    sleep 2
    echo "++ Started a new DBus session instance"
  fi

fi

TOEXPORT="\n$TOEXPORT\nexport DBUS_SESSION_BUS_ADDRESS=$DBUS_SESSION_BUS_ADDRESS"

if [ -z "$DBUS_SESSION_BUS_ADDRESS" ]; then
  echo "!! Cannot export DBUS_SESSION_BUS_ADDRESS. Exit"
  exit 1
fi
export DBUS_SESSION_BUS_ADDRESS

export MAVEN_OPTS_BASE="-DDISPLAY=$DISPLAY -DDBUS_SESSION_BUS_ADDRESS=$DBUS_SESSION_BUS_ADDRESS"
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/lib:/usr/lib:/usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt/jre/lib/arm

mvn="mvn"

if [ $MODULE = 'all' ] || [ $MODULE = 'DLinkNotifier' ]; then
  ./scripts/stop.sh "microservice.DLinkNotifier"

  # wait for ProtocolManager to initialize
  while `! qdbus org.eclipse.agail.ProtocolManager > /dev/null`; do
    echo "waiting for ProtocolManager to initialize";
    sleep 1;
  done

   java -cp org.eclipse.agail.microservice.DLinkNotifier/target/agile-dlink-notifier-1.0.0-jar-with-dependencies.jar org.eclipse.agail.microservice.dlinknotifier.DLinkNotifier &
  echo "Started AGILE DLink notifier"
fi


echo "Module launched use this variables in the shell:"
echo $TOEXPORT
echo ""

wait
