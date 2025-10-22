 node {  
        def mavenHome = tool name: 'Maven-3.9.11', type: 'maven'
        def tomcatSystemIP="172.31.19.130"
        def tomcatserverSSHUserName="ec2-user"
        
        try { 
            
        stage("Git Clone") {
            git branch: 'development', credentialsId: 'GitHubCred', url: 'https://github.com/Rushi-Technologies/student-reg-webapp.git'
        }
        
        stage("Maven Build") {
            sh "${mavenHome}/bin/mvn clean package"
        }
        
        stage("Maven Verify And Sonar Scan") {
          withCredentials([string(credentialsId: 'SonarQubeToken', variable: 'SonarToken')]) {
            sh """
                ${mavenHome}/bin/mvn clean verify sonar:sonar -Dsonar.token=${SonarToken}
               """
          }      
        }
        
        stage("Maven Deploy") {
            sh "${mavenHome}/bin/mvn clean deploy"
        }
        
        stage("Deploy War To Tomcat"){
            sshagent(['TomcatServer_SSH_Credetails']) {
                sh """
                    ssh -o StrictHostKeyChecking=no ${tomcatserverSSHUserName}@${tomcatSystemIP} sudo systemctl stop tomcat
                    sleep 20
                    ssh -o StrictHostKeyChecking=no ${tomcatserverSSHUserName}@${tomcatSystemIP} rm /opt/tomcat/webapps/student-reg-webapp.war
                    scp -o StrictHostKeyChecking=no target/student-reg-webapp.war ${tomcatserverSSHUserName}@${tomcatSystemIP}:/opt/tomcat/webapps/student-reg-webapp.war
                    ssh -o StrictHostKeyChecking=no ${tomcatserverSSHUserName}@${tomcatSystemIP} sudo systemctl start tomcat
                """
            }
        }
     } catch(err){
       currentBuild.result = 'FAILURE'
     } finally {
       def buildStatus = currentBuild.result ?: 'SUCCESS'    
       emailext body: """
                    <html>
                    <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                        <h2 style="color: #2d87f0;">Jenkins Build Notification</h2>
                        <p><strong>Build Result:</strong> ${buildStatus}</p>
                        <p><strong>Build Number:</strong> ${env.BUILD_NUMBER}</p>
                        <p><strong>Project:</strong> ${env.JOB_NAME}</p>
                        <p><strong>Build URL:</strong> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                        <p>For more details, please visit the Jenkins job page.</p>
                    </body>
                    </html>
                    """,subject: "${env.JOB_NAME} - ${env.BUILD_NUMBER} - Build ${buildStatus}", mimeType: 'text/html',to: 'balajireddy.urs@gmail.com'
     }
} 