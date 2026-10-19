pipeline {
    agent any
    stages {
        stage ("git clone") {
            steps{
               git branch: 'main', credentialsId: '0c28846d-59bf-4bec-b496-5c5800cb2bfa', url: 'https://github.com/AbinayaBalaji23/Jenkins.git' 
            }
        }
        stage ("maven build") {
            environment {
                maven = tool name: 'Maven', type: 'maven'
            }
            steps{
               sh "${maven}/bin/mvn clean package"
            }
        }
        stage ("Sonar Scanner") {
            environment {
                sonar = tool name: 'Sonar', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
                pwd = credentials('sonar-pat')
            }
            
            steps{
               sh """
                    ${sonar}/bin/sonar-scanner \
                    -Dsonar.host.url=http://172.31.0.227:9000 \
                    -Dsonar.token="${pwd}" \
                    -Dsonar.projectKey=student-reg-webapp \
                    -Dsonar.projectName=student-reg-webapp \
                    -Dsonar.sources=src/main/java \
                    -Dsonar.java.binaries=target/classes \
                    -Dsonar.exclusions=**/test/**,**/generated-sources/**
                  """
            }
        }
        stage ("maven deploy") {
            environment {
                maven = tool name: 'Maven', type: 'maven'
            }
            steps{
               sh "${maven}/bin/mvn clean deploy"
            }
        }
        stage ("tomcat deploy") {
            steps {
                deploy adapters: [tomcat9(alternativeDeploymentContext: '', credentialsId: 'tomcat', path: '', url: 'http://16.171.26.188:8080')], contextPath: '/student-reg-web', war: '**/*.war'
            }
        }
    }
}
