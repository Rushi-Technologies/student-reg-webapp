pipeline {
    
    agent any
    
    triggers {
       githubPush()
     }

    options {
      buildDiscarder logRotator(numToKeepStr: '5')
      timeout(time: 10, unit: 'MINUTES')
      disableConcurrentBuilds()
    }
    
    environment {
        SONARQUBE_URL = "http://13.57.206.25:9000"
        SONAR_QUBE_TOKEN = credentials('SonarToken')
        TOMCAT_SERVER_IP = "172.31.21.97"
    }
    
    tools {
        maven 'Maven-3.9.10'
    }

    stages {


       stage("Maven Clean Package"){
           steps {
               sh "mvn clean package"
           }
       }
       
      stage("Sonar Scan"){
           steps {
            sh "mvn sonar:sonar -Dsonar.url=${SONARQUBE_URL} -Dsonar.token=${SONAR_QUBE_TOKEN}"
          }
      }
      
      stage("Upload War To Nexus"){
          steps {
              sh "mvn clean deploy"
          }
      }
      
       stage("Deployt To Dev Server") {
        when {
            expression {
                return env.BRANCH_NAME == 'development'
            }
        }
        steps{
    
            sshagent(['Tomcat_Server']) {
                sh """
                     ssh -o  StrictHostKeyChecking=no ec2-user@${TOMCAT_SERVER_IP} sudo systemctl stop tomcat
                     echo Stoping the Tomcat Process
                     sleep 30
                     scp -o  StrictHostKeyChecking=no target/student-reg-webapp.war ec2-user@${TOMCAT_SERVER_IP}:/opt/tomcat/webapps/student-reg-webapp.war
                     echo Copying the War file to Tomcat Server
                     ssh -o  StrictHostKeyChecking=no ec2-user@${TOMCAT_SERVER_IP} sudo systemctl start tomcat
                     echo Strating the Tomcat process
                   """
            }
        }
      }
      
       
   }
   
   post {
        always {
            cleanWs()
        }
        success {
        slackSend (channel: 'lic-appteam', color: "good", message: "Build - SUCCESS : ${env.JOB_NAME} #${env.BUILD_NUMBER} - URL: ${env.BUILD_URL}")
          sendEmail(
           "${env.JOB_NAME} - ${env.BUILD_NUMBER} - Build SUCCESS",
           "Build SUCCESS. Please check the console output at ${env.BUILD_URL}",
           'balajireddy.urs@gmail.com' )
        }
        failure {
         slackSend (channel: 'lic-appteam', color: "danger", message: "Build - FAILED : ${env.JOB_NAME} #${env.BUILD_NUMBER} - URL: ${env.BUILD_URL}")    
         sendEmail(
           "${env.JOB_NAME} - ${env.BUILD_NUMBER} - Build FAILED",
           "Build FAILED. Please check the console output at ${env.BUILD_URL}",
           'balajireddy.urs@gmail.com' )
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
