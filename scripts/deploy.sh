#!/bin/bash
REPOSITORY=/home/ec2-user/app/step2

cd $REPOSITORY

# 현재 실행 중인 컨테이너 확인
running_containers=$(docker-compose ps -q)

# 실행 중인 컨테이너가 있으면 중지 및 삭제
if [ -n "$running_containers" ]; then
    echo "Stopping and removing existing containers..."
    docker-compose down
fi

# Docker Compose 실행
echo "Running Docker Compose..."
docker-compose up -d
