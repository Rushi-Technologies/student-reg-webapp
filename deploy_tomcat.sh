#!/bin/bash

# This script deploys a WAR file to multiple Tomcat servers using SSH and SCP.
# Credentials are managed by Jenkins sshagent, not hardcoded here.

TOMCAT_SERVER_IPS="$1"
if [ -z "$TOMCAT_SERVER_IPS" ]; then
    echo "Error: TOMCAT_SERVER_IPS parameter is required."
    exit 1
fi
WAR_FILE="target/student-reg-webapp.war"
DEST_PATH="/opt/tomcat/webapps/student-reg-webapp.war"
USER="ec2-user"

IFS=',' read -ra IP_ARRAY <<< "$TOMCAT_SERVER_IPS"

for ip in "${IP_ARRAY[@]}"; do
    echo "Stopping Tomcat on $ip"
    ssh -o StrictHostKeyChecking=no $USER@$ip "sudo systemctl stop tomcat"
    sleep 30
    echo "Copying WAR file to $ip"
    scp -o StrictHostKeyChecking=no $WAR_FILE $USER@$ip:$DEST_PATH
    echo "Starting Tomcat on $ip"
    ssh -o StrictHostKeyChecking=no $USER@$ip "sudo systemctl start tomcat"
done
