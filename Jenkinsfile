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
    echo "An error occurred: ${err.getMessage()}"
    currentBuild.result = 'FAILURE'
} finally {
    def buildStatus = currentBuild.result ?: 'SUCCESS'
    def subject = "${env.JOB_NAME} - ${env.BUILD_NUMBER} - Build ${buildStatus}"

    def gradient = ''
    switch (buildStatus) {
        case 'SUCCESS':
            gradient = 'linear-gradient(to right, #4CAF50, #81C784)'  // Green
            break
        case 'FAILURE':
            gradient = 'linear-gradient(to right, #f44336, #e57373)'  // Red
            break
        case 'UNSTABLE':
            gradient = 'linear-gradient(to right, #ff9800, #ffcc80)'  // Orange
            break
        default:
            gradient = 'linear-gradient(to right, #9e9e9e, #cfd8dc)'  // Gray
    }

    def htmlBody = """
    <html>
    <body style="font-family: Arial, sans-serif;">
        <div style="padding: 20px; background: ${gradient}; color: white; border-radius: 10px;">
            <h2>Build ${buildStatus}</h2>
            <p><strong>Job:</strong> ${env.JOB_NAME}</p>
            <p><strong>Build Number:</strong> ${env.BUILD_NUMBER}</p>
            <p><strong>URL:</strong> <a style="color: white;" href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
        </div>
    </body>
    </html>
    """

    sendEmail(subject, htmlBody, 'praveen.kumar23346@gmail.com')
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
