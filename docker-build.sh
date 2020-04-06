#!/usr/bin/env bash

docker build . -f submiss-docker/Dockerfile -t nexus.eurodyn.com:15483/submiss
docker push nexus.eurodyn.com:15483/submiss
