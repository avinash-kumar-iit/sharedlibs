#!/usr/bin/groovy
def GetLastCommitFiles() {
    commitHash = bat (script: "git log -n 1 --pretty=format:'%H'", returnStdout: true)
    echo "[INFO] Commit is $commitHash"
    echo "[INFO] Branch is '$env.BRANCH_NAME'"
   	filename = bat(script: "git show --name-only", returnStdout: true)
    echo "[INFO] change file name is $filename"
}

def getLastCommitID()
{ 
    echo "DEBUG: Get gitsha1/commmit_id from git repo"
    commit_id = bat(returnStdout: true, script: "git rev-parse HEAD").trim()
    echo "DEBUG: got commit_id: $commit_id"
    return commit_id
}

def setEnvVarsFromGitProperties() {

    // git basic details, required for pipeline
    env.gitUrl = scm.getUserRemoteConfigs()[0].getUrl()-"https://"
     //NEED TO SET THIS AS PER BANK
    //*********env.gitProjectName = gitUrl.split("github.com/")[1].split("/")[0].toLowerCase()************
    System.out.println("git urel---->" + env.gitUrl)
    env.gitRepoName = gitUrl.tokenize('/').last().split("\\.")[0]
	env.gitProjectName = "emvs"
    //env.gitBranchName = env.BRANCH_NAME.split("/").last()
    if (isUnix()){
        env.gitCommitHash = bat(script: "git log -n 1 --pretty=format:'%H'", returnStdout: true)
        env.gitCommitHashShort = bat(script: "git log -n 1 --pretty=format:'%h'", returnStdout: true)
    }else{
        env.gitCommitHash = bat(script: "git log -n 1 --pretty=format:'%H'", returnStdout: true)
        env.gitCommitHashShort = bat(script: "git log -n 1 --pretty=format:'%h'", returnStdout: true)
    }
}

def setAdvancedGitEnvProperties(){
    try {
        // Git commit details
        def gitCommitDate = bat (script: "git show --no-patch --no-notes --pretty='%cD' ${env.gitCommitHash} | cut -c1-25", returnStdout: true)
        def gitCommitterName = bat (script: "git show --no-patch --no-notes --pretty='%cn' ${env.gitCommitHash}", returnStdout: true)
        def gitCommitterSSO = bat (script: "git show --no-patch --no-notes --pretty='%cN' ${env.gitCommitHash}", returnStdout: true)
        def gitCommitterEmail = bat (script: "git show --no-patch --no-notes --pretty='%ce' ${env.gitCommitHash}", returnStdout: true)
        def gitCommitSubject = bat (script: "git show --no-patch --no-notes --pretty='%s' ${env.gitCommitHash}", returnStdout: true)

        env.gitCommitDate = gitCommitDate.trim()
        env.gitCommitterName = gitCommitterName.trim()
        env.gitCommitterSSO = gitCommitterSSO.trim()
        env.gitCommitterEmail = gitCommitterEmail.trim()
        env.gitCommitSubject = gitCommitSubject.trim()

        // Git URLs
        def gitRemoteUrl = scm.userRemoteConfigs[0].url 					  
        def gitUrlString = ("${gitRemoteUrl}" =~ /.*(?=\/)/)[0]
        def gitProjectUrl = "${gitUrlString}".replace("/scm/","/projects/")
        def gitBranchUrl = "${gitProjectUrl}/repos/${env.gitRepoName}/browse?at=${env.gitBranchName}"
        def gitCommitUrl = "${gitProjectUrl}/repos/${env.gitRepoName}/commits/${env.gitCommitHash}"

        env.gitProjectUrl=gitProjectUrl
        env.gitBranchUrl=gitBranchUrl
        env.gitCommitUrl=gitCommitUrl

        // Git history
        def gitDiffStat = bat (script: "git diff --stat ${env.gitCommitHash} ${env.gitCommitHash}~ | sort", returnStdout: true)
        def gitCommitGraph = bat (script: "git log --graph -5 --oneline", returnStdout: true)

        env.gitDiffStat = """$gitDiffStat"""
        env.gitCommitGraph = """$gitCommitGraph"""

        // debug logger
        pipelineLogger.debug("""
            Git variables:
                gitProjectName = $env.gitProjectName
                gitRepoName = $env.gitRepoName
                gitBranchName = $env.gitBranchName
                gitCommitHash = $env.gitCommitHash
                gitProjectUrl = $env.gitProjectUrl
        """) 
    }  catch(Exception e){
            pipelineLogger.fatal("Caught Exception, printing Stack Trace: ${e}")
    }
}
def getOptionalTagging(config) {
    def final TAGGING="gitTagging"
	def gitTagging = ""
    
    if (config.containsKey(TAGGING) && config[TAGGING] == "false" )
  	{
      return config[TAGGING]
    }
  	else
    {
      pipelineLogger.info("${gitTagging}")
      return true
    }
}
