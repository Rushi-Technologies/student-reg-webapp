pipeline {
    agent any 
    tools {
        maven 'Maven_3.8.8'

    }

    options {
          buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')
          disableConcurrentBuilds()
          timeout(time: 10, unit: 'MINUTES')
    }
    environment {
        GIT_BRANCH = ''
        GIT_CREDENTIALS = ''
        SONARQUBE_TOKEN = ""
        TOMCAT_IP_ADDRESS = "18.212.79.240"
        TOMCAT_USER_NAME = "ec2-user"
    }

    triggers {
        githubPush()
    }
    stages{
            stage("git clone"){
                steps{
                    git branch: 'main', credentialsId: 'GIT-credentials', url: 'https://github.com/Manoj0919/student-reg-webapp'
                    
                    echo "Git clone completed"
                }
            }
            stage("Maven build tool"){
                steps{
                    sh " mvn clean package "
                }   
            }
            stage ("SonarQube") {
                steps{
                    withCredentials([string(credentialsId: 'SonarToken', variable: 'SONAR_TOKEN')]) {
 
                        sh """
                        mvn clean verify sonar:sonar -Dsonar.token=${SONAR_TOKEN}
                        """
                    }
                }
            }
            stage ("Deploy Artifact to nexus") {
                steps{
                    sh"mvn clean deploy"
                }
            }
            stage ("DEPLOY TO TOMCAT") {
                steps{
                    sshagent(['SSH-to-tomcatserver']) {
                        sh """
                        ssh -o StrictHostKeyChecking=no ${TOMCAT_USER_NAME}@${TOMCAT_IP_ADDRESS} "sudo systemctl stop tomcat"
                        sleep 25
                        ssh -o StrictHostKeyChecking=no ${TOMCAT_USER_NAME}@${TOMCAT_IP_ADDRESS} "rm /opt/tomcat/webapps/student-reg-webapp.war" || true
                        scp -o StrictHostKeyChecking=no target/student-reg-webapp.war ${TOMCAT_USER_NAME}@${TOMCAT_IP_ADDRESS}:/opt/tomcat/webapps/student-reg-webapp.war
                        ssh -o StrictHostKeyChecking=no ${TOMCAT_USER_NAME}@${TOMCAT_IP_ADDRESS} "sudo systemctl start tomcat"
                        """
   
                        }
                }
            }
            
            
        }
    post {
        always{
             cleanWs()
        }
        success {
             sendEmail(
                "${env.JOB_NAME} - ${env.BUILD_NUMBER} - Build SUCCESS",
                "Build SUCCESS. Please check the console output at ${env.BUILD_URL}", 'devopsmanu1909@gmail.com'
                 )
    }
        failure{
            sendEmail(
                "${env.JOB_NAME} - ${env.BUILD_NUMBER} - Build FAILED",
                "Build FAILED. Please check the console output at ${env.BUILD_URL}",'devopsmanu1909@gmail.com'
                 )
    }
    }

}
def sendEmail(String subject, String body, String recipient) {
    emailext(
        subject: subject,
        body: body,
        to: recipient,
        mimeType: 'text/html'
    )
}