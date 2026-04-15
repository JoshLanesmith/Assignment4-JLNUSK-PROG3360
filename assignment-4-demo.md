# Assignment 4 Demo Runbook (Full Story)

- Normal successful order creation = all INFO logs
- Insufficient quantity = WARN log
- Product Not Found = ERROR log

## 1. Start Fresh (Clean Reset)
```powershell
minikube start
kubectl delete -f k8s/
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

kubectl apply -f k8s/prometheus-deployment.yaml
kubectl apply -f k8s/grafana-deployment.yaml
kubectl apply -f k8s/zipkin-deployment.yaml
kubectl apply -f k8s/product-deployment.yaml
kubectl apply -f k8s/order-deployment.yaml

kubectl apply -f k8s/prometheus-service.yaml
kubectl apply -f k8s/grafana-service.yaml
kubectl apply -f k8s/zipkin-service.yaml
kubectl apply -f k8s/product-service.yaml
kubectl apply -f k8s/order-service.yaml
```

## 4. Verify Deployments (Part 1 Evidence)
```powershell
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

## 6. Generate Business Traffic with Happy Path
```powershell
# Successful Order = all INFO logs
curl.exe -s -H "Content-Type: application/json" -d '{\"id\":1,\"name\":\"Laptop\",\"price\":1200.0,\"quantity\":25}' http://localhost:18080/api/products
curl.exe -s -H "Content-Type: application/json" -d ("{\"productId\":1,\"quantity\":2}") http://localhost:18081/api/orders
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
Zipkin UI
```text
1) Navigate to http://localhost:19411 and Run Query
2) Show trace details for successful order
3) Navigate to Dependencies and Run Query
```
```powershell
# Correlate traces with logs
kubectl logs deployment/order-service -n assignment-4 --tail=200
kubectl logs deployment/product-service -n assignment-4 --tail=200
```
## 9. Show Unhappy Path Logs

```powershell
# Insufficient Quantity = WARN log
curl.exe -s -H "Content-Type: application/json" -d ("{\"productId\":1,\"quantity\":200}") http://localhost:18081/api/orders
```
```powershell
# Product Not Found  = ERROR log
curl.exe -s -H "Content-Type: application/json" -d ("{\"productId\":1,\"quantity\":200}") http://localhost:18081/api/orders
```
```powershell
# View logs with WARN and ERROR
kubectl logs deployment/order-service -n assignment-4 --tail=200
kubectl logs deployment/product-service -n assignment-4 --tail=200
```

## 10. Optional Cleanup
```powershell
kubectl delete -f k8s/
```
