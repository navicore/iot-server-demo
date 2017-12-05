Akka on Kubernetes
---

See helpful [Software Mill blog post] for inspiration.

Components:

 * service for seeds StatefulSet
 * service for http ingress
 * ingress controller
 * StatefulSet for seeds
 * Deployment for kafka ingestion stream
 * Deployment for workers that also serve as http handlers for ingress service

---
[blog post]: https://medium.com/google-cloud/clustering-akka-in-kubernetes-with-statefulset-and-deployment-459c0e05f2ea
