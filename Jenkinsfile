pipeline {
    agent none
    environment {
	MAVEN_OPTS = '-Duser.home=/home/joao'
    }
    stages {
        stage('build') {
    	    agent { 
	        docker { 
		    image 'maven:3.6.0' 
		    args '-v /home/joao/.m2:/home/joao/.m2'
		} 
	    }
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
	stage('test') {
    	    agent { 
	        docker { 
		    image 'maven:3.6.0' 
		    args '-v /home/joao/.m2:/home/joao/.m2'
		} 
	    }
	    steps {
	        sh '''
		cd ./LocMessServer
		mvn test
		'''
	    }
	}
	stage('production_image'){
    	    agent { 
	        label 'docker-agent'
	    }
	    steps {
            sh '''
	    docker build -t locmess:production .
	    '''
            }
	}
    }
    post {
        always {
            echo 'FINISHED'
	}
	success {
	    echo 'Ready to deploy!'
        }
    }
}
