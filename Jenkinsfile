pipeline {
    agent any // Ejecuta en cualquier agente disponible

    environment {
        // Define el nombre de la imagen usando tu usuario de Docker Hub
        DOCKER_IMAGE = "ozgamez/athletic-reportes"
    }

    stages {
        stage('Checkout') {
            steps {
                // Clona el repositorio actual
                git branch: 'main', credentialsId: 'github-ssh-key', url: 'git@github.com:Walo94/athletic-reportes.git'
            }
        }

        stage('Build Docker Image') {
            steps {
                // Construye la imagen de Docker y la etiqueta
                sh "docker build -t ${DOCKER_IMAGE}:latest ."
            }
        }

        stage('Push to Docker Hub') {
            steps {
                // Usa las credenciales de Docker Hub guardadas en Jenkins
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh "echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin"
                    sh "docker push ${DOCKER_IMAGE}:latest"
                }
            }
        }
        
        // Opcional: Desplegar automáticamente después de subir la imagen
        // stage('Deploy') {
        //     steps {
        //         sshagent(credentials: ['ssh-produccion']) {
        //             sh 'ssh user@tu-servidor "cd /ruta/proyecto && docker-compose pull backend && docker-compose up -d --no-deps backend"'
        //         }
        //     }
        // }
    }
}