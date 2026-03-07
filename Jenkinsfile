pipeline {

    agent any

    tools {
        maven 'Maven-3.9.12'
    }

    options {
         disableConcurrentBuilds()
         timeout(time: 180, unit: 'SECONDS')
         buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '5', numToKeepStr: '10')
    }



    environment {
        SONARQUBE_TOKEN = credentials('SonarQube_token')
        SONARQUBE_HOST_URL = 'http://172.31.17.228:9000'
        Tomcatserverip = "172.31.41.31"
        Tomcatusername = "ec2-user"
    }

    triggers {
        githubPush()
    }

    stages {
        stage ('checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/deepakby-123/student-reg-webapp.git'
            }
        }
        stage ('maven build') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage ('sonarqube analysis') {
            steps {
                sh "mvn sonar:sonar -Dsonar.url=${SONARQUBE_HOST_URL} -Dsonar.token=${SONARQUBE_TOKEN}"
            }
        }
        stage ('upload war to Nexus') {
            steps {
                sh "mvn clean deploy"
            }
        }
        stage ('Deploy to tomcat') {
            steps{
                sshagent(['Tomcat_ssh-cred']) {
                sh "ssh -o StrictHostKeyChecking=no ${Tomcatusername}@${Tomcatserverip} sudo systemctl stop tomcat"
                sh "sleep 20"
                sh "ssh -o StrictHostKeyChecking=no ${Tomcatusername}@${Tomcatserverip} rm -f /opt/tomcat/webapps/student-reg-webapp.war"
                sh "scp -o StrictHostKeyChecking=no target/student-reg-webapp.war ${Tomcatusername}@${Tomcatserverip}:/opt/tomcat/webapps/student-reg-webapp.war"
                sh "ssh -o StrictHostKeyChecking=no ${Tomcatusername}@${Tomcatserverip} sudo systemctl stop tomcat"
                }
               
            }
        }
    }
  
  post {

        always {
            cleanWs()
        }

        success {
            slackSend channel: 'all-deepak', color: '#36a64f',
            message: "Jenkins Job ${env.JOB_NAME}-${env.BUILD_NUMBER}-SUCCESS. Check console output at ${env.BUILD_URL}"

            sendEmail("SUCCESS")
        }

        failure {
            slackSend channel: 'all-deepak', color: '#FF0000',
            message: "Jenkins Job ${env.JOB_NAME}-${env.BUILD_NUMBER}-FAILURE. Check console output at ${env.BUILD_URL}"

            sendEmail("FAILURE")
        }
    }
}

def sendEmail(String Buildstatus) {

    emailext(
        subject: "${env.JOB_NAME}-${env.BUILD_NUMBER}-${Buildstatus}",
        to: "bydeepak6@gmail.com",
        mimeType: "text/html",
        body: """
        <html>
        <body>
            <h2>Build Result</h2>
            <p>Job: ${env.JOB_NAME}</p>
            <p>Status: ${Buildstatus}</p>
            <p><a href="${env.BUILD_URL}">Build Logs</a></p>
        </body>
        </html>
        """
    )
}