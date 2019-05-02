pipeline {
    agent { docker { image 'maven:3.6.0' } }
    stages {
        stage('build') {
            steps {
                sh '''
		cd ./crypto-lib
		mvn clean compile install
		cd ..
		cd ./LocMessServer
		mvn clean compile'
		'''
            }
        }
    }
}
