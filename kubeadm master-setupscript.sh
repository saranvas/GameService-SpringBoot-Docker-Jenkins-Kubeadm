#!/bin/bash
#

set -euxo pipefail

# If you need public access to API server using the servers Public IP adress, change PUBLIC_IP_ACCESS to true.

PUBLIC_IP_ACCESS="false"
NODENAME=$(hostname -s)
POD_CIDR="192.168.0.0/16"

# Pull required images

sudo kubeadm config images pull

# Initialize kubeadm based on PUBLIC_IP_ACCESS

if [[ "$PUBLIC_IP_ACCESS" == "false" ]]; then

    # Robust private IP detection (do not assume eth1 exists)
    MASTER_PRIVATE_IP=""
    # preferred: IP used to reach the internet
    MASTER_PRIVATE_IP=$(ip route get 8.8.8.8 2>/dev/null | awk '{for(i=1;i<=NF;i++) if($i=="src"){print $(i+1); exit}}' || true)
    # fallback: first non-loopback global IPv4
    if [ -z "$MASTER_PRIVATE_IP" ]; then
      MASTER_PRIVATE_IP=$(ip -4 -o addr show scope global 2>/dev/null | awk '{print $4}' | cut -d/ -f1 | head -n1 || true)
    fi
    # fallback: hostname IP
    if [ -z "$MASTER_PRIVATE_IP" ]; then
      MASTER_PRIVATE_IP=$(hostname -I 2>/dev/null | awk '{print $1}' || true)
    fi
    # fallback: EC2 metadata (if running on EC2 and IMDS is available)
    if [ -z "$MASTER_PRIVATE_IP" ]; then
      MASTER_PRIVATE_IP=$(curl -s --connect-timeout 2 http://169.254.169.254/latest/meta-data/local-ipv4 || true)
    fi

    if [ -z "$MASTER_PRIVATE_IP" ]; then
      echo "ERROR: could not determine MASTER_PRIVATE_IP (no eth1, and fallback methods failed). Aborting."
      exit 1
    fi

    sudo kubeadm init --apiserver-advertise-address="$MASTER_PRIVATE_IP" --apiserver-cert-extra-sans="$MASTER_PRIVATE_IP" --pod-network-cidr="$POD_CIDR" --node-name "$NODENAME" --ignore-preflight-errors Swap

elif [[ "$PUBLIC_IP_ACCESS" == "true" ]]; then

    # Prefer cloud metadata for public IP (EC2). Fallback to external service.
    MASTER_PUBLIC_IP=$(curl -s --connect-timeout 2 http://169.254.169.254/latest/meta-data/public-ipv4 || true)
    if [ -z "$MASTER_PUBLIC_IP" ]; then
      MASTER_PUBLIC_IP=$(curl -s --connect-timeout 5 https://ifconfig.me || true)
    fi

    if [ -z "$MASTER_PUBLIC_IP" ]; then
      echo "ERROR: PUBLIC_IP_ACCESS=true but could not determine public IP. Aborting."
      exit 1
    fi

    sudo kubeadm init --control-plane-endpoint="$MASTER_PUBLIC_IP" --apiserver-cert-extra-sans="$MASTER_PUBLIC_IP" --pod-network-cidr="$POD_CIDR" --node-name "$NODENAME" --ignore-preflight-errors Swap

else
    echo "Error: MASTER_PUBLIC_IP has an invalid value: $PUBLIC_IP_ACCESS"
    exit 1
fi

# Configure kubeconfig

mkdir -p "$HOME"/.kube
sudo cp -i /etc/kubernetes/admin.conf "$HOME"/.kube/config
sudo chown "$(id -u)":"$(id -g)" "$HOME"/.kube/config

# Install Calico Network Plugin

kubectl apply -f https://docs.projectcalico.org/manifests/calico.yaml
