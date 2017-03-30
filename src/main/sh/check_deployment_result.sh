#!/usr/bin/env bash

C_USER=`whoami`

function report()
{
  logger "$1"
  echo "$1"
}

function check_app_status()
{

  if [[ $2 > 0 ]];
  then
    HTTP_PORT=$2
  else
    HTTP_PORT=8080
  fi

  case "$1" in
    distalk*)
      CHECK_URL=http://127.0.0.1:$HTTP_PORT/menos/health
      ;;
    *)
     report "No CHECK_URL defined for $1, FATAL"
     exit 6
     ;;
  esac

  COUNTER=0
  echo "Checking deployment state ... : "
  unset http_proxy
  unset HTTP_PROXY
  while [[ true ]];
  do
    COUNTER=$((COUNTER+1))
    RESULT_TMP=/tmp/deployit-wget-result-$RANDOM.txt
    /usr/bin/wget --no-check-certificate --secure-protocol=SSLv3 -O $RESULT_TMP $WGET_FLAGS $CHECK_URL
    if [[ $? == 0 ]];
    then
      report "Deployment of $1 successful"
      rm $RESULT_TMP
      return 0
    else
      echo "Not OK (yet)"
      cat $RESULT_TMP && rm $RESULT_TMP
    fi

    if [[ $COUNTER -gt 30 ]];
    then
      report "Application $1 is not available after $COUNTER tries. My grandmother is quicker than this. I'm done waiting. Giving up"
      exit 1
    fi

    echo -n "$COUNTER"
    sleep 1
  done
}

CHECK_STATE=1

HTTP_PORT=`grep "Connector port=" /opt/tomcat-instances/$C_USER/conf/server.xml | grep 'protocol="HTTP/1.1"' | awk '{ print $2; }' | sed -e 's/port="//' | tr -d '"'`
if [[ $? > 0 ]];
then
  CHECK_STATE=0
  report "Unable to determine http port for tomcat instance, not checking deployment results, WARNING"
fi

if [[ $CHECK_STATE > 0 ]];
then
    check_app_status $C_USER $HTTP_PORT
fi

exit 0
