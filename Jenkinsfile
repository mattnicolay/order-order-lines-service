pipeline {
   agent any
   stages {
      stage('Build') {
         steps {
            sh './gradlew -Dskiptests clean build'
         }
      }
      stage('Test') {
         steps {
            sh './gradlew test'
         }
      }
      stage('Deploy'){
         steps{
            pushToCloudFoundry(
              cloudSpace: 'mnicolay-cnf',
              credentialsId: 'b7b468ca-d37e-40c7-ae4b-ffcfe4f7dd61',
              organization: 'solstice-cnf',
              target: 'api.run.pivotal.io'
            )
         }
      }
   }
}