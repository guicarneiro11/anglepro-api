# 📊 AnglePro API

API desenvolvida em Kotlin com Ktor para geração e envio de relatórios em PDF para aplicativo de avaliação fisioterapêutica.

## 🚀 Tecnologias Utilizadas

![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Ktor](https://img.shields.io/badge/Ktor-FF6F00?style=for-the-badge&logo=kotlin&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Google Cloud](https://img.shields.io/badge/Google_Cloud-4285F4?style=for-the-badge&logo=google-cloud&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)

## 📋 Descrição

API responsável por gerar relatórios em PDF dos pacientes a partir dos dados armazenados no Firebase Firestore. A API é integrada com serviço de e-mail para envio automático dos relatórios gerados.

## 🛠️ Funcionalidades

- ✅ Autenticação com Firebase
- ✅ Geração de PDF com dados do paciente
- ✅ Envio de e-mail com relatório em anexo
- ✅ Integração com Firebase Firestore
- ✅ CORS configurado para segurança
- ✅ Containerização com Docker
- ✅ Deploy automatizado no Google Cloud Run

## 🔒 Segurança

- Autenticação via Firebase Authentication
- Bearer Token validation
- CORS configurado para requisições seguras
- Credenciais seguras via variáveis de ambiente

## 📡 Endpoints

### Gerar e Enviar Relatório
```http
GET /api/users/{userId}/patients/{patientId}/send-pdf?email={email}
```

#### Parâmetros
- `userId`: ID do usuário no Firebase
- `patientId`: ID do paciente
- `email`: E-mail para envio do relatório

#### Headers necessários
```
Authorization: Bearer {firebase_token}
```

#### Respostas
- `200`: PDF gerado e enviado com sucesso
- `400`: Parâmetros inválidos ou faltantes
- `401`: Não autorizado
- `404`: Paciente não encontrado
- `500`: Erro interno do servidor

## 🏗️ Arquitetura

```
src/
├── Application.kt          # Configuração principal
├── Security.kt            # Configuração de autenticação
├── Serialization.kt       # Configuração de serialização JSON
├── Routing.kt            # Definição de rotas
└── HTTP.kt               # Configuração de CORS e Headers
```

## 🚀 Deploy

O deploy é realizado automaticamente no Google Cloud Run através de container Docker.

### Dockerfile
```dockerfile
FROM gradle:8-jdk17 as build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle dependencies --no-daemon
RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/ktor-app-fat.jar /app/ktor-app.jar
COPY firebase-credentials.json /app/firebase-credentials.json
ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/ktor-app.jar"]
```

## ⚙️ Configuração Local

1. Clone o repositório
```bash
git clone https://github.com/seu-usuario/anglepro-api.git
```

2. Configure as credenciais do Firebase
```bash
cp firebase-credentials.example.json firebase-credentials.json
# Adicione suas credenciais no arquivo
```

3. Build com Docker
```bash
docker build -t anglepro-api .
```

4. Execute o container
```bash
docker run -p 8080:8080 anglepro-api
```

## 📦 Dependências Principais

- Ktor: Framework web em Kotlin
- Firebase Admin SDK: Integração com Firebase
- iText: Geração de PDFs
- Apache Commons Email: Envio de e-mails
- Kotlin Serialization: Serialização JSON

## 🔍 Monitoramento

- Logs disponíveis no Google Cloud Console
- Métricas de performance via Cloud Run
- Rastreamento de erros e exceções

## 🤝 Integração

Esta API é parte do ecossistema AnglePro, integrada com:
- Aplicativo Android AnglePro
- Firebase Authentication
- Firebase Firestore
- Google Cloud Platform

## 👨‍💻 Autor

[Guilherme Carneiro](https://github.com/guicarneiro11)
