#!/bin/bash


docker pull daylak/app:latest

docker pull postgres:15-alpine

docker compose up -d

docker attach lab2-docker-app-1