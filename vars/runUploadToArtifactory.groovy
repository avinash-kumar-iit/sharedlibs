def maven(Map config) {
         withCredentials([usernamePassword(credentialsId: 'jenkins', passwordVariable: 'password', usernameVariable: 'user')]){
                  bat "echo env.JAVA_HOME"
                  //env.JAVA_HOME = (config["javaHome"]==null) ? "${home}" : config["javaHome"]
                  bat "echo ${user}"
                  //env.JAVA_HOME = "/usr/lib/jvm/java-17-openjdk-17.0.5.0.8-2.el8_6.x86_64"
                   if (config["javaHome"]==null || config["javaHome"]== "" ) { 
                               //If the condition is true print the following statement 
                               println("The value is less than 100"); 
                               }
                  else{
                           env.JAVA_HOME = config["javaHome"]
                           println("The value is less than 200"); 
                      }
                  bat "echo ${PATH}"
                  //env.MAVEN_HOME = config["mavenHome"]
                  env.MAVEN_HOME = "/usr/share/maven"
                  bat "echo ${PATH}"
                  bat "mvn --version"
                   bat "mvn clean install"
                  //server = Artifactory.server 'Artifactory'
                 def server = Artifactory.newServer url: 'https://demohdfc123.jfrog.io/artifactory', username: "${user}", password: "${password}"
                 server.connection.timeout = 300
                 server.bypassProxy = true
                 //server.credentialsId = 'jFrogID'
                 //def server = Artifactory.server "${env.Artifactory}"
                 def buildInfo = Artifactory.newBuildInfo()
                 buildInfo.env.capture = true
                 def rtMaven = Artifactory.newMavenBuild()
                 //mvnReleaseRepo = "emvs-maven-release-virtual"
                 //mvnSnapshotRepo = "emvs-maven-snapshot-virtual"
                 mvnReleaseRepo = config['mavenReleaseRepo']
                 mvnSnapshotRepo = config['mavenSnapshotRepo']

                  

                 pipelineLogger.debug("Artifactory Maven repo : ${mvnReleaseRepo} : ${mvnSnapshotRepo}")
                 /*path = env.WORKSPACE+'/target'
                           dir (path){
                                  sh "ls -l"
                           }*/

                 rtMaven.resolver releaseRepo: mvnReleaseRepo, snapshotRepo: mvnSnapshotRepo, server: server
                 rtMaven.deployer releaseRepo: mvnReleaseRepo, snapshotRepo: mvnSnapshotRepo, server: server
                 rtMaven.deployer.deployArtifacts = true
                 //if (config['includePattern'] != ""){
                   //  rtMaven.deployer.artifactDeploymentPatterns.addInclude(config['includePattern'].toString())
                 //}
                 //if (config['excludePattern'] != ""){
                   //  rtMaven.deployer.artifactDeploymentPatterns.addExclude(config['excludePattern'].toString())
                 //}
                 def pomPath = env.WORKSPACE
                 //buildInfo = rtMaven.run pom: 'pom.xml', goals: 'clean install'
                 rtMaven.run pom: 'pom.xml', goals: config['compileArgs'], buildInfo: buildInfo
                 server.publishBuildInfo buildInfo
                 pipelineLogger.info("Maven Build completed sucessfully")

        
}
}
def gradle(Map config) {
         withCredentials([usernamePassword(credentialsId: 'artifactory_id', passwordVariable: 'password', usernameVariable: 'user')]){


                  bat "/opt/gradle-7.6/bin/gradle -v"
                  bat "echo ${user}"
                  bat "java -version"
                  bat "echo ${JAVA_HOME}"
                  bat "echo ${PATH}"
                  bat "gradle -v"
                  def server = Artifactory.newServer url: 'https://demohdfc123.jfrog.io/artifactory', username: "${user}", password: "${password}"
                 server.connection.timeout = 300
                 server.bypassProxy = true
                 def buildInfo = Artifactory.newBuildInfo()
                 buildInfo.env.capture = true
                 def rtGradle = Artifactory.newGradleBuild()
                 grdlReleaseRepo = "prarambh-gradle-snapshot-local"
                 grdlSnapshotRepo = "prarambh-gradle-remote"
                 pipelineLogger.debug("Artifactory Maven repo : ${grdlReleaseRepo} : ${grdlSnapshotRepo}")
                 rtGradle.resolver server: server, repo: 'UPI_ISSUER_Gradle_virtual'
                 rtGradle.deployer server: server, repo: 'UPI_ISSUER_Gradle_virtual'

                 rtGradle.deployer.deployArtifacts = true
                  //sh "pwd; ls -l"
                 // sh "sudo chmod -R 777 ./ "
                 //rtGradle.deployer.deployMavenDescriptors = true
                 //rtGradle.deployer.mavenCompatible = true
                 //rtGradle.useWrapper = true
                 //rtGradle.usesPlugin = true
                 rtGradle.tool = 'Gradle-7.6'
                 pipelineLogger.debug("Now starting Build Process")
                 bat "echo env.GRADLE_HOME"
                 rtGradle.run rootDir: '/opt/jnlp_jenkins_agent/JNLP/71d6017a/workspace/SUER_Adaptor-common_jfrog-gradle/', buildFile: 'build.gradle', tasks: 'clean artifactoryPublish'
                 
      
                 //server.publishBuildInfo buildInfo
                 pipelineLogger.info("Gradle Build completed sucessfully")

        //build.gradle', buildFile: 'build.gradle' /opt/jnlp_jenkins_agent/JNLP/53ad9a5b/workspace/I_ISSUER_Adptor-common_cicd-test
}
}
