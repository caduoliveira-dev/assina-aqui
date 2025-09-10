# Docker Swarm Setup - Assina Aqui

This document explains the Docker Swarm configuration for the Assina Aqui digital signature application.

## Architecture Overview

### Services
- **Frontend Service**: Next.js application served by Nginx (3 replicas)
- **Backend Service**: Spring Boot API (3 replicas)
- **Network**: Overlay network for service communication

### Service Topology
```
┌─────────────────┐    ┌──────────────────┐
│   Load Balancer │────│   Frontend       │
│   (Docker)      │    │   (Nginx + Next) │
└─────────────────┘    └──────────────────┘
                              │
                              │ HTTP/API calls
                              ▼
                       ┌──────────────────┐
                       │   Backend        │
                       │   (Spring Boot)  │
                       └──────────────────┘
```

## Files Structure

- `Dockerfile` - Frontend container (Next.js + Nginx)
- `backend/Dockerfile` - Backend container (Spring Boot)
- `docker-compose.yml` - Swarm deployment configuration
- `nginx.conf` - Nginx configuration with backend proxy
- `deploy-swarm.sh` - Deployment script
- `test-load-balancing.sh` - Load balancing test script

## Deployment Instructions

### 1. Initialize and Deploy
```bash
# Make scripts executable
chmod +x deploy-swarm.sh test-load-balancing.sh

# Deploy the stack
./deploy-swarm.sh
```

### 2. Verify Deployment
```bash
# Check services
docker service ls

# Check service details
docker service ps assina-aqui_frontend
docker service ps assina-aqui_backend
```

### 3. Test Load Balancing
```bash
# Run load balancing tests
./test-load-balancing.sh
```

## Load Balancing Demonstration

### Frontend Load Balancing
Access the info endpoint to see different Nginx instances:
```bash
curl http://localhost/info
```

### Backend Load Balancing
Access the backend hostname endpoint through the proxy:
```bash
curl http://localhost/api/info/hostname
```

### Manual Testing
Make multiple requests to see different container IDs:
```bash
for i in {1..10}; do curl -s http://localhost/api/info/hostname | jq '.containerID'; done
```

## Service Scaling

### Scale Services
```bash
# Scale frontend to 5 replicas
docker service scale assina-aqui_frontend=5

# Scale backend to 5 replicas  
docker service scale assina-aqui_backend=5
```

### Monitor Scaling
```bash
# Watch service status
watch docker service ls

# See replica distribution
docker service ps assina-aqui_frontend
docker service ps assina-aqui_backend
```

## Network Configuration

- **Network Name**: `assina-aqui-network`
- **Driver**: overlay
- **Attachable**: true
- **Ports**: Only frontend port 80 is exposed externally

## Endpoints

- **Frontend**: http://localhost
- **Frontend Info**: http://localhost/info
- **Backend Health**: http://localhost/api/info/health
- **Backend Hostname**: http://localhost/api/info/hostname

## Cleanup

### Remove Stack
```bash
docker stack rm assina-aqui
```

### Leave Swarm (if needed)
```bash
docker swarm leave --force
```

## Key Features Demonstrated

1. **Service Discovery**: Services communicate using service names
2. **Load Balancing**: Built-in Docker Swarm load balancing
3. **Scaling**: Dynamic replica scaling
4. **Overlay Networking**: Secure service communication
5. **Rolling Updates**: Zero-downtime deployments
6. **Health Checks**: Service health monitoring

## Troubleshooting

### Check Logs
```bash
# Frontend logs
docker service logs assina-aqui_frontend

# Backend logs  
docker service logs assina-aqui_backend
```

### Debug Network
```bash
# List networks
docker network ls

# Inspect overlay network
docker network inspect assina-aqui_assina-aqui-network
```

### Service Issues
```bash
# Inspect service
docker service inspect assina-aqui_backend

# Force service update
docker service update --force assina-aqui_backend
```