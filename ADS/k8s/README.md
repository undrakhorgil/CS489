# ADS on local Minikube (Kubernetes)

This folder deploys the same stack as `ADS/docker-compose.yml`: **PostgreSQL → Spring Boot API → nginx + React** (nginx proxies `/api/` to the backend).

## Prerequisites

- [Minikube](https://minikube.sigs.k8s.io/docs/start/) installed and running (`minikube start`).
- `kubectl` with the Minikube context selected (`kubectl config use-context minikube`).
- Enough resources (e.g. `minikube start --memory=4096`).

### Troubleshooting: `connect: connection refused` / OpenAPI download failed

`kubectl` talks to the **Kubernetes API** (often `https://127.0.0.1:<port>` for Minikube). If you see:

`failed to download openapi: Get "https://127.0.0.1:....": dial tcp ...: connect: connection refused`

the API server is **not reachable**: the cluster is stopped, or `kubectl` is using an **old** kubeconfig entry from a previous Minikube run.

1. **Start Minikube** (if you use it):

   ```bash
   minikube status
   minikube start
   ```

2. **Use the right context** and verify the API answers:

   ```bash
   kubectl config get-contexts
   kubectl config use-context minikube
   kubectl cluster-info
   ```

   `kubectl cluster-info` should print a reachable control plane URL, not refuse the connection.

3. **If the context still points at a dead port**, refresh Minikube’s kubeconfig:

   ```bash
   minikube update-context
   ```

4. **`--validate=false`** only skips client-side OpenAPI validation. It does **not** fix a stopped cluster; `apply` will still fail if the API server is down.

If you use **Docker Desktop Kubernetes** instead of Minikube:

1. Open **Docker Desktop** → **Settings** (gear) → **Kubernetes**.
2. Turn **Enable Kubernetes** **on**, click **Apply & restart**, and wait until the whale menu shows **Kubernetes running** (green).
3. Confirm:

   ```bash
   kubectl config use-context docker-desktop
   kubectl cluster-info
   ```

If you see `kubernetes.docker.internal:6443` … **connection refused**, the API is not up yet (still starting) or Kubernetes is still **disabled**. `kubectl` cannot talk to the cluster until that finishes.

**Alternative:** use **Minikube** instead (`minikube start`, then `kubectl config use-context minikube`) so you are not tied to Docker Desktop’s embedded cluster.

## 1. Build images Minikube can use

Minikube’s cluster usually cannot see images built only on your host Docker daemon. Pick **one** approach:

### A. Build inside Minikube’s Docker (simple)

```bash
eval $(minikube docker-env)
docker build -t ads-backend:local -f ADS/ads-backend/Dockerfile ADS/ads-backend
docker build -t ads-frontend:local -f ADS/ads-frontend/Dockerfile ADS/ads-frontend
eval $(minikube docker-env -u)
```

Manifests use `imagePullPolicy: IfNotPresent` and tags `ads-backend:local` / `ads-frontend:local`.

### B. Build on host and load into Minikube

```bash
docker build -t ads-backend:local -f ADS/ads-backend/Dockerfile ADS/ads-backend
docker build -t ads-frontend:local -f ADS/ads-frontend/Dockerfile ADS/ads-frontend
minikube image load ads-backend:local
minikube image load ads-frontend:local
```

## 2. Apply manifests

From the **repository root** (the directory that contains `ADS/`):

```bash
kubectl apply -k ADS/k8s
```

Or from **inside `ADS/`**:

```bash
kubectl apply -k k8s
```

Wait until pods are ready:

```bash
kubectl get pods -n ads -w
```

## 3. Open the UI

### Option A — Port forward (matches CORS defaults)

CORS in the backend already allows `http://localhost:8088`. Use:

```bash
kubectl port-forward -n ads service/ads-ui 8088:80
```

Then open **http://localhost:8088** (same as Docker Compose).

### Option B — NodePort (default `30088`)

```bash
minikube service -n ads ads-ui --url
```

Use the printed URL. If the browser blocks API calls due to CORS, add that origin to the backend deployment env `ADS_CORS_ALLOWED_ORIGINS` (comma-separated), or prefer **port-forward** above.

## 4. Optional: API from your machine

```bash
kubectl port-forward -n ads service/ads-backend 8080:8080
```

Then `http://localhost:8080/api/v1/health`.

## 5. Teardown

```bash
kubectl delete -k ADS/k8s
```

The Postgres PVC is deleted with the stack unless you keep it; to wipe data remove the PVC manually if needed.

## Secrets

- `postgres-secret` — DB user/password (demo defaults).
- `ads-app-secret` — JWT signing key; edit before any real use.

For production, use sealed-secrets, external secret managers, or Helm values—not committed plain secrets.
