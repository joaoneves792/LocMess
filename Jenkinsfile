pipeline {
    agent { 
	docker { 
		image 'maven:3.6.0' 
		args '-v /root/.m2:/root/.m2'
	} 
    }
    stages {
        stage('build') {
            steps {
                sh '''
		cd ./crypto-lib
		mvn clean compile install
		cd ..
		cd ./LocMessServer
		mvn clean compile
		'''
            }
        }
    }
}
