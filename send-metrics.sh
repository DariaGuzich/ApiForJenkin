#!/bin/bash

# Script to send test results to InfluxDB
# Usage: ./send-metrics.sh [surefire-reports-path]

SUREFIRE_REPORTS_PATH=${1:-"target/surefire-reports"}

echo "Sending test metrics to InfluxDB..."
echo "Reports path: $SUREFIRE_REPORTS_PATH"

# Check if reports directory exists
if [ ! -d "$SUREFIRE_REPORTS_PATH" ]; then
    echo "Error: Surefire reports directory not found: $SUREFIRE_REPORTS_PATH"
    exit 1
fi

# Check if there are any XML test result files
if [ -z "$(ls -A $SUREFIRE_REPORTS_PATH/TEST-*.xml 2>/dev/null)" ]; then
    echo "Warning: No test result XML files found in $SUREFIRE_REPORTS_PATH"
    exit 0
fi

# Set InfluxDB connection properties if not already set
export INFLUXDB_URL=${INFLUXDB_URL:-"http://localhost:8086"}
export INFLUXDB_TOKEN=${INFLUXDB_TOKEN:-"my-super-secret-auth-token"}
export INFLUXDB_ORG=${INFLUXDB_ORG:-"test-org"}
export INFLUXDB_BUCKET=${INFLUXDB_BUCKET:-"test-results"}

# Run the metrics sender
java -cp "target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)" \
     -Dinfluxdb.url="$INFLUXDB_URL" \
     -Dinfluxdb.token="$INFLUXDB_TOKEN" \
     -Dinfluxdb.org="$INFLUXDB_ORG" \
     -Dinfluxdb.bucket="$INFLUXDB_BUCKET" \
     -DBUILD_NUMBER="${BUILD_NUMBER:-unknown}" \
     -DJOB_NAME="${JOB_NAME:-local}" \
     metrics.InfluxDBMetrics "$SUREFIRE_REPORTS_PATH"

echo "Metrics sending completed"