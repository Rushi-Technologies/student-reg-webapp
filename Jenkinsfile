pipeline {
    
   agent any
   options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
    }

    triggers {
       githubPush()
    }

    environment{
        SONARQUBE_TOkEN = credentials('SonarQubeToken')
        SONAR_QUBE_URL = "http://172.31.44.188:9000/"
        TOMCAT_SERVERIP = "172.31.38.49"
        TOMCAT_SERVER_SSH_USER = "ec2-user"
    }
    
    tools {
         maven 'Maven-3.9.16'
    }
    
    stages{
        
        stage("Maven Build") {
            steps {
                sh 'mvn clean package'
            }
        }
        
        stage("Sonar Scan") {
            steps {
                withSonarQubeEnv('SonarQubeServer') {
                    sh "mvn sonar:sonar"
                }
            }
        }
        
        stage("WaitForSonarQubeQualityGate"){
            steps {
                 timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        
        stage("Upload Package To Nexus"){
            steps{
                sh "mvn clean deploy"
            }
        }
        
        stage("Deploy To Tomcat"){
            steps {
                sshagent(['TomcatServer_SSH_Credentails']) {
                   sh """ssh -o StrictHostKeyChecking=no ${TOMCAT_SERVER_SSH_USER}@${TOMCAT_SERVERIP} sudo systemctl stop tomcat
                      ssh -o StrictHostKeyChecking=no ${TOMCAT_SERVER_SSH_USER}@${TOMCAT_SERVERIP} sudo rm /opt/tomcat/webapps/student-reg-webapp*.war || true
                      ssh -o StrictHostKeyChecking=no ${TOMCAT_SERVER_SSH_USER}@${TOMCAT_SERVERIP} sudo rm -rf /opt/tomcat/webapps/student-reg-webapp || true
                      sleep 30s
                      scp -o StrictHostKeyChecking=no target/student-reg-webapp.war ${TOMCAT_SERVER_SSH_USER}@${TOMCAT_SERVERIP}:/opt/tomcat/webapps/student-reg-webapp.war
                      ssh -o StrictHostKeyChecking=no ${TOMCAT_SERVER_SSH_USER}@${TOMCAT_SERVERIP} sudo systemctl start tomcat"""
                 }
            }
        }
        
    }
    
    post {
        always {
          script {
             def buildStatus = currentBuild.result ?: 'SUCCESS'
             sendEmail(buildStatus)
             slackSend channel: '#lic-app-team', message: "Build ${buildStatus}. Please check the console output at ${env.BUILD_URL}"
           }
        }
    }
}

def sendEmail(String buildStatus) {
    String subject = "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - ${buildStatus}"
    String body = """<!DOCTYPE html>
                    <html>
                    <head>
                        <style>
                            body { font-family: Arial, sans-serif; }
                            .header { background-color: #f4f5f7; padding: 10px; border-bottom: 2px solid #ccc; }
                            .success { color: green; font-weight: bold; }
                            .failure { color: red; font-weight: bold; }
                            .content { margin-top: 15px; }
                        </style>
                    </head>
                    <body>
                        <div class="header">
                            <h2>Jenkins Build Report</h2>
                        </div>
                        <div class="content">
                            <p><strong>Job:</strong> ${env.JOB_NAME}</p>
                            <p><strong>Build Number:</strong> #${env.BUILD_NUMBER}</p>
                            <p><strong>Status:</strong> <span class="${buildStatus == 'SUCCESS' ? 'success' : 'failure'}">${buildStatus}</span></p>
                            <p>Check the full console output <a href="${env.BUILD_URL}">here</a>.</p>
                        </div>
                    </body>
                    </html>"""
    String recipient = 'balajireddy.urs@gmail.com'
    emailext body: body, subject: subject, to: recipient, mimeType: 'text/html'
}