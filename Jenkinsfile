node {
    def mavenHome= tool name: 'Maven-3.9.10', type: 'maven'

    try {
    stage("Git clone"){
        echo "Git clone"
        git branch: 'development', credentialsId: 'GitHubCred', url: 'https://github.com/PraveenKumar-Devops/student-reg-webapp.git'
    }
    stage("maven verify And Sonar Scan") {
        sh "${mavenHome}/bin/mvn clean package"
        sh "${mavenHome}/bin/mvn clean verify sonar:sonar"
    }

    stage("maven deploy") {
        sh "${mavenHome}/bin/mvn clean deploy"
    }
    stage("Stop Tomcat Server"){
        sshagent(['Tomcat_Server']){
            sh """
            echo Stopping the tomcat process
            ssh -o StrictHostKeyChecking=no ec2-user@172.31.10.81 sudo sh /opt/tomcat/bin/startup.sh
            sleep 10
            """
        }
    }
    stage("Deploy war file to Tomcat"){
     sshagent(['Tomcat_Server']) {
         sh "scp -o StrictHostKeyChecking=no target/student-reg-webapp.war ec2-user@172.31.10.81:/opt/tomcat/webapps/student-reg-webapp.war"
     }
    }
    stage("Start Tomcat"){
        sshagent(['Tomcat_Server']){
            sh """
            echo Starting the tomcat process
            ssh -o StrictHostKeyChecking=no ec2-user@172.31.10.81 sudo sh /opt/tomcat/bin/startup.sh
            """
         }
      }
    }catch (err) {
        echo "An error occurred: ${e.getMessage()}"
        currentBuild.result = 'FAILURE'
    } finally {
        def buildStatus = currentBuild.result ?: 'SUCCESS'
        def colorcode = buildStatus == 'SUCCESS' ? 'good' : 'danger'
        sendEmail(
           "${env.JOB_NAME} - ${env.BUILD_NUMBER} - Build ${buildStatus}",
           "Build ${buildStatus}. Please check the console output at ${env.BUILD_URL}",
           'praveen.kumar23346@gmail.com' )
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
