def call(Map sonarqube_scan=[:]) {
  env.scannerHome = tool 'SonarQubeScanner'
    withSonarQubeEnv('SonarQube') {
      bat "mvn sonar:sonar"
      bat 'pwd'
    }
}
