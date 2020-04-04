#!/usr/bin/env bash

CONTAINER_NAME=testspringcloud


docker stop $CONTAINER_NAME || true
docker rm $CONTAINER_NAME || true
docker rmi $CONTAINER_NAME:1 || true


docker build -t $CONTAINER_NAME:1 .
