#!/usr/bin/env bash

# Nginx와 연결되어 있지 않은 프로필을 가져오기.
function find_idle_profile()
{
    RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost/profile)
    if [ ${RESPONSE_CODE} -ge 400 ] # 400 보다 크면 에러

    then
        CURRENT_PROFILE=real2
    else
        CURRENT_PROFILE=$(curl -s http://localhost/profile)
    fi
    # 현재 활동중인 PROFILE 찾음.
    if [ ${CURRENT_PROFILE} == real1 ]
    then
        IDLE_PROFILE=real2
    else
        IDLE_PROFILE=real1
    fi

    echo "${IDLE_PROFILE}"
}

function find_idle_port()
{
    IDLE_PROFILE=$(find_idle_profile)
    if [ ${IDLE_PROFILE} == real1 ]
    then
      echo "8081"
    else
      echo "8082"
    fi
}