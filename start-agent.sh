#!/bin/sh
JENKINS_URL=${JENKINS_URL:-http://jenkins-master:8080}
echo "Waiting for Jenkins Master at $JENKINS_URL..."
until curl -f $JENKINS_URL/login; do
  echo "Jenkins not ready, waiting..."
  sleep 10
done
echo "Jenkins is ready, downloading agent..."
wget -O agent.jar $JENKINS_URL/jnlpJars/agent.jar
echo "Starting agent..."
java -jar agent.jar -url $JENKINS_URL -secret "$JENKINS_SECRET" -name "$AGENT_NAME" -workDir /home/jenkins/agent