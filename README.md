<h1>OVERVIEW</h1>

This project delivers a complete CI/CD flow for a Spring Boot application using Jenkins, Docker, Trivy scanning, and Kubernetes (kubeadm-based) deployment. The Jenkins pipeline automates every stage of the build lifecycle: workspace cleanup, Git checkout, Maven compilation, unit testing, SonarQube code quality checks, artifact publishing, container image creation, and vulnerability scanning. After producing and pushing the Docker image, the pipeline deploys the application directly to a Kubernetes cluster using kubectl apply, followed by automated deployment verification.

The deployment runs three replicas of the service behind a Kubernetes LoadBalancer. The container image is built through a multi-stage Dockerfile, ensuring optimized build caching and a lightweight runtime image based on Eclipse Temurin JRE 17.

To complement the CI/CD and deployment pipeline, the system includes real-time infrastructure and service monitoring using

**Node Exporter** → system-level metrics (CPU, memory, disk, network)

**Blackbox Exporter** → HTTP & endpoint availability checks

This provides end-to-end visibility of node performance, service uptime, and potential failures across the environment.

Overall, the project demonstrates practical DevOps concepts: automated builds, code quality enforcement, secure containerization, vulnerability scanning, registry integration, Kubernetes deployment, and monitoring — representing a clean, end-to-end workflow from source code to running workload.
