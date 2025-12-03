Kubernetes Lab 4
===================

Запускать

minikube delete
minikube start

kubectl apply -f kube

kubectl port-forward svc/urlshortener-service 8080:8080