#!/bin/bash

echo "=== Docker Swarm Load Balancing Test ==="
echo "Testing frontend and backend load balancing..."
echo ""

# Test frontend load balancing (nginx instances)
echo "=== Frontend Load Balancing Test ==="
echo "Making 10 requests to frontend info endpoint:"
for i in {1..10}; do
    echo -n "Request $i: "
    curl -s http://localhost/info | jq -r '.hostname' 2>/dev/null || curl -s http://localhost/info
    sleep 0.5
done

echo ""
echo "=== Backend Load Balancing Test ==="
echo "Making 10 requests to backend hostname endpoint:"
for i in {1..10}; do
    echo -n "Request $i: "
    curl -s http://localhost/api/info/hostname | jq -r '.containerID' 2>/dev/null || curl -s http://localhost/api/info/hostname
    sleep 0.5
done

echo ""
echo "=== Service Scaling Test ==="
echo "Current service status:"
docker service ls

echo ""
echo "Scaling frontend to 5 replicas..."
docker service scale assina-aqui_frontend=5

echo "Scaling backend to 5 replicas..."
docker service scale assina-aqui_backend=5

echo ""
echo "Waiting for scaling to complete..."
sleep 15

echo "Updated service status:"
docker service ls

echo ""
echo "=== Testing Load Balancing with Scaled Services ==="
echo "Making 15 requests to backend after scaling:"
for i in {1..15}; do
    echo -n "Request $i: "
    curl -s http://localhost/api/info/hostname | jq -r '.containerID' 2>/dev/null || curl -s http://localhost/api/info/hostname
    sleep 0.3
done

echo ""
echo "=== Load Balancing Test Complete ==="
echo "You should see different container IDs/hostnames indicating load balancing is working."
