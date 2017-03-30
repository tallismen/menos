#!groovy

import hudson.triggers.TimerTrigger

properties([
        buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '10', daysToKeepStr: '', numToKeepStr: '30')),
        [$class: 'ScannerJobProperty', doNotScan: false],
        pipelineTriggers([cron('@midnight')])
])

node {
    catchError {
        // Mark the code checkout 'stage'....
        stage('Checkout') {
            // Get code from a repository
            checkout scm
        }

        // Mark the code build 'stage'....
        stage ('Build') {
            withEnv(["JAVA_HOME=${ tool 'JDK 8' }", "GRADLE_OPTS=-Xmx512M", "PATH+JAVA=${env.JAVA_HOME}/bin"]) {
                def mvnHome = tool 'Maven 3.3.9'
                sh "${mvnHome}/bin/mvn -U clean install cobertura:cobertura -Dcobertura.report.format=xml -Dmaven.compiler.showDeprecation=true -Dmaven.compiler.showWarnings=true"
            }
        }

        /* Disable Sonar analysis until https://issues.jenkins-ci.org/browse/JENKINS-39346 is really solved
        if (isUserOrTimer(currentBuild.rawBuild.causes)) {
            stage ('Sonar') {
                withSonarQubeEnv('My SonarQube Server') {
                    withEnv(["JAVA_HOME=${ tool 'JDK 8' }", "GRADLE_OPTS=-Xmx1024M", "PATH+JAVA=${env.JAVA_HOME}/bin"]) {
                        // requires SonarQube Scanner for Maven 3.2+
                        def mvnHome = tool 'Maven 3.3.9'
                        sh '${mvnHome}/bin/mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.2:sonar'
                    }
                }
            }
        }
        */

        stage ('Publish') {
            step([$class: 'WarningsPublisher', consoleParsers: [[parserName: 'Java Compiler'], [parserName: 'Maven']]])
            step([$class: 'TasksPublisher', pattern: '**/*.java, **/*.xml', high: 'FIXME', normal: 'TODO'])
            step([$class: 'AnalysisPublisher', isWarningsActivated: true, isOpenTasksActivated: true])
        }

        stage ('Archive') {
            step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar,**/target/*.war,**/target/site/**,**/target/surefire-reports/*', fingerprint: true])
            step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
        }
    }
    step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: 'mlammers@anwb.nl', sendToIndividuals: true])
}

@NonCPS
def isUserOrTimer(causes) {
   echo "${causes}"
   causes.any { cause -> cause.class in [Cause.UserIdCause, TimerTrigger.TimerTriggerCause] }
}
