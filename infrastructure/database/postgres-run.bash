#!/usr/bin/env bash

CONTAINER_NAME=testspringcloud

docker stop $CONTAINER_NAME || true
docker rm $CONTAINER_NAME || true

docker run -d -e POSTGRES_PASSWORD=$CONTAINER_NAME --name $CONTAINER_NAME -p 5432:5432  $CONTAINER_NAME:1

echo "To see the logs execute this: "
echo "docker logs -f $CONTAINER_NAME"
