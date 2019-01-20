FROM openjdk:8-jdk-slim

RUN apt-get update && apt-get install -y python3-requests make git

# Caching step; ForgeGradle requires a fairly significant number
# of downloads that don't vary unless build.gradle changes. We
# improve overall build time form 4m+ to 30s by doing this as part
# of standard image build
COPY gradle/ /tmp/gradle/
COPY build.gradle gradle* /tmp/
RUN (cd /tmp && ./gradlew setupCiWorkspace build)

RUN mkdir /prospects
COPY . /prospects
