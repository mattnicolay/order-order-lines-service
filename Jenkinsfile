pipeline {
   agent any
   stages {
      stage('Build') {
         steps {
            sh './gradlew clean build'
         }
      }
      stage('test') {
         steps {
            sh './gradlew test'
         }
      }
      stage('Deploy'){
         steps{
             sh 'cf push'
         }
      }
   }
}