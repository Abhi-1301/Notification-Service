#!/bin/bash

directory="Deployments"

kubectl apply -f "$directory/mysql-deployment.yaml" \
              -f "$directory/zookeeper-deployment.yaml" \
              -f "$directory/elasticsearch-deployment.yaml" \
              -f "$directory/kafka-deployment.yaml" \
              -f "$directory/redis-deployment.yaml"

sleep 15

kubectl apply -f "$directory/app-deployment.yaml"

# minikube service notification --url
