<h1>OVERVIEW</h1>

This project delivers a complete CI/CD flow for a Spring Boot application using Jenkins, Docker, Trivy scanning, and Kubernetes (kubeadm-based) deployment. The Jenkins pipeline automates every stage of the build lifecycle: workspace cleanup, Git checkout, Maven compilation, unit testing, SonarQube code quality checks, artifact publishing, container image creation, and vulnerability scanning. After producing and pushing the Docker image, the pipeline deploys the application directly to a Kubernetes cluster using kubectl apply, followed by automated deployment verification.

The deployment runs three replicas of the service behind a Kubernetes LoadBalancer. The container image is built through a multi-stage Dockerfile, ensuring optimized build caching and a lightweight runtime image based on Eclipse Temurin JRE 17.

To complement the CI/CD and deployment pipeline, the system includes real-time infrastructure and service monitoring using

**Node Exporter** → system-level metrics (CPU, memory, disk, network)

**Blackbox Exporter** → HTTP & endpoint availability checks

This provides end-to-end visibility of node performance, service uptime, and potential failures across the environment.

Overall, the project demonstrates practical DevOps concepts: automated builds, code quality enforcement, secure containerization, vulnerability scanning, registry integration, Kubernetes deployment, and monitoring — representing a clean, end-to-end workflow from source code to running workload.

<h1>ARCHITECTURE DIAGRAM</h1>

<img width="4563" height="2124" alt="Code Analysis (1)" src="https://github.com/user-attachments/assets/4d2d2a16-839b-452a-aa42-1de61a747679" />

- Spun up 3 EC2 machines and one is for control plane and other two as worker nodes and installed and configured containerd runtime and installed kubeadm, kubectl, kubelet/

- Run kubeadm init command in control plane and generate the kubeadm join command, then run that command on both the worker nodes to make a cluster.

- Install Calico networkplugin in control node which is the CNI(container network interface) which handle the cluster networking.
  
- Created the webapps namespace using kubectl create namespace webapps.

- Defined a minimal Role (role.yml) that grants only the required permissions to manage Deployments, Services, Pods, and ConfigMaps within webapps.

- Created the ServiceAccount (serviceaccount.yml) named jenkins, which Jenkins uses to authenticate to the cluster.

- Bound the Role and ServiceAccount using binding.yml, giving the jenkins ServiceAccount scoped permissions in the webapps namespace.

- Generated the API token via secret.yml, and used it to build a kubeconfig stored securely in Jenkins as the credential k8-cred.

- Configured Jenkins to deploy using the ServiceAccount kubeconfig, ensuring no secrets or kubeconfigs are stored in the repository.

<h1>Tools & Technologies Used</h1>

<h2>Backend & Build</h2>

- **Java 17**
- **Spring Boot**
- **Maven 3**

<h2>CI/CD</h2>

- **Jenkins** (pipeline + email notifications)
- **SonarQube** (code quality & quality gate)
- **Trivy** (filesystem + container image vulnerability scanning)

<h2>Containerization</h2>

- **Docker** (multi-stage image build)
- **Docker Hub** (container registry)

<h2>Kubernetes</h2>

- **kubeadm** (cluster setup)
- **kubectl** (deployment & verification)
- **Kubernetes Deployment & Service manifests** (deployment-service.yaml)
- **ServiceAccount / Role / RoleBinding** (serviceaccount.yml, role.yml, binding.yml)

<h2>Monitoring</h2>

- **Node Exporter** (system-level metrics)
- **Blackbox Exporter** (HTTP/endpoint monitoring)
- **Prometheus** (metrics scraping)
- **Grafana** (dashboarding)

<h2>Version Control</h2>

- **Git + GitHub**

<h1>Runtime Environment & System Output</h1>
<img width="1364" height="521" alt="Screenshot 2025-11-27 024139" src="https://github.com/user-attachments/assets/fc0c82b7-9114-4bee-9f8e-b7faeddea567" />
<img width="1355" height="577" alt="Screenshot 2025-11-27 024611" src="https://github.com/user-attachments/assets/1402286e-3224-414b-9151-b0546abecd40" />

- Showcasing reference of the AWS EC2 instances, security groups, and supporting components that form the core infrastructure behind the Kubernetes cluster and CI/CD pipeline.
<img width="1361" height="680" alt="Screenshot 2025-11-27 023920" src="https://github.com/user-attachments/assets/1b843301-adb6-4237-9042-7c242c685c34" />
<img width="1355" height="676" alt="Screenshot 2025-11-27 023826" src="https://github.com/user-attachments/assets/4204daf2-653a-47c9-8dc6-3373af391260" />



- This image displays the Jenkins job dashboard for the project, including the latest successful build artifacts, overall build status, and the test result trend graph. It confirms that all unit tests passed consistently across multiple builds.

<img width="1361" height="493" alt="Screenshot 2025-11-27 024053" src="https://github.com/user-attachments/assets/a56cb9ac-8212-47df-99cf-6e2cc287bb6d" />

- This screenshot shows the Nexus Repository Manager storing the generated Maven snapshot artifacts for the project. It confirms that Jenkins successfully published the application JAR and metadata into the maven-snapshots repository during the CI pipeline.

<img width="555" height="578" alt="Screenshot 2025-11-27 023950" src="https://github.com/user-attachments/assets/a162e715-734b-46aa-992f-6a6a7620a7cc" />

- This image displays the SonarQube issue analysis for the project, highlighting code smells, bugs, and vulnerabilities detected during the pipeline scan. It demonstrates that static code analysis is integrated into the CI workflow and provides actionable insights for improving code quality.




