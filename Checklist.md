Here’s a **clean, logical demo checklist** you can literally follow step-by-step during your presentation to make sure you hit **everything needed for full marks**. I’ve ordered it exactly how a smooth demo should flow (setup → logs → metrics → tracing → wrap-up).

---

# ✅ **FULL DEMO CHECKLIST (ORDERED FOR MAX MARKS)**

Based directly on your assignment requirements 

---

## 🔷 1. Start: Show System is Running (Foundation)

☐ Minikube cluster is running
☐ Both services deployed:

* order-service
* product-service

☐ Monitoring stack deployed:

* Prometheus
* Grafana
* Zipkin

☐ (Optional but strong) Show `kubectl get pods` to prove everything is UP

---

## 🔷 2. Logging Demo (Part 1 – 25 Marks)

### ✅ 2.1 Show Logs from BOTH Services

☐ Run:

```
kubectl logs deployment/order-service
kubectl logs deployment/product-service
```

☐ Show each service has:

* ☐ INFO log
* ☐ WARN log
* ☐ ERROR log

---

### ✅ 2.2 Show Meaningful Log Messages

☐ Logs include **real data values**, NOT generic messages
Examples to show:

* order ID
* product ID
* price
* URL call

✔ Must prove **business events are meaningful** 

---

### ✅ 2.3 Show Correlation ID Working

☐ Show `correlationId` in logs

☐ Trigger ONE request and show:

* ☐ Same correlationId in order-service
* ☐ Same correlationId in product-service

✔ This proves cross-service tracing of logs

---

### ✅ 2.4 (Important for Full Marks)

☐ Confirm **correlationId propagates between services**
☐ Mention MDC + header (`X-Correlation-Id`) briefly

---

## 🔷 3. Metrics Demo (Part 2 – 25 Marks)

### ✅ 3.1 Show Actuator Endpoint

☐ Open:

```
/actuator/prometheus
```

☐ Show:

* ☐ Raw metrics output
* ☐ Your **custom metric visible**

---

### ✅ 3.2 Show Prometheus is Scraping

☐ Open Prometheus UI

☐ Show:

* ☐ Targets page
* ☐ Both services = **UP**

---

### ✅ 3.3 Show PromQL Query

☐ Run query for your custom metric

☐ Show it returns real data

---

### ✅ 3.4 Show Grafana Setup

☐ Open Grafana

☐ Show:

* ☐ Data source configured
* ☐ “Save & Test” successful

---

### ✅ 3.5 Show Dashboard (VERY IMPORTANT)

Dashboard name:
☐ **E-Commerce Services Overview**

Must show ALL 4 panels:
☐ JVM Heap Memory
☐ HTTP Request Rate
☐ Custom Business Metric
☐ Pod CPU / Memory

✔ All panels must show **live data (not empty)** 

---

### ✅ 3.6 Show Alert Rule

☐ Open alert configuration

☐ Show:

* ☐ Threshold
* ☐ Evaluation interval
* ☐ “For” duration

☐ (Bonus) Show alert in pending/firing state

---

## 🔷 4. Distributed Tracing Demo (Part 3 – 25 Marks)

### ✅ 4.1 Show Zipkin UI

☐ Open Zipkin

☐ Show:

* ☐ A trace

---

### ✅ 4.2 Show Cross-Service Trace

☐ One request should show:

* ☐ order-service span
* ☐ product-service span

✔ Must be **same trace**

---

### ✅ 4.3 Show Custom Span

☐ Highlight your custom span
(e.g., product-service call or business logic)

---

### ✅ 4.4 Show Dependency Graph

☐ Show:

* order-service → product-service relationship

---

### ✅ 4.5 Show Logs with traceId

☐ Go back to logs

☐ Show:

* ☐ traceId
* ☐ correlationId
  in SAME log line

✔ This connects logs + tracing

---

## 🔷 5. (BONUS – +5 Marks) Tie Everything Together

☐ Pick ONE request and show ALL:

* ☐ Logs (with correlationId + traceId)
* ☐ Zipkin trace
* ☐ Prometheus metric increment
* ☐ Grafana dashboard update

✔ This is the **highest-impact demo moment**

---

## 🔷 6. Explanation (Part 4 Requirement)

☐ Briefly explain:

* ☐ Logs → what happened
* ☐ Metrics → system health & trends
* ☐ Traces → request flow across services

☐ Explain how they **work together (observability)**

✔ Required for full video marks 

---

# 🧠 Pro Tips (What Gets You “Awesome” 18–25)

From rubric (page 6):

* EVERYTHING must be shown **live**, not static
* Data must be **real (not placeholder)**
* Correlation must work **across services**
* Dashboard must be **fully populated**
* Tracing must show **complete flow**

---

# ✅ Quick Final “Did I Cover Everything?” Checklist

If you can say YES to all → you’re in full marks territory:

☐ Logs: all 3 levels in both services
☐ Logs: meaningful + correlationId across services
☐ Metrics: custom metric visible everywhere
☐ Prometheus: both services UP
☐ Grafana: 4 working panels + alert
☐ Zipkin: full trace + custom span
☐ Logs include traceId
☐ Explained observability clearly

---

If you want, I can turn this into a **1-page printable cheat sheet** or a **script you can literally read during demo**.
