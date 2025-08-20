# GitHub Actions Secrets Configuration

Para que el workflow de CI/CD funcione correctamente, necesitas configurar los siguientes secrets en tu repositorio de GitHub:

## Configuración de Secrets

Ve a tu repositorio en GitHub → Settings → Secrets and variables → Actions

### Secrets requeridos:

1. **DOCKERHUB_USERNAME**
   - Tu nombre de usuario de DockerHub
   - Ejemplo: `alejandro123`

2. **DOCKERHUB_TOKEN**
   - Token de acceso de DockerHub (no la contraseña)
   - Ve a DockerHub → Account Settings → Security → New Access Token

3. **KUBECONFIG_DATA**
   - Tu configuración de kubectl codificada en base64
   - Obtén tu kubeconfig: `kubectl config view --raw --minify`
   - Codifica en base64: `cat ~/.kube/config | base64 -w 0`

## Configuración de DockerHub

1. Crea una cuenta en DockerHub si no tienes una
2. Crea un repositorio público llamado `notifications-api`
3. Genera un Access Token en Security settings

## Configuración de Kubernetes

El workflow está configurado para desplegar automáticamente cuando hay push a la rama `main`.

### Para clusters cloud (AWS EKS, GCP GKE, Azure AKS):
- Configura el KUBECONFIG_DATA con las credenciales de tu cluster

### Para desarrollo local con Minikube:
- Puedes comentar temporalmente el job de `deploy` hasta tener un cluster production

## Ramas configuradas

El workflow se ejecuta en:
- **Push a main/develop**: Ejecuta tests, build y deploy (solo main)
- **Pull requests**: Ejecuta tests y build (sin deploy)

## Funcionalidades incluidas

✅ Tests unitarios e integración
✅ Cobertura de código con JaCoCo
✅ Build multi-arquitectura (AMD64/ARM64)
✅ Cache de Maven para builds más rápidos
✅ Deploy automático a Kubernetes
✅ Verificación del despliegue
