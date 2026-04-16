## ADS on Kubernetes (local minikube)

This folder contains Kubernetes manifests to run the ADS API + PostgreSQL **locally** using `minikube`.

### Prerequisites

- Docker Desktop installed and running
- `kubectl` installed
- `minikube` installed

### 1) Start minikube

```bash
minikube start
```

### 2) Build the ADS Docker image (local)

From the `ADS/` folder:

```bash
mvn -q -DskipTests clean package
docker build -t ads-api:latest .
```

Load the image into minikube so Kubernetes can run it without pulling from a registry:

```bash
minikube image load ads-api:latest
```

### 3) Deploy Postgres + ADS

```bash
kubectl apply -f k8s/00-namespace-and-secrets.yaml
kubectl apply -f k8s/10-postgres.yaml
kubectl apply -f k8s/20-ads-api.yaml
```

Wait for pods to be ready:

```bash
kubectl -n ads get pods -w
```

### 4) Get the service URL

```bash
minikube service -n ads ads-api --url
```

The service is a NodePort (`30080`). The URL returned by the command above is the one you can submit as the "deployed solution" URL.

### 5) Smoke test

```bash
BASE_URL="$(minikube service -n ads ads-api --url)"
curl -i "${BASE_URL}/api/v1/health"
```

### Cleanup

```bash
kubectl delete namespace ads
```

