#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

REPOSITORY=/home/ec2-user/app/cd
PROJECT_NAME=mvc-service

cp $REPOSITORY/zip/*.jar $REPOSITORY/
JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)
chmod +x $JAR_NAME

IDLE_PROFILE=$(find_idle_profile)

nohup java -jar -Dspring.config.location=classpath:/application.yml,classpath:/application-$IDLE_PROFILE.yml,/home/ec2-user/app/application-real-db.yml -Dspring.profiles.active=$IDLE_PROFILE $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &
