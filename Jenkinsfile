node {
    def mavenHome= tool name: 'Maven-3.9.10', type: 'maven'
    stage("Git clone"){
        echo "Git clone"
        git branch: 'development', credentialsId: 'GitHubCred', url: 'https://github.com/PraveenKumar-Devops/student-reg-webapp.git'
    }
    stage("maven build with sonar") {
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
}
