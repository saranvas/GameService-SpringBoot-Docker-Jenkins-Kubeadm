pipeline {
    agent any
    tools{
        jdk 'jdk17'
        maven 'maven3'
    }
    environment {
        SCANNER_HOME= tool 'sonar-scanner'
    }

    stages {
        stage('clean workspace'){
            steps{
                cleanWs()
            }
        }
        stage('git clone') {
            steps {
                git branch: 'main', credentialsId: 'github-token', url: 'https://github.com/saranvas/Boardgame.git'
            }
        }
        stage('compile') {
            steps {
                sh "mvn compile"
            }
        }
        stage('Testcases') {
            steps {
                sh "mvn test"
            }
        }
        stage('file system scan') {
            steps {
                sh "trivy fs --format table -o trivy-fs.html ."
            }
        }
        stage('sonarqube analysis') {
            steps {
                withSonarQubeEnv('sonar') {
                    sh ''' $SCANNER_HOME/bin/sonar-scanner -Dsonar.projectName=BoardGame -Dsonar.projectKey=BoardGame \
                           -Dsonar.java.binaries=. '''    // some block
                
                }
            }
        }
        stage('Quality Gate') {
            steps {
                script {
                    waitForQualityGate abortPipeline: false, credentialsId: 'sonar-token'
                }
            }
        }
        stage('Build') {
            steps {
                sh "mvn package"
            }
        }
        stage('publish artifact') {
            steps {
                withMaven(globalMavenSettingsConfig: 'global-settings', jdk: 'jdk17', maven: 'maven3', traceability: true) {
    // some block   
                    sh "mvn deploy"
                }
            }
        }
        stage('Build Docker image') {
            steps {
                script{
                    withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
    // some block
                        sh "docker build -t saranvas/gameservice:latest ."
                        
                    }
                }
               
            }
        }
        stage('scan image') {
            steps {
                sh "trivy image --format table -o trivy-image.html saranvas/gameservice:latest"
            }
        }
        stage('push Docker image') {
            steps {
                script{
                    withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
    // some block
                        sh "docker push saranvas/gameservice:latest"
                        
                    }
                }
               
            }
        }
        stage('deploy to k8s') {
            steps {
                withKubeConfig(caCertificate: '', clusterName: 'kubernetes', contextName: '', credentialsId: 'k8-cred', namespace: 'webapps', restrictKubeConfigAccess: false, serverUrl: 'https://172.31.128.140:6443') {
    // some block
                    sh "kubectl apply -f deployment-service.yaml"
                }
                
            }
        }
        stage('Deployment verification') {
            steps {
                withKubeConfig(caCertificate: '', clusterName: 'kubernetes', contextName: '', credentialsId: 'k8-cred', namespace: 'webapps', restrictKubeConfigAccess: false, serverUrl: 'https://172.31.128.140:6443') {
    // some block
                    sh "kubectl get pods -n webapps"
                    sh "kubectl get svc -n webapps"
                }
                
            }
        }
        
    }
    post {
    always {
        script {
            def jobName = env.JOB_NAME
            def buildNumber = env.BUILD_NUMBER
            def pipelineStatus = currentBuild.result ?: 'UNKNOWN'
            def bannerColor = pipelineStatus.toUpperCase() == 'SUCCESS' ? 'green' : 'red'

            def body = """
                <html>
                <body>
                <div style="border: 4px solid ${bannerColor}; padding: 10px;">
                <h2>${jobName} - Build ${buildNumber}</h2>
                <div style="background-color: ${bannerColor}; padding: 10px;">
                <h3 style="color: white;">Pipeline Status: ${pipelineStatus.toUpperCase()}</h3>
                </div>
                <p>Check the <a href="${BUILD_URL}">console output</a>.</p>
                </div>
                </body>
                </html>
            """

            emailext (
                subject: "${jobName} - Build ${buildNumber} - ${pipelineStatus.toUpperCase()}",
                body: body,
                to: 'saran666an@gmail.com',
                from: 'jenkins@example.com',
                replyTo: 'jenkins@example.com',
                mimeType: 'text/html',
                attachmentsPattern: 'trivy-image.html,trivy-fs.html'
            )
        }
    }
}
}
