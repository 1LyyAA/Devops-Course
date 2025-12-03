Kubernetes Lab 4
===================

Запускать

minikube delete
minikube start

# Собрать и загрузить образ в minikube
docker build -t urlshortener:latest -f dockerfile .
minikube image load urlshortener:latest

# Применить манифесты
kubectl apply -f kube

# Открыть сервис в браузере
minikube service urlshortener-service


<!-- kubectl port-forward svc/urlshortener-service 8080:8080 -->