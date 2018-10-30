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

ARG BASEIMAGE_BUILD=agileiot/raspberry-pi3-zulujdk:8-jdk-maven
ARG BASEIMAGE_DEPLOY=agileiot/raspberry-pi3-zulujdk:8-jre

FROM $BASEIMAGE_BUILD

# Add packages
RUN apt-get update && apt-get install --no-install-recommends -y \
    ca-certificates \
    apt \
    software-properties-common \
    maven \
    gettext \
    pkg-config \
    qdbus \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# resin-sync will always sync to /usr/src/app, so code needs to be here.
WORKDIR /usr/src/app
ENV APATH /usr/src/app

COPY scripts scripts

# copy directories into WORKDIR
COPY org.eclipse.agail.microservice.DLinkNotifier org.eclipse.agail.microservice.DLinkNotifier

RUN cd org.eclipse.agail.microservice.DLinkNotifier && mvn package

FROM $BASEIMAGE_DEPLOY
WORKDIR /usr/src/app
ENV APATH /usr/src/app

RUN apt-get update && apt-get install --no-install-recommends -y \
    qdbus \
    && apt-get clean && rm -rf /var/lib/apt/lists/*


COPY --from=0 $APATH/scripts scripts
COPY --from=0 $APATH/org.eclipse.agail.microservice.DLinkNotifier/target/agile-dlink-notifier-1.0.0-jar-with-dependencies.jar org.eclipse.agail.microservice.DLinkNotifier/target/agile-dlink-notifier-1.0.0-jar-with-dependencies.jar

CMD [ "bash", "/usr/src/app/scripts/start.sh" ]
