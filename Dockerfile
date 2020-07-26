# Stage 1: Build binaries
FROM gradle:jdk14 AS build

# Set build-args
ARG snapshot_build=true
ARG base_ref=development

#### BUILD JAR ####
WORKDIR /app
ADD . .
RUN gradle build -Dbase_branch=$base_ref -Dsnapshot=$snapshot_build

# Stage 2: Final binaries and final setup
FROM markusk00/jdk-tor:latest AS final

# Set Env variables
ENV DEBUG false
ENV POOL_SIZE 2

WORKDIR /app
COPY --from=build app/build/libs/miner-*-withDependencies.jar miner.jar
COPY --from=build app/run.sh run.sh

#### RUN SETTINGS ####
RUN mkdir -p /app/work
RUN ["chmod", "+x", "/app/run.sh"]

ENTRYPOINT ["/bin/bash", "-c", "/app/run.sh"]