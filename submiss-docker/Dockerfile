FROM openjdk:8u131-jre-alpine
MAINTAINER submiss@eurodyn.com

ENV DOCKERIZE_VERSION v0.5.0

COPY submiss-dist/target/submiss-*.tar.gz /tmp
COPY submiss-docker/org.ops4j.pax.logging.cfg /tmp

RUN apk update && \
apk add ca-certificates wget && \
update-ca-certificates && \
wget http://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz && \
tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz && \
mkdir -p /var/lib/submiss && \
tar xfz /tmp/submiss*.tar.gz -C /var/lib/submiss --strip 1 && \
rm /tmp/submiss* && \
cp /tmp/org.ops4j.pax.logging.cfg /var/lib/submiss/etc

CMD /var/lib/submiss/bin/karaf