#!/bin/bash

DOCKERHUB_USERNAME="daylak" 
IMAGE_NAME="app"
TAG="latest"

docker build -t $DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG .

docker login

docker push $DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG