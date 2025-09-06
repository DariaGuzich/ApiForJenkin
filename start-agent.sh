#!/bin/sh
echo "Waiting for Jenkins Master..."
until curl -f http://localhost:8080/login; do
  echo "Jenkins not ready, waiting..."
  sleep 10
done
echo "Jenkins is ready, downloading agent..."
wget -O agent.jar http://localhost:8080/jnlpJars/agent.jar
echo "Starting agent..."
java -jar agent.jar -url http://localhost:8080 -secret "$JENKINS_SECRET" -name "$AGENT_NAME" -workDir /home/jenkins/agent