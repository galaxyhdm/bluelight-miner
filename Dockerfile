# Stage 1: Build binaries
FROM gradle:jdk14 AS build

ARG SNAPSHOT=true
ARG BASE=development

ENV snap $SNAPSHOT
ENV base $BASE

#### BUILD JAR ####
WORKDIR /app
ADD . .
RUN gradle build

# Stage 2: Final binaries and final setup
FROM openjdk:14-jdk-slim AS final

#Set Env variables
ENV DEBUG false
ENV POOL_SIZE 2

WORKDIR /app
COPY --from=build app/build/libs/miner-*-withDependencies.jar miner.jar
COPY --from=build app/run.sh run.sh

#Install curl apt-transport-https lsb-release gpg
RUN apt update && apt install curl apt-transport-https lsb-release gpg -y

#Install tor socket
RUN echo "deb https://deb.torproject.org/torproject.org/ $(lsb_release -cs) main" > /etc/apt/sources.list.d/tor.list
RUN curl https://deb.torproject.org/torproject.org/A3C4F0F979CAA22CDBA8F512EE8CBC9E886DDD89.asc | gpg --import
RUN gpg --export A3C4F0F979CAA22CDBA8F512EE8CBC9E886DDD89 | apt-key add -
RUN apt update && apt install tor tor-geoipdb torsocks deb.torproject.org-keyring -y

# Backup openssl.cnf and set SECLEVEL=1
RUN cp /etc/ssl/openssl.cnf /etc/ssl/openssl.cnf.save
RUN sed -i 's/DEFAULT@SECLEVEL=2/DEFAULT@SECLEVEL=1/g' /etc/ssl/openssl.cnf

#### RUN SETTINGS ####
RUN mkdir -p /app/work
RUN ["chmod", "+x", "/app/run.sh"]

ENTRYPOINT ["/bin/bash", "-c", "/app/run.sh"]