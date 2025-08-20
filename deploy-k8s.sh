#!/bin/bash
# Script para desplegar la API de Notificaciones en Kubernetes

echo "🚀 Desplegando API de Notificaciones en Kubernetes..."

# Verificar que kubectl esté disponible
if ! command -v kubectl &> /dev/null; then
    echo "❌ kubectl no está instalado. Instálalo primero."
    exit 1
fi

echo "📋 Aplicando manifiestos de Kubernetes..."
kubectl apply -f k8s/

echo "⏳ Esperando que los pods estén listos..."
kubectl wait --for=condition=ready pod -l app=mysql --timeout=120s
kubectl wait --for=condition=ready pod -l app=redis --timeout=60s
kubectl wait --for=condition=ready pod -l app=notifications-api --timeout=120s

echo "✅ Despliegue completado!"
echo ""
echo "🔍 Estado de los pods:"
kubectl get pods

echo ""
echo "🌐 Servicios disponibles:"
kubectl get svc

echo ""
echo "📱 Para acceder a la aplicación:"
echo "   En cloud: kubectl get svc notifications-api-service"
echo "   En Minikube: minikube service notifications-api-service"
