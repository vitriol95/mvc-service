#!/usr/bin/env bash
ABSPATH=$(readlink -f $0) # 심볼릭 링크가 연결된 파일의 경로 가져오기(현재 파일)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

IDLE_PORT=$(find_idle_port)

IDLE_PID=$(lsof -ti tcp:${IDLE_PORT})

if [ -z ${IDLE_PID} ]
then
  echo "> 구동중인 Application 없음"
else
  kill -15 ${IDLE_PID}
  sleep 5
fi
