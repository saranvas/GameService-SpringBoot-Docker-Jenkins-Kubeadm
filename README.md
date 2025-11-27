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

- Spun up 3 EC2 machines and one is for control plane and other two as worker nodes and installed and configured **containerd runtime** and installed **kubeadm, kubectl, kubelet**.
- Run *kubeadm init* command in control plane and generate the *kubeadm join* command, then run that command on both the worker nodes to make a cluster.

- Install **Calico networkplugin** in control node which is the CNI(container network interface) which handle the cluster networking.
  
- Created the webapps namespace using *kubectl create namespace webapps*.

- Defined a minimal **Role** (role.yml) that grants permissions to manage Deployments, Services, Pods, and ConfigMaps within webapps.

- Created the **ServiceAccount** (serviceaccount.yml) named jenkins, which Jenkins uses to authenticate to the cluster.

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

- Showcasing reference of the **AWS EC2 instances**, security groups, and supporting components that form the core infrastructure behind the Kubernetes cluster and CI/CD pipeline.
<img width="1361" height="680" alt="Screenshot 2025-11-27 023920" src="https://github.com/user-attachments/assets/1b843301-adb6-4237-9042-7c242c685c34" />
<img width="1355" height="676" alt="Screenshot 2025-11-27 023826" src="https://github.com/user-attachments/assets/4204daf2-653a-47c9-8dc6-3373af391260" />



- This image displays the Jenkins job dashboard for the project, including the latest successful build artifacts, overall build status, and the test result trend graph. It confirms that all unit tests passed consistently across multiple builds.

<img width="561" height="493" alt="Screenshot 2025-11-27 024053" src="https://github.com/user-attachments/assets/a56cb9ac-8212-47df-99cf-6e2cc287bb6d" />

- This screenshot shows the Nexus Repository Manager storing the generated **Maven snapshot artifacts** for the project. It confirms that Jenkins successfully published the application JAR and metadata into the maven-snapshots repository during the CI pipeline.

<img width="555" height="578" alt="Screenshot 2025-11-27 023950" src="https://github.com/user-attachments/assets/a162e715-734b-46aa-992f-6a6a7620a7cc" />

- This image displays the **SonarQube issue analysis** for the project, highlighting **code smells**, **bugs**, and **vulnerabilities** detected during the pipeline scan. It demonstrates that **static code analysis** is integrated into the CI workflow and provides actionable insights for improving code quality.

<img width="14174" height="4352" alt="Code Analysis (2)" src="https://github.com/user-attachments/assets/e26db119-152d-4003-8a9f-be83ca429e03" />

- This screenshot shows the home page of the BoardGame web application running on the Kubernetes cluster. Although the Service is defined as a **LoadBalancer**, the **self-hosted kubeadm cluster** does not have MetalLB or any external load balancer provider configured, so no external IP was assigned. As a result, the application was accessed using the worker node’s public IP along with the allocated port (32377). This confirms that the application is deployed correctly and reachable through the service on the node.

<img width="1124" height="430" alt="Screenshot 2025-11-27 113710" src="https://github.com/user-attachments/assets/6617aa87-8301-4ef1-9959-5c44e7cd77b2" />

This screenshot shows the Kubernetes verification commands executed on the master node. It includes:

- Running pods of the gameservice deployment.

- The LoadBalancer-based service (gameservice-ssvc) exposing port 32377.

- The Deployment rollout status for 3 replicas.

- The created Role (app-role), ServiceAccount (jenkins), and RoleBinding (app-rolebinding) in the webapps namespace.



<img width="562" height="421" alt="Screenshot 2025-11-27 113827" src="https://github.com/user-attachments/assets/df6236a9-ea3c-4ba0-bd6d-3fe06549bb70" />

- This screenshot shows the **automated email notification** sent by Jenkins after a successful pipeline run. The email includes the build name, status badge, and direct link to the console output, along with attached Trivy scan reports. This confirms that post-build notifications and reporting are fully integrated into the CI/CD pipeline.

<img width="15516" height="3984" alt="Code Analysis (3)" src="https://github.com/user-attachments/assets/75127bf2-41e0-4644-9d20-541ce8cf9baa" />

- This screenshot shows Prometheus successfully scraping multiple **Blackbox Exporter endpoints**. These probes continuously check the availability and response time of the **application URLs** running on the Kubernetes cluster. Since the cluster uses a LoadBalancer service without MetalLB, the targets are monitored using the worker node’s public IP and the assigned port (32377). All targets are in the UP state, confirming that the web application is reachable and healthy from Prometheus.

<img width="9560" height="3482" alt="Code Analysis (1)" src="https://github.com/user-attachments/assets/1193465a-a8d9-4f9b-8e99-f1dadaab322a" />

  <img width="13415" height="3440" alt="Code Analysis (5)" src="https://github.com/user-attachments/assets/3dca2660-ff8a-4ecc-b5b7-877bfba81d4e" />

  - These screenshots show the complete monitoring setup for the application using **Prometheus**, **Blackbox** **Exporter**, and **Grafana**. The Grafana dashboard visualizes **real-time probe status, HTTP duration, DNS lookup time**, and **probe** **latency** for the **application endpoints** exposed through the Kubernetes LoadBalancer service. Prometheus continuously scrapes these targets and reports their health, while the raw Blackbox Exporter UI shows recent probe results, confirming both successful and failed checks. Together, these views validate that external uptime monitoring is fully configured and actively verifying the availability and performance of the deployed application.

<img width="1122" height="548" alt="Screenshot 2025-11-27 023448" src="https://github.com/user-attachments/assets/62cf4d54-2925-4da8-b97d-f2b23c8be15f" />

<img width="14570" height="3393" alt="Code Analysis (2)" src="https://github.com/user-attachments/assets/dba0b1b3-55b0-4444-b910-2b2295189576" />

These screenshots show the system-level monitoring setup implemented using **Prometheus Node Exporter** and **Grafana**. The Node Exporter endpoint exposes detailed **CPU**, **memory**, **disk**, and **network statistics from the server**, while the Grafana dashboards visualize this **data in real time**. The charts display CPU load, RAM usage, disk utilization, network throughput, and overall node health for the Jenkins server. This confirms that low-level infrastructure monitoring is fully configured, allowing continuous visibility into resource utilization and system performance of the Jenkins server.

  





