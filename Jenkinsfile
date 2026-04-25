@Library('JenkinsSharedLib')_
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
       def branchName = ''
       /* def branch = script { sh(script: "git rev-parse --abbrev-ref HEAD", returnStdout: true).trim() }
    */
    }

    triggers {
        githubPush()
    }
    stages{
            stage('Set Branch Name') {
                steps {
                    script {
                        branchName = getbranchname ()
                        echo "Branch name is: ${branchName}"
                     }
                }
            }

             stage('Use Branch Name') {
                steps {
                        echo "Branch name is: ${branchName}"
                }
            }
            stage("git clone"){
                steps{
                    git branch: 'main', credentialsId: 'GIT-credentials', url: 'https://github.com/Manoj0919/student-reg-webapp'
                    
                    echo "Git clone completed"
                }
            }
            stage("Maven build tool"){
                steps{
                    mavenaction( 'package' )
                    echo "Branch name is: ${branchName} "
      
                }   
            }
            stage ("parallel run "){
                parallel {
                    stage ("SonarQube") {
                        steps{
                            withCredentials([string(credentialsId: 'SonarToken', variable: 'SONAR_TOKEN')]) {
        
                                sh """
                                mvn clean verify sonar:sonar -Dsonar.token=${SONAR_TOKEN}
                                """
                                 echo " Code verification done"
                            }
                        }
                    }
                    stage ("Deploy Artifact to nexus") {
                        steps{
                            sh"mvn clean deploy" 
                            sh "echo 'deploy to nexus'"
                             echo " ${branchName}  "
                        }
                    }
                }
            }       
            stage("DEPLOY TO TOMCAT") {
                when {
                    expression{ branchName == "main" }
                }
                    steps {
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
            stage("SKIP DEPLOYMENT") {
                when {
                    expression { branchName != "main" }       
                }
                steps {
                     echo "Skipped due to the Branch name is: ${branchName} "
      
                    echo "This is not the main branch. Skipping deployment."
                }
            }
   
            
        }
        post {
            always{
                sendemail(currentBuild.currentResult,"devopsmanu1909@gmail.com")
                cleanWs()
            }
       
    }

}
