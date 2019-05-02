pipeline {
    agent { docker { image 'maven:3.6.0' } }
    stages {
        stage('build') {
            steps {
                sh 'cd ./LocMessServer;  mvn clean compile'
            }
        }
    }
}
