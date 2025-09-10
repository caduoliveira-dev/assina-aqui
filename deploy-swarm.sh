#!/bin/bash

echo "=== Docker Swarm Deployment Script for Assina Aqui ==="

# Check if Docker Swarm is initialized
if ! docker info | grep -q "Swarm: active"; then
    echo "Initializing Docker Swarm..."
    docker swarm init
else
    echo "Docker Swarm is already active"
fi

# Build the images
echo "Building frontend image..."
docker build -t assina-aqui-frontend:latest .

echo "Building backend image..."
docker build -t assina-aqui-backend:latest ./backend

# Deploy the stack
echo "Deploying the Assina Aqui stack..."
docker stack deploy -c docker-compose.yml assina-aqui

# Show deployment status
echo "Waiting for services to start..."
sleep 10

echo "=== Service Status ==="
docker service ls

echo "=== Frontend Service Details ==="
docker service ps assina-aqui_frontend

echo "=== Backend Service Details ==="
docker service ps assina-aqui_backend

echo "=== Network Details ==="
docker network ls | grep assina-aqui

echo "=== Stack deployed successfully! ==="
echo "Frontend available at: http://localhost"
echo "Backend info endpoint: http://localhost/api/info/hostname"
echo ""
echo "To scale services:"
echo "  docker service scale assina-aqui_frontend=5"
echo "  docker service scale assina-aqui_backend=5"
echo ""
echo "To remove the stack:"
echo "  docker stack rm assina-aqui"
