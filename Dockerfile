FROM archlinux/base:latest AS build
WORKDIR /
RUN pacman -Sy --noconfirm \
    archlinux-keyring  && \
    pacman -Sy --noconfirm \
    jdk8-openjdk \
    maven && \
    pacman -Scc --noconfirm

ADD ./crypto-lib /crypto-lib/
ADD ./LocMessServer /LocMessServer/

ENV JAVA_HOME "/usr/lib/jvm/java-8-openjdk/"

RUN cd /crypto-lib && \
    mvn clean install && \
    cd /LocMessServer && \
    mvn clean compile install spring-boot:repackage


FROM alpine:edge AS deployment
RUN apk add --no-cache openjdk8-jre
COPY --from=build /LocMessServer/target/LocMessServer-1.0-SNAPSHOT-spring-boot.jar /LocMessServer.jar
ENTRYPOINT ["java", "-jar", "/LocMessServer.jar"]
