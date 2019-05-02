pipeline {
    agent { 
	docker { 
		image 'maven:3.6.0' 
		args '-v /home/joao/.m2:/home/joao/.m2'
	} 
    }
    environment {
	MAVEN_OPTS = '-Duser.home=/home/joao'
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
