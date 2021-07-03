#!/bin/bash

REPOSITORY=/home/ec2-user/app/ci
PROJECT_NAME=mvc-service

cp $REPOSITORY/zip/*.jar $REPOSITORY/
CURRENT_PID=$(pgrep -fl api_aws | grep java | awk '{print $1}')

echo "> pid: $CURRENT_PID"

if [-z "$CURRENT_PID"]; then
  echo "어플리케이션 바로 실행"
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)
chmod +x $JAR_NAME
nohup java -jar -Dsping.config.location=classpath:/application.yml,/home/ec2-user/app/application-real-db.yml -Dspring.profiles.active=real-db $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &