# Assignment 4 Demo Runbook (Full Story)

## 1. Start Fresh (Clean Reset)
```powershell
kubectl delete namespace assignment-4 --ignore-not-found=true
kubectl wait --for=delete namespace/assignment-4 --timeout=240s
```

## 2. Build App Images and Load into Minikube
```powershell
docker compose build product-service order-service
minikube image load product-service:blue
minikube image load order-service:blue
```

## 3. Deploy Assignment 4 Stack
```powershell
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/prometheus-configmap.yaml
kubectl apply -f k8s/grafana-datasource-configmap.yaml

kubectl apply -f k8s/product-deployment.yaml
kubectl apply -f k8s/order-deployment.yaml
kubectl apply -f k8s/prometheus-deployment.yaml
kubectl apply -f k8s/grafana-deployment.yaml
kubectl apply -f k8s/zipkin-deployment.yaml

kubectl apply -f k8s/product-service.yaml
kubectl apply -f k8s/order-service.yaml
kubectl apply -f k8s/prometheus-service.yaml
kubectl apply -f k8s/grafana-service.yaml
kubectl apply -f k8s/zipkin-service.yaml
```

## 4. Verify Deployments (Part 1 Evidence)
```powershell
kubectl rollout status deployment/product-service -n assignment-4 --timeout=240s
kubectl rollout status deployment/order-service -n assignment-4 --timeout=240s
kubectl rollout status deployment/prometheus -n assignment-4 --timeout=240s
kubectl rollout status deployment/grafana -n assignment-4 --timeout=240s
kubectl rollout status deployment/zipkin -n assignment-4 --timeout=240s

kubectl get pods -n assignment-4 -o wide
kubectl get svc -n assignment-4
kubectl get deployments -n assignment-4
kubectl get replicasets -n assignment-4

--- OOR ---
kubectl get all -n assignment-4
```

## 5. Expose Services for Demo (Separate Terminals)
```powershell
kubectl port-forward svc/product-service 18080:8080 -n assignment-4
```
```powershell
kubectl port-forward svc/order-service 18081:8081 -n assignment-4
```
```powershell
kubectl port-forward svc/prometheus 19090:9090 -n assignment-4
```
```powershell
kubectl port-forward svc/grafana 13000:3000 -n assignment-4
```
```powershell
kubectl port-forward svc/zipkin 19411:9411 -n assignment-4
```

## 6. Generate Business Traffic
```powershell
$p = curl.exe -s -H "Content-Type: application/json" -d '{\"id\":1,\"name\":\"Laptop\",\"price\":1200.0,\"quantity\":25}' http://localhost:18080/api/products | ConvertFrom-Json
curl.exe -s -H "Content-Type: application/json" -d ("{\"productId\":1,\"quantity\":2}") http://localhost:18081/api/orders
curl.exe -s -H "Content-Type: application/json" -d ("{\"productId\":" + $p.id + ",\"quantity\":1}") http://localhost:18081/api/orders
```
```
curl.exe --request POST "http://localhost:18080/api/products" --header "Content-Type: application/json" --data-raw '{"id":1,"quantity":200,"name":"NEW-XYZ", "price": 100.00}'
```
## 7. Prometheus + Grafana (Part 2 Evidence)
```powershell
start http://localhost:19090/targets
Invoke-RestMethod -Uri "http://localhost:19090/api/v1/query?query=total_orders_total" | ConvertTo-Json -Depth 8
Invoke-RestMethod -Uri "http://localhost:19090/api/v1/query?query=sum(rate(http_server_requests_seconds_count[1m]))" | ConvertTo-Json -Depth 8
```

```text
Grafana URL: http://localhost:13000
Default login: admin / admin
```

Suggested dashboard panels:
```text
1) total_orders_total
2) sum(rate(http_server_requests_seconds_count[1m])) by (uri, status)
3) sum(jvm_memory_used_bytes{area="heap"}) by (instance)
```

## 8. Zipkin (Part 3 Evidence)
```powershell
# Verify Zipkin API is up
Invoke-RestMethod -Uri "http://localhost:19411/zipkin/api/v2/services"

# Show latest traces by service
Invoke-RestMethod -Uri "http://localhost:19411/zipkin/api/v2/traces?serviceName=orderservice&limit=5" | ConvertTo-Json -Depth 8
Invoke-RestMethod -Uri "http://localhost:19411/zipkin/api/v2/traces?serviceName=productservice&limit=5" | ConvertTo-Json -Depth 8

# Optional: dependency endpoint
$end=[DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds()
Invoke-RestMethod -Uri ("http://localhost:19411/zipkin/api/v2/dependencies?endTs={0}&lookback=600000" -f $end) | ConvertTo-Json -Depth 8

# Correlate traces with logs
kubectl logs deployment/order-service -n assignment-4 --tail=200
kubectl logs deployment/product-service -n assignment-4 --tail=200
```

## 10. Optional Cleanup
```powershell
kubectl delete namespace assignment-4
```
